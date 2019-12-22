package com.esaygo.app.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;
import com.esaygo.app.common.constan.Constans;

import es.dmoral.toasty.Toasty;


/**
 * Toast工具类，解决多个Toast同时出现的问题
 */
public class ToastUtils {

    private static Toast mToast;
    private static Context context = AppUtils.getAppContext();

    public static void speak(String message) {
        getToast(message, Toast.LENGTH_SHORT).show();
    }


    public static SpeechSynthesizer mSpeechSynthesizer;


    private static void initVoice() {
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(AppUtils.getAppContext()); // this 是Context的之类，如Activity
        mSpeechSynthesizer.setAppId("18082164"/*这里只是为了让Demo运行使用的APPID,请替换成自己的id。*/);
        mSpeechSynthesizer.setApiKey("OqfRD5B3Trbw8Bdo7QEXj1Rt", "DIRp0atDgu89qYpRnZhBt8377yF3fO8h"/*这里只是为了让Demo正常运行使用APIKey,请替换成自己的APIKey*/);
        mSpeechSynthesizer.auth(TtsMode.ONLINE); // 离在线混合
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "4"); // 设置发声的人声音，在线生效
        mSpeechSynthesizer.initTts(TtsMode.ONLINE); // 初始化离在线混合模式，如果只需要在线合成功能，使用 TtsMode.ONLINE
    }

    public static void Speak(String msg){
        if (mSpeechSynthesizer==null){
            initVoice();
        }
        mSpeechSynthesizer.speak(msg);
    }


    /**********************
     * 非连续弹出的Toast
     ***********************/
    public static void showSingleToast(int resId) { //R.string.**
        getSingleToast(resId, Toast.LENGTH_SHORT).show();
    }

    public static void showSingleToast(String text) {
        getSingleToast(text, Toast.LENGTH_SHORT).show();
    }

    public static void showSingleLongToast(int resId) {
        getSingleToast(resId, Toast.LENGTH_LONG).show();
    }

    public static void showSingleLongToast(String text) {
        getSingleToast(text, Toast.LENGTH_LONG).show();
    }

    /***********************
     * 连续弹出的Toast 位置在中央
     ************************/
    public static void showCenterToast(int resId) {
        getCenterToast(resId, Toast.LENGTH_SHORT).show();
    }

    public static void showCenterToast(String text) {
        getCenterToast(text, Toast.LENGTH_SHORT).show();
    }

    public static void showCenterLongToast(int resId) {
        getCenterToast(resId, Toast.LENGTH_LONG).show();
    }

    public static void showCenterLongToast(String text) {
        getCenterToast(text, Toast.LENGTH_LONG).show();
    }

    /**********************
     * 非连续弹出的Toast 位置在中央
     ***********************/
    public static void showCenterSingleToast(int resId) { //R.string.**
        getCenterSingleToast(resId, Toast.LENGTH_SHORT).show();
    }

    public static void showCenterSingleToast(String text) {
        getCenterSingleToast(text, Toast.LENGTH_SHORT).show();
    }

    public static void showCenterSingleLongToast(int resId) {
        getCenterSingleToast(resId, Toast.LENGTH_LONG).show();
    }

    public static void showCenterSingleLongToast(String text) {
        getCenterSingleToast(text, Toast.LENGTH_LONG).show();
    }

    /***********************
     * 连续弹出的Toast
     ************************/
    public static void showToast(int resId) {
        getToast(resId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(String text) {
        if (true) {
            getToast(text, Toast.LENGTH_SHORT).show();
        } else {
            getToast(text, Toast.LENGTH_SHORT).show();
            speak(text);
        }
    }

    public static void showLongToast(int resId) {
        getToast(resId, Toast.LENGTH_LONG).show();
    }

    public static void showLongToast(String text) {
        getToast(text, Toast.LENGTH_LONG).show();
    }

    public static Toast getSingleToast(int resId, int duration) { // 连续调用不会连续弹出，只是替换文本
        return getSingleToast(context.getResources().getText(resId).toString(), duration);
    }

    public static Toast getSingleToast(String text, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, duration);
        } else {
            mToast.setText(text);
        }
        return mToast;
    }

    public static Toast getToast(int resId, int duration) { // 连续调用会连续弹出
        return getToast(context.getResources().getText(resId).toString(), duration);
    }

    public static Toast getToast(String text, int duration) {
        return Toast.makeText(context, text, duration);
    }

    public static Toast getCenterSingleToast(int resId, int duration) { // 连续调用不会连续弹出，只是替换文本
        return getCenterSingleToast(context.getResources().getText(resId).toString(), duration);
    }

    public static Toast getCenterSingleToast(String text, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, duration);
        } else {
            mToast.setText(text);
        }
        mToast.setGravity(Gravity.CENTER, 0, 0);
        return mToast;
    }

    public static Toast getCenterToast(int resId, int duration) { // 连续调用会连续弹出
        return getCenterToast(context.getResources().getText(resId).toString(), duration);
    }

    public static Toast getCenterToast(String text, int duration) {
        mToast = Toast.makeText(context, text, duration);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        return mToast;
    }

    public static void showSucessToast(String msg){
        Toasty.success(context, msg, Constans.TIME_1500, true).show();
    }

    public static void showWarningToast(String msg){
        Toasty.warning(context, msg, Constans.TIME_1500, true).show();
    }

    public static void showInfoToast(String msg){
        Toasty.info(context, msg, Constans.TIME_1500, true).show();
    }

    public static void showErrorToast(String msg){
        Toasty.error(context, msg, Constans.TIME_1500, true).show();
    }
}
