package com.esaygo.app.utils.book.utils;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.esaygo.app.module.query.modle.TrainBean;
import com.esaygo.app.module.ticket.model.TaskDetails;
import com.esaygo.app.utils.book.bean.BookResult;
import com.esaygo.app.utils.book.bean.OrderMsg;
import com.esaygo.app.utils.book.bean.QueryTimeResult;
import com.esaygo.app.utils.network.support.ApiConstants;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 订票工具类
 */
public class BookUtils {

    private final static String Tag = "BookUtils";

    /**
     * 检查是否登录
     *
     * @param
     * @return
     */

    public static BookResult checkUserStatus(BookResult bookResult) {
        Log.e(Tag, "正在检查用户是否登录...");

        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("_json_att", "");

        Request request = new Request.Builder()
                .url("https://kyfw.12306.cn/otn/login/checkUser")
                .post(formBody.build())
                .addHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400")
                .addHeader("Host", "kyfw.12306.cn")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Origin", "https://kyfw.12306.cn")
                .build();
        Response response = null;
        try {
            response = bookResult.getClient().newCall(request).execute();
            if (response.isSuccessful()) {
                JSONObject result = JSON.parseObject(response.body().string());
                //System.out.println(result);
                JSONObject data = result.getJSONObject("data");
                if (result.getBoolean("status") && data.getBoolean("flag")) {
                    bookResult.setCheckUser(true);

                }
                return bookResult;
            }
        } catch (Exception e) {
            Log.e(Tag, "请求失败，错误信息:[" + e.toString() + "]");
        }
        return bookResult;
    }

