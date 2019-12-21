package com.esaygo.app.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;


import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import com.esaygo.app.PdaApplication;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * APP工具类
 */
public class AppUtils {

    private static Context mContext;
    private static Thread mUiThread;
    private static Timer mTimer;

    private static Handler sHandler = new Handler(Looper.getMainLooper());

    public static void init(Context context) { //在Application中初始化
        mContext = context;
        mUiThread = Thread.currentThread();
        mTimer = new Timer();
    }


    public static Context getAppContext() {
        return mContext;
    }

    public static AssetManager getAssets() {
        return mContext.getAssets();
    }

    public static float getDimension(int id) {
        return getResource().getDimension(id);
    }

    public static Resources getResource() {
        return mContext.getResources();
    }

    @SuppressWarnings("deprecation")
    public static Drawable getDrawable(int resId) {
        return mContext.getResources().getDrawable(resId);
    }

    @SuppressWarnings("deprecation")
    public static int getColor(int resId) {
        return mContext.getResources().getColor(resId);
    }

    public static String getString(@StringRes int resId) {
        return mContext.getResources().getString(resId);
    }

    public static String[] getStringArray(@ArrayRes int resId) {
        return mContext.getResources().getStringArray(resId);
    }

    public static boolean isUIThread() {
        return Thread.currentThread() == mUiThread;
    }

    public static void runOnUI(Runnable r) {
        sHandler.post(r);
    }

    public static void runOnUIDelayed(Runnable r, long delayMills) {
        sHandler.postDelayed(r, delayMills);
    }

    public static void runOnUITask(TimerTask r, long delay, long rate) {
        mTimer.schedule(r, delay, rate);
    }

    public static void runCancel() {
        mTimer.cancel();
    }

    public static void removeRunnable(Runnable r) {
        if (r == null) {
            sHandler.removeCallbacksAndMessages(null);
        } else {
            sHandler.removeCallbacks(r);
        }
    }

    /**
     * 获取当前本地apk的版本
     *
     * @param mContext
     * @return
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;

    }

    public static String getDeviceUUid() {
        String androidId = getAndroidID();
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) androidId.hashCode() << 32));
        return deviceUuid.toString();
    }

    private static String getAndroidID() {
        String id = Settings.Secure.getString(
                PdaApplication.getInstance().getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
        return id == null ? "" : id;
    }

    /**
     *比较两个版本的大小
     * @param newVer
     * @param oldVer
     * @return
     */
    public static boolean verComparison(String newVer, String oldVer) {
        if (newVer.isEmpty() || oldVer.isEmpty()) {
            return false;
        }
        StringBuffer newStringBuffer = new StringBuffer();
        for (int i = 0; i < newVer.length(); i++) {
            if (newVer.charAt(i) != '.') {
                if (newVer.charAt(i) >= 48 && newVer.charAt(i) <= 57) {
                    newStringBuffer.append(newVer.charAt(i));
                }
            }
        }
        if (newStringBuffer.length() == 0) {
            return false;
        }
        StringBuffer oldStringBuffer = new StringBuffer();
        for (int i = 0; i < oldVer.length(); i++) {
            if (oldVer.charAt(i) != '.') {
                if (oldVer.charAt(i) >= 48 && oldVer.charAt(i) <= 57) {
                    oldStringBuffer.append(oldVer.charAt(i));
                }
            }
        }
        if (oldStringBuffer.length() == 0) {
            return false;
        }

        if (Integer.parseInt(newStringBuffer.toString()) - Integer.parseInt(oldStringBuffer.toString()) > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 拨打电话（跳转到拨号界面，用户手动点击拨打）
     * @param context
     * @param phoneNum
     */
    public static void callPhoneToPhoneView(Context context, String phoneNum){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        context.startActivity(intent);
    }

    /**
     * 拨打电话（直接拨打电话）
     * @param phoneNum 电话号码
     */
    public static void callPhone(Context context, String phoneNum){
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        context.startActivity(intent);
    }


}
