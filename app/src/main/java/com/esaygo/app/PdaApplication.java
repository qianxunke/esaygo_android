package com.esaygo.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.facebook.stetho.Stetho;
import com.esaygo.app.di.component.AppComponent;
import com.esaygo.app.di.component.DaggerAppComponent;
import com.esaygo.app.di.module.ApiModule;
import com.esaygo.app.di.module.AppModule;
import com.esaygo.app.utils.AppUtils;
import com.esaygo.app.utils.CrashHandler;
import com.esaygo.app.utils.LogUtils;
import com.esaygo.app.utils.PrefsUtils;
import com.esaygo.app.utils.network.NetworkUtils;
import com.esaygo.app.utils.network.support.ApiConstants;


import java.util.HashSet;
import java.util.Set;

import es.dmoral.toasty.Toasty;


public class PdaApplication extends Application {

    private static volatile PdaApplication instance = null;
    private Set<Activity> allActivities;
    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        AppUtils.init(this);
        instance = this;
        initNetwork();
        initStetho();
        initCrashHandler();
        initLog();
        initPrefs();
        initComponent();
      //  CrashReport.initCrashReport(this, "ed6f6a0e1c", ApiConstants.isDebug);
         // 初始化 JPush
    }

    public static PdaApplication getInstance() {
        return instance;
    }


    /**
     * 增加Activity
     *
     * @param act act
     */
    public void addActivity(Activity act) {
        if (allActivities == null) {
            allActivities = new HashSet<>();
        }
        allActivities.add(act);
    }

    /**
     * 移除Activity
     *
     * @param act act
     */
    public void removeActivity(Activity act) {
        if (allActivities != null) {
            allActivities.remove(act);
        }
    }

    public void removeAllActivitys() {
        if (allActivities != null) {
            synchronized (allActivities) {
                for (Activity act : allActivities) {
                    act.finish();
                }
            }
        }
    }

    /**
     * 退出应用
     */
    public void exitApp() {
        if (allActivities != null) {
            synchronized (allActivities) {
                for (Activity act : allActivities) {
                    act.finish();
                }
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    /**
     * 初始化网络模块组件
     */
    private void initComponent() {
        mAppComponent = DaggerAppComponent.builder()
                .apiModule(new ApiModule())
                .appModule(new AppModule(this))
                .build();

    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    /**
     * 初始化sp
     */
    private void initPrefs() {
        PrefsUtils.init(this, getPackageName() + "_preference", Context.MODE_MULTI_PROCESS);
    }

    /**
     * 初始化调试
     */
    private void initStetho() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

    /**
     * 开启网络监听
     */
    private void initNetwork() {
        NetworkUtils.startNetService(this);
    }


    /**
     * 初始化崩溃日志
     */
    private void initCrashHandler() {
        CrashHandler.getInstance().init(this);
    }


    /**
     * 初始化log
     */
    private void initLog() {
        LogUtils.init(this);
    }


}