    /**
     * 提交订单请求 检查该用户是否有未完成订单，如果有则返回false
     *
     * @param bookResult
     * @return
     */
    public static BookResult submitOrder(BookResult bookResult, TrainBean chooseTrain,TaskDetails taskDetails) {
        Log.e(Tag, "正在检查是否有未完成订单...");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String back_train_date = sdf.format(new Date());
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("train_date", chooseTrain.getTrain_date());
        formBody.add("back_train_date", back_train_date);
        formBody.add("tour_flag", "dc");
        formBody.add("purpose_codes", taskDetails.getTask().getType());
        formBody.add("query_from_station_name", chooseTrain.getFind_from());
        formBody.add("query_to_station_name", chooseTrain.getFind_to());
        formBody.add("undefined", "");
        try {
            formBody.add("secretStr", URLDecoder.decode(chooseTrain.getSecret_str(), "UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        Request request = new Request.Builder()
                .url("https://kyfw.12306.cn/otn/leftTicket/submitOrderRequest")
                .post(formBody.build())
                .addHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400")
                .addHeader("Host", "kyfw.12306.cn")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Origin", "https://kyfw.12306.cn")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Referer", "https://kyfw.12306.cn/otn/leftTicket/init")
                .addHeader("Accept-Encoding", "gzip, deflate, br")
                .build();
        Response response = null;
        try {
            response = bookResult.getClient().newCall(request).execute();
            if (response.isSuccessful()) {
                JSONObject result = JSONObject.parseObject(response.body().string());
                //System.out.println(result);
                if (result.getBoolean("status")) {
                    bookResult.setSubmitOrder(true);
                    return bookResult;
                } else {
                    Log.i(Tag, result.get("messages").toString());
                }
            }
        } catch (Exception e) {
            Log.i(Tag, "请求失败，错误信息:[" + e.toString() + "]");
        }
        return bookResult;

    }

    public static BookResult getInitDc(BookResult bookResult) {
        Log.i(Tag, "正在请求InitDc...");
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("_json_att", "");
        Request request = new Request.Builder()
                .url("https://kyfw.12306.cn/otn/confirmPassenger/initDc")
                .post(formBody.build())
                .addHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400")
                .addHeader("Host", "kyfw.12306.cn")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Origin", "https://kyfw.12306.cn")
                .addHeader("X-Requested-With", "XMLHttpRequest")
             //   .addHeader("Accept-Encoding", "gzip, deflate, br")
                .build();
        Response response = null;
        try {
            response = bookResult.getClient().newCall(request).execute();
            if (response.isSuccessful()) {
                // 获取globalRepeatSubmitToken 和 ticketInfoForPassengerForm
                // 这里说明一下，该请求会返回一个HTML页面，页面中的globalRepeatSubmitToken和 ticketInfoForPassengerForm
                // 是请求其他页面的必要参数
              //  String resu=response.body().string();
                String str= new String(response.body().bytes(), StandardCharsets.UTF_8);
                Log.i("InitDc",str);
                String[] html = str.split("\n");
                for (String line : html) {
                    if (line.contains("globalRepeatSubmitToken")) {
                        bookResult.setGlobalRepeatSubmitToken(line.substring(line.indexOf("'") + 1, line.length() - 2));
                        Log.i("globalRepeatSubmitToken",bookResult.getGlobalRepeatSubmitToken());
                        //System.out.println(bookResult.getGlobalRepeatSubmitToken());
                    }
                    if (line.contains("var ticketInfoForPassengerForm")) {
                        String ticketInfo = line.substring(line.indexOf("{"), line.length() - 1);
                        bookResult.setInitDcInfo(FormatUtils.formatInitDc(ticketInfo));
                        Log.i("globalRepeatSubmitToken",bookResult.getGlobalRepeatSubmitToken());
                    }
                }
                bookResult.setInitDc(true);
                return bookResult;
            }
        } catch (Exception e) {
            Log.i(Tag, "请求失败，错误信息:[" + e.toString() + "]");
        }
        return bookResult;
    }

    public static BookResult getPassenger(String methed,BookResult bookResult) {
        Log.i(Tag, "正在请求乘客信息...");

        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("_json_att", "");
        formBody.add("REPEAT_SUBMIT_TOKEN", bookResult.getGlobalRepeatSubmitToken());
        Request request;
        if(methed.equals("POST")){
            request=new Request.Builder()
                    .url("https://kyfw.12306.cn/otn/confirmPassenger/getPassengerDTOs")
                    .post(formBody.build())
                    .addHeader("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400")
                    .addHeader("Host", "kyfw.12306.cn")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .addHeader("Origin", "https://kyfw.12306.cn")
                    .addHeader("X-Requested-With", "XMLHttpRequest")
                //    .addHeader("Accept-Encoding", "gzip, deflate, br")
                    .build();
        }else {
            request=new Request.Builder()
                    .url("https://kyfw.12306.cn/otn/confirmPassenger/getPassengerDTOs")
                    .put(formBody.build())
                    .addHeader("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400")
                    .addHeader("Host", "kyfw.12306.cn")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .addHeader("Origin", "https://kyfw.12306.cn")
                    .addHeader("X-Requested-With", "XMLHttpRequest")
                //    .addHeader("Accept-Encoding", "gzip, deflate, br")
                    .build();
        }
        Response response = null;
        try {
            response = bookResult.getClient().newCall(request).execute();
            if (response.isSuccessful()) {
                JSONObject result = JSONObject.parseObject(response.body().string());
                System.out.println(result);
                if (result.getBoolean("status") && result.getInteger("httpstatus") == 200) {
                    bookResult.setPassenger(true);
                }
                return bookResult;
            }
        } catch (Exception e) {
            Log.e(Tag, "请求失败，错误信息:[" + e.toString() + "]");
        }
        return bookResult;
    }

    public static BookResult checkOrderInfo(BookResult bookResult, TaskDetails taskDetails, String setType) {
        Log.i(Tag, "正在检查订单信息...");

        String passengerTicketStr = "";
        String oldPassengerStr = "";


        // 拼接passengerTicketStr
        for (int i = 0; i < taskDetails.getTask_passenger().length; i++) {
            if (i == 0) {
                passengerTicketStr += setType + ",0,1," + taskDetails.getTask_passenger()[i].getName() + ",1," + taskDetails.getTask_passenger()[i].getId_num() + "," + taskDetails.getTask_passenger()[i].getTel_num() + ",N," + taskDetails.getTask_passenger()[i].getAllEncStr();
            } else {
                passengerTicketStr += "_" + setType + ",0,1," + taskDetails.getTask_passenger()[i].getName() + ",1," + taskDetails.getTask_passenger()[i].getId_num() + "," + taskDetails.getTask_passenger()[i].getTel_num() + ",N," + taskDetails.getTask_passenger()[i].getAllEncStr();
            }
            oldPassengerStr += taskDetails.getTask_passenger()[i].getName() + ",1," + taskDetails.getTask_passenger()[i].getId_num() + ",1_";
        }
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("cancel_flag", "2");
        formBody.add("bed_level_order_num", "000000000000000000000000000000");
        formBody.add("passengerTicketStr", passengerTicketStr);
        formBody.add("oldPassengerStr", oldPassengerStr);
        formBody.add("tour_flag", "dc");
        formBody.add("randCode", "");
        formBody.add("whatsSelect", "1");
        formBody.add("_json_att", "");
        formBody.add("REPEAT_SUBMIT_TOKEN", bookResult.getGlobalRepeatSubmitToken());
        Request request = new Request.Builder()
                .url("https://kyfw.12306.cn/otn/confirmPassenger/checkOrderInfo")
                .post(formBody.build())
                .addHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400")
                .addHeader("Host", "kyfw.12306.cn")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Origin", "https://kyfw.12306.cn")
                .addHeader("X-Requested-With", "XMLHttpRequest")
           //     .addHeader("Accept-Encoding", "gzip, deflate, br")
                .build();
        Response response = null;

		/*
		String passengerTicketStr = StringUtils
				.join(new String[] { u.getSeatType(), "0", "1", u.getName(), "1", u.getId(), u.getTelNum(), "N" }, ",");
		// 拼接oldPassengerStr
		String oldPassengerStr = StringUtils.join(new String[] { u.getName(), "1", u.getId(), "1_" }, ",");
        */

        // 准备表单数据
        bookResult.setPassengerTicketStr(passengerTicketStr);
        bookResult.setOldPassengerStr(oldPassengerStr);

        try {
            response = bookResult.getClient().newCall(request).execute();
            if (response.isSuccessful()) {
                String responseText = response.body().string();
                System.out.println(responseText);
                JSONObject result = JSONObject.parseObject(responseText);
                JSONObject data = result.getJSONObject("data");
                boolean submitStatus = data.getBoolean("submitStatus");
                boolean status = result.getBoolean("status");
                if (status && submitStatus) {
                    bookResult.setCheckOrderInfo(true);
                    return bookResult;
                } else {
                    Log.e(Tag, "订单信息错误...");
                    System.exit(0);
                }

            }
        } catch (Exception e) {
            Log.e(Tag, "请求失败，错误信息:[" + e.toString() + "]");
        }
        return bookResult;
    }

    /**
     * 该方法为请求请求是否可以加入队列，但响应结果没有用，舍弃请求该页面
     *
     * @param bookResult
     * @return
     */

    public static BookResult getQueueCount(BookResult bookResult) {
        // logger.info("正在进入队列...");
        // String URL = IOUtils.getPropertyValue("checkOrderInfo");
        // HttpPost queueCountPost = new HttpPost(URL);
        //
        // queueCountPost.setHeader("User-Agent",
        // "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko)
        // Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400
        // QQBrowser/10.3.2864.400");
        // queueCountPost.setHeader("Host", "kyfw.12306.cn");
        // queueCountPost.setHeader("Content-Type", "application/x-www-form-urlencoded;
        // charset=UTF-8");
        // queueCountPost.setHeader("X-Requested-With", "XMLHttpRequest");
        // String trainDateGMT = " Mon Feb 13 2019 00:00:00 GMT+0800";
        // // 创建请求数据
        // Map<String, String> form = new HashMap<>();
        // form.put("_json_att", "");
        // form.put("from_station_telecode",
        // bookResult.getInitDcInfo().getFromStationTelecode());
        // form.put("leftTicket", bookResult.getInitDcInfo().getLeftTicketStr());
        // form.put("purpose_codes", bookResult.getInitDcInfo().getPurposeCodes());
        // form.put("REPEAT_SUBMIT_TOKEN", bookResult.getGlobalRepeatSubmitToken());
        // form.put("seatType", "O");
        // form.put("stationTrainCode",
        // bookResult.getInitDcInfo().getStationTrainCode());
        // form.put("toStationTelecode",
        // bookResult.getInitDcInfo().getToStationTelecode());
        // form.put("train_date", trainDateGMT);
        // form.put("train_location", bookResult.getInitDcInfo().getTrainLocation());
        // form.put("train_no", bookResult.getInitDcInfo().getTrainNo());
        // queueCountPost.setEntity(FormatUtils.setPostEntityFromMap(form));
        // CloseableHttpResponse response = null;
        //
        // try {
        // response = bookResult.getClient().execute(queueCountPost);
        // if (response.getStatusLine().getStatusCode() == 200) {
        // System.out.println(EntityUtils.toString(response.getEntity()));
        //
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        return bookResult;
    }

    /**
     * 该方法为请求进入购票队列
     *
     * @param bookResult
     * @return
     */
    public static BookResult getConfirmSingleForQueue(BookResult bookResult) {
        Log.i(Tag, "正在下单...");
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("_json_att", "");
        formBody.add("choose_seats", "");
        formBody.add("dwAll", "N");
        formBody.add("key_check_isChange", bookResult.getInitDcInfo().getKeyCheckIsChange());
        formBody.add("leftTicketStr", bookResult.getInitDcInfo().getLeftTicketStr());
        formBody.add("oldPassengerStr", bookResult.getOldPassengerStr());
        formBody.add("passengerTicketStr", bookResult.getPassengerTicketStr());
        formBody.add("purpose_codes", bookResult.getInitDcInfo().getPurposeCodes());
        formBody.add("randCode", "");
        formBody.add("REPEAT_SUBMIT_TOKEN", bookResult.getGlobalRepeatSubmitToken());
        formBody.add("roomType", "00");
        formBody.add("seatDetailType", "000");
        formBody.add("train_location", bookResult.getInitDcInfo().getTrainLocation());
        formBody.add("whatsSelect", "1");
        Request request = new Request.Builder()
                .url("https://kyfw.12306.cn/otn/confirmPassenger/confirmSingleForQueue")
                .post(formBody.build())
                .addHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400")
                .addHeader("Host", "kyfw.12306.cn")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Origin", "https://kyfw.12306.cn")
                .addHeader("X-Requested-With", "XMLHttpRequest")
         //       .addHeader("Accept-Encoding", "gzip, deflate, br")
                .build();
        Response response = null;

        try {
            response = bookResult.getClient().newCall(request).execute();
            if (response.isSuccessful()) {
                JSONObject result = JSON.parseObject(response.body().string());
//				System.out.println(result);
                JSONObject data = result.getJSONObject("data");
                boolean status = result.getBoolean("status");
                boolean submitStatus = data.getBoolean("submitStatus");
                if (status && submitStatus) {
                    bookResult.setConfirmSingleForQueue(true);
                }

            }
        } catch (Exception e) {
            Log.i(Tag, "请求失败，错误信息:[" + e.toString() + "]");
        }
        return bookResult;
    }

    /**
     * 该方法为请求排队时间
     *
     * @param bookResult
     * @return
     */
    public static BookResult getQueryOrderTime(BookResult bookResult) {
        Log.i(Tag, "正在查询排队时间...");
        //	String URL = IOUtils.getPropertyValue("queryOrderTime");

        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("_json_att", "");
        formBody.add("random", String.valueOf(System.currentTimeMillis()));
        formBody.add("REPEAT_SUBMIT_TOKEN", bookResult.getGlobalRepeatSubmitToken());
        formBody.add("tourFlag", "dc");
        Request request = new Request.Builder()
                .url("https://kyfw.12306.cn/otn/confirmPassenger/queryOrderWaitTime")
                .post(formBody.build())
                .addHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400")
                .addHeader("Host", "kyfw.12306.cn")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Origin", "https://kyfw.12306.cn")
                .addHeader("X-Requested-With", "XMLHttpRequest")
        //        .addHeader("Accept-Encoding", "gzip, deflate, br")
                .build();

        QueryTimeResult queryTimeResult = new QueryTimeResult();

        while (!(queryTimeResult = getQueryOrderTimeMethod(request, bookResult)).isOK()) {
            try {
                Thread.sleep(4000);// 每4秒刷新一次排队信息
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.i(Tag, "订票成功,订单号：" + queryTimeResult.getOrderId());
        bookResult.setQueryOrderTime(true);
        bookResult.setQueryTimeResult(queryTimeResult);
        return bookResult;
    }

    /**
     * 该方法为请求排队时间的方法体，因为要循环请求，所以单独拿出来作为一个方法
     *
     * @param bookResult
     * @return
     */
    public static QueryTimeResult getQueryOrderTimeMethod(Request request, BookResult bookResult) {
        QueryTimeResult queryTimeResult = new QueryTimeResult();

        Response response = null;
        try {
            response = bookResult.getClient().newCall(request).execute();
            if (response.isSuccessful()) {
                JSONObject result = JSONObject.parseObject(response.body().string());
                //System.out.println(result);
                JSONObject data = result.getJSONObject("data");
                boolean status = result.getBoolean("status");
                boolean queryOrderWaitTimeStatus = data.getBoolean("queryOrderWaitTimeStatus");
                if (status && queryOrderWaitTimeStatus) {
                    queryTimeResult.setWaitCount(data.getInteger("waitCount"));
                    queryTimeResult.setWaitTime(data.getInteger("waitTime"));
                    String msg = String.format("等待时间%d,等待人数%d", queryTimeResult.getWaitTime(),
                            queryTimeResult.getWaitCount());
                    Log.i(Tag, msg);
                    // json处理null比较麻烦，需要将null转换成JSONObject比较
                    // 这里json null比较还是有错
                 //   Object o = data.getString("orderId");
                    if (data.getString("orderId")!=null&&data.getString("orderId").isEmpty()) {
                        queryTimeResult.setOK(true);
                        queryTimeResult.setOrderId(data.getString("orderId"));
                        return queryTimeResult;
                    }
                    // 出现错误时提示错误信息
                    if (data.containsKey("errorcode")) {
                        Log.i(Tag, data.getString("msg"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // logger.error("请求失败，错误信息:[" + e.toString() + "]");
        }
        return queryTimeResult;
    }

    /**
     * 请求resultOrderForQueue页面，虽然该页面不会返回任何信息，但是如果不请求该页面，不能请求后面的订单信息页面
     *
     * @param bookResult
     * @return
     */
    public static BookResult getResultOrderForQueue(BookResult bookResult) {
        //	String URL = IOUtils.getPropertyValue("resultOrderForQueue");
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("_json_att", "");
        formBody.add("orderSequence_no", bookResult.getQueryTimeResult().getOrderId()==null?"":bookResult.getQueryTimeResult().getOrderId());
        formBody.add("REPEAT_SUBMIT_TOKEN", bookResult.getGlobalRepeatSubmitToken());
        Request request = new Request.Builder()
                .url(ApiConstants.resultOrderForQueue)
                .post(formBody.build())
                .addHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400")
                .addHeader("Host", "kyfw.12306.cn")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Origin", "https://kyfw.12306.cn")
                .addHeader("X-Requested-With", "XMLHttpRequest")
         //       .addHeader("Accept-Encoding", "gzip, deflate, br")
                .build();

        Response response = null;
        try {
            response = bookResult.getClient().newCall(request).execute();
            if (response.isSuccessful()) {
                JSONObject result = JSONObject.parseObject(response.body().string());
                //System.out.println(result);
                JSONObject data = result.getJSONObject("data");
                boolean status = result.getBoolean("status");
                boolean submitStatus = data.getBoolean("submitStatus");
                if (status && submitStatus) {
                    bookResult.setResultOrderForQueue(true);
                    return bookResult;
                }
            }
        } catch (Exception e) {
            Log.i(Tag, "请求失败，错误信息:[" + e.toString() + "]");
        }
        return bookResult;
    }

    /**
     * 获取订单信息
     * 该方法中的请求地址URL需要拼接上当前时间的时间戳
     *
     * @param bookResult
     * @return
     */
    public static BookResult getOrderMsg(BookResult bookResult) {
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("_json_att", "");
        formBody.add("REPEAT_SUBMIT_TOKEN", bookResult.getGlobalRepeatSubmitToken());
        Request request = new Request.Builder()
                .url(ApiConstants.bookResult + String.valueOf(System.currentTimeMillis()))
                .post(formBody.build())
                .addHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400")
                .addHeader("Host", "kyfw.12306.cn")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Origin", "https://kyfw.12306.cn")
                .addHeader("X-Requested-With", "XMLHttpRequest")
         //       .addHeader("Accept-Encoding", "gzip, deflate, br")
                .build();

        Response response = null;
        try {
            response = bookResult.getClient().newCall(request).execute();
            if (response.isSuccessful()) {
                String context = response.body().string();
                //System.out.println(context);
                String[] html = context.split("\n");
                String resultMap = null;
                for (String line : html) {
                    if (line.contains("var passangerTicketList")) {
                        resultMap = line.substring(line.indexOf("["), line.length() - 1);
                    }
                }
                OrderMsg msg = new OrderMsg();
                msg = FormatUtils.formatBookResult(resultMap);
                if (null != msg) {
                    bookResult.setFinish(true);
                    bookResult.setOrderMsg(msg);
                    return bookResult;
                }
            }
        } catch (Exception e) {
            Log.i(Tag, "请求失败，错误信息:[" + e.toString() + "]");
        }

        return bookResult;
    }

}
