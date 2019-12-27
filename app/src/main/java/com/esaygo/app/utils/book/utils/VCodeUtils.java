package com.esaygo.app.utils.book.utils;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.esaygo.app.utils.book.bean.VCode;
import com.esaygo.app.utils.book.vcode.IdentifyVCode;
import com.esaygo.app.utils.network.support.ApiConstants;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.CoderResult;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 验证码处理类
 */
public class VCodeUtils {
    //   private static Logger logger = Logger.getLogger(VCodeUtils.class.getName());
    private static String tag = VCodeUtils.class.getName();

    /**
     * 获取验证码的方法
     */
    public static VCode getVCode(OkHttpClient okClient) {
        // 准备参数
        VCode vcode = new VCode();
        vcode.setTimestamp("" + System.currentTimeMillis());
        // 生成检查时间戳的回调参数 格式为jQuery1910XXXXXXXXXXXXXXXX
        Random r = new Random();
        StringBuffer cbpara = new StringBuffer("jQuery1910");
        for (int i = 0; i < 16; i++) {
            cbpara.append(r.nextInt(9));
        }
        vcode.setCallbackParameter(cbpara.toString() + "_" + vcode.getTimestamp());
        Request request = new Request.Builder()
                .url("https://kyfw.12306.cn/passport/captcha/captcha-image64?login_site=E&module=login&rand=sjrand&" + vcode.getTimestamp()  +"=&callback="+vcode.getCallbackParameter()+"&_=" + vcode.getTimestamp())
                .addHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400")
                .addHeader("Host", "kyfw.12306.cn")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .build();

        Call call = okClient.newCall(request);
        //3 执行Call，得到response
        Response response = null;
        try {
            response = call.execute();
            if (response.isSuccessful()) {
                String VCodeBase64Str = FormatUtils.getVCodeBase64Str(response.body().string());
                vcode.setVcode(VCodeBase64Str);
                vcode.setOkHttpClient(okClient);
            } else {
                vcode.setVcode("");
                vcode.setOkHttpClient(okClient);
            }
        } catch (Exception e) {
            Log.i(tag, "请求失败，错误信息:[" + e.toString() + "]");
            return vcode;
        }
        return vcode;
    }

    public static String markVCode(String imageBase64) {
        Log.i(tag, "正在识别验证码...");
        //将验证码base64上传服务器验证
        String result = "";

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost checkTokentPost = new HttpPost(ApiConstants.POST_CHECK_CODE_FROM_MY_SERVER);

        // 设置Post表单实体
        Map<String, String> form = new HashMap<String, String>();
        form.put("imageFile", imageBase64);
        checkTokentPost.setEntity(FormatUtils.setPostEntityFromMap(form));
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(checkTokentPost);
            //System.out.println(response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == 200) {
                Codeesult codeesult = JSONObject.parseObject(EntityUtils.toString(response.getEntity()), Codeesult.class);
                if (codeesult.code == 0) {
                    Log.i(tag, "识别服务器返回..." + codeesult.data.toString());
                    result = SimulatedClick(codeesult.data);
                    Log.i(tag, "打码为..." + result);
                } else {
                    Log.i(tag, "登录错误，请稍后重试...");

                }
            }
        } catch (Exception e) {
            Log.i(tag, "网络错误，错误信息:[" + e.toString() + "]");
            return "";
        }
        return result;
    }


    public static class Codeesult {
        public int code;
        public String message;
        public String[] data;

    }

    public static OkHttpClient checkVCode(VCode vcode, String result) {
        Log.i(tag, "正在校验验证码...");
        //System.out.println(result);

        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("callback", vcode.getCallbackParameter());
        formBody.add("answer", result);
        formBody.add("rand", "sjrand");
        formBody.add("login_site", "E");
        formBody.add("_",  System.currentTimeMillis()+"");

        Request request = new Request.Builder()
                .url(ApiConstants.checkVCode)
                .addHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400")
                .addHeader("Host", "kyfw.12306.cn")
                .addHeader("Origin", "https://kyfw.12306.cn")
                .addHeader("Referer", "https://kyfw.12306.cn/otn/resources/login.html")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .post(formBody.build())
                .build();

        Call call = vcode.getOkHttpClient().newCall(request);
        //3 执行Call，得到response
        Response response = null;
        try {
            //请求校验验证码
            response = call.execute();
            if (response.isSuccessful()) {
                String repString=response.body().string();
                JSONObject jsonObject = JSONObject.parseObject(repString.substring(repString.indexOf("(") + 1, repString.length() - 2));
                //返回JSON中result_code的值如果等于4，说明验证成功，则返回绑定Cookie的客户端实例
                //因为Cookie中有jsessionid的值，订票的时候需要用到
                if (jsonObject.getString("result_code").equals("4")) {
                    Log.i(tag, "验证码验证成功...");
                    return vcode.getOkHttpClient();
                } else {
                    Log.i(tag, "验证码校验失败，正在重新请求...");
                    return null;
                }

            }
        } catch (Exception e) {
            Log.i(tag, "校验码校验异常，错误信息:[" + e.toString() + "]");
            e.printStackTrace();
            return null;
        }
        return null;

    }


    //模拟用户点击验证码
    public static String SimulatedClick(String[] codeList) {
        Log.i(tag, "[simulatedClick]模拟点击验证码...");

        if (codeList.length == 0) {
            return "";
        }
        String result = "";
        String offsetsX = "";
        String offsetsY = "";
        for (int i = 0; i < codeList.length; i++) {
            switch (codeList[i]) {
                case "1":
                    offsetsY = "40";
                    offsetsX = "40";
                    break;
                case "2":
                    offsetsY = "40";
                    offsetsX = "110";
                    break;
                case "3":
                    offsetsY = "40";
                    offsetsX = "184";
                    break;
                case "4":
                    offsetsY = "40";
                    offsetsX = "256";
                    break;
                case "5":
                    offsetsY = "110";
                    offsetsX = "40";
                    break;
                case "6":
                    offsetsY = "110";
                    offsetsX = "110";
                    break;
                case "7":
                    offsetsY = "110";
                    offsetsX = "184";
                    break;
                case "8":
                    offsetsY = "110";
                    offsetsX = "256";
                    break;
                default:
                    offsetsY = "-1";
                    offsetsX = "-1";
                    break;
            }
            if (result.equals("")) {
                result += offsetsX + "," + offsetsY;
            } else {
                result += "," + offsetsX + "," + offsetsY;
            }
        }
        Log.i(tag, "result:(" + result + ")");
        return result;

    }


    public static class CheckResponse {
        int code;
        String[] data;
        String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String[] getData() {
            return data;
        }

        public void setData(String[] data) {
            this.data = data;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
