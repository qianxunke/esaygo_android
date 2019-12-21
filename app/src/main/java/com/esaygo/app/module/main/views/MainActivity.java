package com.esaygo.app.module.main.views;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.esaygo.app.R;
import com.esaygo.app.common.base.BaseActivity;
import com.esaygo.app.module.main.bean.FeatureItem;
import com.esaygo.app.module.main.contract.MainContract;
import com.esaygo.app.module.main.presenter.MainPresenter;
import com.esaygo.app.module.main.views.adapter.FeatureAdapter;
import com.esaygo.app.rx.RxBus;
import com.esaygo.app.rx.event.Event;
import com.esaygo.app.utils.ToastUtils;
import com.king.app.updater.AppUpdater;
import com.king.app.updater.http.OkHttpManager;
import com.kongzue.dialog.v2.MessageDialog;
import com.ycl.tabview.library.TabView;
import com.ycl.tabview.library.TabViewChild;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

import static java.security.AccessController.getContext;


public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View {

    @BindView(R.id.tabView)
    TabView tabView;

    long exitTime = 0L;


    HomeFragment homeFragment;
    BookFragment bookFragment;
    MeFragment meFragment;
    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initToolbar() {
        mBack = false;
        super.initToolbar();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        initTableView();
    }

    @Override
    protected void loadData() {
        super.loadData();

     //   mPresenter.doneUpdate("1.0.0");
    }

    @Override
    public void showUpdate(String rsp) {
        /*
        new AppUpdater.Builder()
                .serUrl(mUrl)
                .setFilename("AppUpdater.apk")
                .build(getContext())
                .setHttpManager(OkHttpManager.getInstance())//使用OkHttpClient实现下载，需依赖okhttp库
                .start();
        AppDialog.INSTANCE.dismissDialogFragment(getSupportFragmentManager());

         */
    }




    private void initTableView() {
        homeFragment = HomeFragment.getInstance("首页");
        bookFragment = BookFragment.getInstance("抢票");
        meFragment=MeFragment.getInstance("我的");
        List<TabViewChild> tabViewChildList = new ArrayList<>();
        TabViewChild tabViewChild01 = new TabViewChild(R.mipmap.ic_launcher, R.mipmap.ic_launcher, "首页", homeFragment);
        TabViewChild tabViewChild02 = new TabViewChild(R.mipmap.ic_launcher, R.mipmap.ic_launcher, "抢票", bookFragment);
        TabViewChild tabViewChild03 = new TabViewChild(R.mipmap.ic_launcher, R.mipmap.ic_launcher, "我的", meFragment);
        tabViewChildList.add(tabViewChild01);
        tabViewChildList.add(tabViewChild02);
        tabViewChildList.add(tabViewChild03);
        tabView.setTextViewSelectedColor(getResources().getColor(R.color.main_color));
        tabView.setTextViewUnSelectedColor(getResources().getColor(R.color.text_66));
        tabView.setTabViewBackgroundColor(getResources().getColor(R.color.white));
        tabView.setTabViewHeight(50);
        tabView.setImageViewTextViewMargin(4);
        tabView.setTextViewSize(13);
        tabView.setImageViewWidth(60);
        tabView.setImageViewHeight(60);
        tabView.setTabViewGravity(Gravity.TOP);
        tabView.setTabViewChild(tabViewChildList, getSupportFragmentManager());
        tabView.setTabViewDefaultPosition(0);
    }


    /**
     * 监听back键处理
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitApp();
        }
        return true;
    }

    /**
     * 双击退出App
     */
    private void exitApp() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            ToastUtils.showToast(getString(R.string.press_again_to_exit));
            exitTime = System.currentTimeMillis();
        } else {
            Event.ExitEvent event = new Event.ExitEvent();
            event.exit = -1;
            RxBus.INSTANCE.post(event);
        }
    }

    @Override
    public void showModules(List<FeatureItem> featureItems) {

    }



}
