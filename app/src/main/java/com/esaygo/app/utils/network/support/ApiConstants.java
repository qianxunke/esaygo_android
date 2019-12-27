package com.esaygo.app.utils.network.support;


/**
 * @author qianxunke
 * @email 736567805@qq.com
 * @date 2019-11-28 16:53
 * @desc API常量类
 */
public class ApiConstants {
    public final static boolean isDebug = true;
    //172.16.3.162
    //http://webapi.ftl-express.com
    public final static String API_BASE_URL = "http://49.235.63.91/api/";


    //#Link to get station code
    public final static String getStationCode = "https://kyfw.12306.cn/otn/resources/js/framework/station_name.js";

    //       #Link to query train message
    public final static String queryTrain = "https://kyfw.12306.cn/otn/leftTicket/queryZ";

    // #Automatic Identification VCode
    public final static String identifyVCode = "http://60.205.200.159/api";
    //         #Get Identification VCode Result
    public final static String getIndentifyResult = "http://check.huochepiao.360.cn/img_vcode";
    //           #Spare Automatic Identification VCode
    public final static String spareIdentifyVCode = "http://www.xiuler.com/test";
    //          #Link to check VCode
    public final static String checkVCode = "https://kyfw.12306.cn/passport/captcha/captcha-check";

    //            #Link to Login 12306
    public final static String loginURL = "https://kyfw.12306.cn/passport/web/login";
    //            #Link to get Token
    public final static String getToken = "https://kyfw.12306.cn/passport/web/auth/uamtk";
    //            #Link to check Token
    public final static String checkToken = "https://kyfw.12306.cn/otn/uamauthclient";
    //           #Link to check user login status
    public final static String checkLoginStatus = "https://kyfw.12306.cn/otn/login/checkUser";

    //            # submitOrderRequestURL
    public final static String submitOrderRequestURL = "https://kyfw.12306.cn/otn/leftTicket/submitOrderRequest";
    public final static String initDcURL = "https://kyfw.12306.cn/otn/confirmPassenger/initDc";
    public final static String getPassenger = "https://kyfw.12306.cn/otn/confirmPassenger/getPassengerDTOs";
    public final static String checkOrderInfo = "https://kyfw.12306.cn/otn/confirmPassenger/checkOrderInfo";
    //           #getQueueCountURL=https://kyfw.12306.cn/otn/confirmPassenger/getQueueCount"
    public final static String confirmSingleForQueueURL = "https://kyfw.12306.cn/otn/confirmPassenger/confirmSingleForQueue";
    public final static String queryOrderTime = "https://kyfw.12306.cn/otn/confirmPassenger/queryOrderWaitTime";
    public final static String resultOrderForQueue = "https://kyfw.12306.cn/otn/confirmPassenger/resultOrderForDcQueue";
    public final static String bookResult = "https://kyfw.12306.cn/otn//payOrder/init?random=";

    public final static String POST_CHECK_CODE_FROM_MY_SERVER=API_BASE_URL+"verify/base64";


    public final static String COMMON_UA_STR = " Android Client/1.0 (soleilyoyiyi@gmail.com)";
}
