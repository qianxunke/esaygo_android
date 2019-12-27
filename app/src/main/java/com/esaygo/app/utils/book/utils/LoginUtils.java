package com.esaygo.app.utils.book.utils;


import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.esaygo.app.module.user.UserModel;
import com.esaygo.app.utils.book.bean.CheckVCodeResult;
import com.esaygo.app.utils.book.bean.LoginCheckResult;
import com.esaygo.app.utils.book.vcode.IdentifyVCode;
import com.esaygo.app.utils.network.support.ApiConstants;

import java.util.logging.Logger;


import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 登录工具类
 */
public class LoginUtils {
    private static Logger logger = Logger.getLogger(LoginUtils.class.getName());

    /**
     * 验证用户名密码是否正确
     */


    public static LoginCheckResult checkUser(String methed, OkHttpClient okClient, String checkCode, LoginCheckResult loginResult) {
        // 准备URL
        logger.info("正在验证账号密码...");
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("username", UserModel.getCurrentUser().getTran_user_account());
        formBody.add("password", UserModel.getCurrentUser().getTran_user_pwd());
        formBody.add("appid", "otn");
        formBody.add("answer", checkCode);
        Request request;

        if (methed.equals("POST")) {
            request = new Request.Builder()
                    .url(ApiConstants.loginURL)
                    .addHeader("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400")
                    .addHeader("Host", "kyfw.12306.cn")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .addHeader("Origin", "https://kyfw.12306.cn")
                    .addHeader("Referer", "https://kyfw.12306.cn/otn/resources/login.html")
                    .addHeader("X-Requested-With", "XMLHttpRequest")
                    .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                    .addHeader("Accept", "application/json, text/javascript, */*; q=0.01")
                    .addHeader("Connection", "keep-alive")
                    .post(formBody.build())
                    .build();
        } else {
            request = new Request.Builder()
                    .url(ApiConstants.loginURL)
                    .addHeader("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400")
                    .addHeader("Host", "kyfw.12306.cn")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .addHeader("Origin", "https://kyfw.12306.cn")
                    .addHeader("Referer", "https://kyfw.12306.cn/otn/resources/login.html")
                    .addHeader("X-Requested-With", "XMLHttpRequest")
                    .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                    .addHeader("Accept", "application/json, text/javascript, */*; q=0.01")
                    .addHeader("Connection", "keep-alive")
                    .put(formBody.build())
                    .build();
        }
        Response response = null;

        // 发送Post请求
        try {
            Log.i("发送请求头：", request.headers().toMultimap().toString());

            response = okClient.newCall(request).execute();
            Log.i("发送Cookie：", response.request().headers().toMultimap().toString());
            if (response.isSuccessful()) {
                JSONObject result = JSONObject.parseObject(response.body().string());
                //System.out.println(result);
                int result_code = result.getInteger("result_code");
                if (result_code == 0) {
                    logger.info("账号密码正确...");
                    loginResult.setCheckUser(true);
                    loginResult.setOkHttpClient(okClient);
                    return loginResult;
                }
                if (result_code == 1) {
                    loginResult.setCheckUser(false);
                    loginResult.setOkHttpClient(okClient);
                    return loginResult;
                }

            } else {
                logger.info("连接错误..." + response.code());
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("网络错误，错误信息:[" + e.toString() + "]");
        }
        return loginResult;
    }

    /**
     * 获取登录Token
     *
     * @param
     * @return
     */
    public static LoginCheckResult getToken(OkHttpClient okClient, LoginCheckResult loginResult) {
        // 准备URL
        logger.info("正在获取Token...");
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("appid", "otn");

        Request request = new Request.Builder()
                .url("https://kyfw.12306.cn/passport/web/auth/uamtk")
                .post(formBody.build())
                .addHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400")
                .addHeader("Host", "kyfw.12306.cn")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Origin", "https://kyfw.12306.cn")
                .addHeader("Referer", "https://kyfw.12306.cn/otn/resources/login.html")
                .build();
        Response response = null;
        // 发送Post请求
        try {
            response = okClient.newCall(request).execute();
            if (response.isSuccessful()) {
                logger.info("获取Token成功...");
                JSONObject jsonObject = JSONObject.parseObject(response.body().string());
                loginResult.setNewapptk(jsonObject.getString("newapptk"));
                loginResult.setGetToken(true);
                loginResult.setOkHttpClient(okClient);
                return loginResult;
            } else {
                logger.info("获取Token失败..." + response.code());
            }
        } catch (Exception e) {
            logger.info("网络错误，错误信息:[" + e.toString() + "]");
        }
        return loginResult;
    }

    /**
     * 登录
     *
     * @param loginResult
     * @return
     */

    public static LoginCheckResult checkToken(OkHttpClient okClient, LoginCheckResult loginResult) {
        logger.info("正在验证Token...");
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("tk", loginResult.getNewapptk());

        Request request = new Request.Builder()
                .url("https://kyfw.12306.cn/otn/uamauthclient")
                .post(formBody.build())
                .addHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400")
                .addHeader("Host", "kyfw.12306.cn")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Origin", "https://kyfw.12306.cn")
                .addHeader("Referer", "https://kyfw.12306.cn/otn/resources/login.html")
                .build();
        Response response = null;

        try {
            response = okClient.newCall(request).execute();
            //System.out.println(response.getStatusLine().getStatusCode());
            if (response.isSuccessful()) {
                JSONObject jsonObject = JSONObject.parseObject(response.body().string());
                if (jsonObject.getInteger("result_code") == 0) {
                    loginResult.setCheckToken(true);
                    loginResult.setApptk((String) jsonObject.get("apptk"));
                    loginResult.setUsername((String) jsonObject.get("username"));
                    loginResult.setOkHttpClient(okClient);
                    logger.info("登陆成功,用户名:" + loginResult.getUsername());
                    return loginResult;
                } else {
                    logger.info("登录错误，请稍后重试...");
                }
            }
        } catch (Exception e) {
            logger.info("网络错误，错误信息:[" + e.toString() + "]");
        }
        return loginResult;
    }


    public static LoginCheckResult CheckCodeAndLogin(OkHttpClient okClient) {

        LoginCheckResult loginResult = new LoginCheckResult();

        CheckVCodeResult checkResult = IdentifyVCode.autoIdentifyVCode(okClient);
        loginResult.setGetCode(checkResult.isGetCode());
        if (!loginResult.isGetCode()) {
            return loginResult;
        }

        loginResult.setLocalCode(checkResult.isLocalCode());
        if (!loginResult.isLocalCode()) {
            return loginResult;
        }
        loginResult.setCodeCheck(checkResult.isCheckCode());
        if (!loginResult.isCodeCheck()) {
            return loginResult;
        }
        okClient = checkResult.getOkHttpClient();
        loginResult = LoginUtils.checkUser("POST", okClient, checkResult.getCheckVCode(), loginResult);
        if (!loginResult.isCheckUser()) {
            loginResult = LoginUtils.checkUser("PUT", okClient, checkResult.getCheckVCode(), loginResult);
        }

        if (loginResult.isCheckUser()) {
            loginResult = LoginUtils.getToken(okClient, loginResult);
            if (loginResult.isGetToken()) {
                loginResult = LoginUtils.checkToken(okClient, loginResult);
                if (loginResult.isCheckToken()) {
                    loginResult.setOkHttpClient(okClient);
                }
            }
        }

        return loginResult;
    }
}
