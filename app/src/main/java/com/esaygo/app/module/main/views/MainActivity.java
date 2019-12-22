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


import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.esaygo.app.R;
import com.esaygo.app.common.base.BaseActivity;
import com.esaygo.app.common.constan.Constans;
import com.esaygo.app.module.main.bean.FeatureItem;
import com.esaygo.app.module.main.contract.MainContract;
import com.esaygo.app.module.main.presenter.MainPresenter;
import com.esaygo.app.module.main.views.adapter.FeatureAdapter;
import com.esaygo.app.module.user.UserModel;
import com.esaygo.app.rx.RxBus;
import com.esaygo.app.rx.event.Event;
import com.esaygo.app.utils.AppUtils;
import com.esaygo.app.utils.ToastUtils;
import com.esaygo.app.utils.network.common.HttpResponseBase;
import com.king.app.dialog.AppDialog;
import com.king.app.dialog.AppDialogConfig;
import com.king.app.updater.AppUpdater;
import com.king.app.updater.http.OkHttpManager;
import com.kongzue.dialog.v2.MessageDialog;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.ycl.tabview.library.TabView;
import com.ycl.tabview.library.TabViewChild;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

import static android.provider.Settings.EXTRA_APP_PACKAGE;
import static android.provider.Settings.EXTRA_CHANNEL_ID;
import static java.security.AccessController.getContext;


public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View{

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
    protected void onResume() {
        super.onResume();
    }

    private void quanxian(){
        Acp.getInstance(this).request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_WIFI_STATE,
                                Manifest.permission.CHANGE_WIFI_STATE

                           )
                        /*以下为自定义提示语、按钮文字
                        .setDeniedMessage()
                        .setDeniedCloseBtn()
                        .setDeniedSettingBtn()
                        .setRationalMessage()
                        .setRationalBtn()*/
                        .build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                        mPresenter.doneUpdate();
                        checkNotifySetting();
                    }

                    @Override
                    public void onDenied(List<String> permissions) {
                        ToastUtils.Speak("您未开启权限，可能会导致App使用异常");
                      //  makeText(permissions.toString() + "权限拒绝");
                    }
                });

    }

    @Override
    protected void loadData() {
        super.loadData();

      //  mPresenter.doneUpdate();
    }

    @Override
    public void showUpdate(HttpResponseBase<MainPresenter.UpdateInfo> datas) {

        if (datas.code== Constans.API_RESULT_OK) {
            if (AppUtils.verComparison(datas.data.getVersion(), AppUtils.getVerName(mContext))) {
                AppDialogConfig config = new AppDialogConfig();
                config.setTitle("版本更新")
                        .setOk("升级")
                        .setContent(datas.data.getMessage())
                        .setOnClickOk(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new AppUpdater.Builder()
                                        .serUrl(datas.data.getUrl())
                                        .setFilename("AppUpdater.apk")
                                        .build(mContext)
                                        .start();
                                AppDialog.INSTANCE.dismissDialogFragment(getSupportFragmentManager());
                            }
                        });
                AppDialog.INSTANCE.showDialogFragment(getSupportFragmentManager(), config);
            }
        }
    }


    private void initTableView() {
        homeFragment = HomeFragment.getInstance("首页");
        bookFragment = BookFragment.getInstance("抢票");
        meFragment = MeFragment.getInstance("我的");
        List<TabViewChild> tabViewChildList = new ArrayList<>();
        TabViewChild tabViewChild01 = new TabViewChild(R.drawable.ic_home_orange_36dp, R.drawable.ic_home_grey_500_36dp, "首页", homeFragment);
        TabViewChild tabViewChild02 = new TabViewChild(R.drawable.ic_hourglass_empty_orange_36dp, R.drawable.ic_hourglass_empty_grey_500_36dp, "抢票", bookFragment);
        TabViewChild tabViewChild03 = new TabViewChild(R.drawable.ic_person_outline_orange_36dp, R.drawable.ic_person_outline_grey_500_36dp, "我的", meFragment);
        tabViewChildList.add(tabViewChild01);
        tabViewChildList.add(tabViewChild02);
        tabViewChildList.add(tabViewChild03);
        tabView.setTextViewSelectedColor(getResources().getColor(R.color.main_color));
        tabView.setTextViewUnSelectedColor(getResources().getColor(R.color.text_66));
        tabView.setTabViewBackgroundColor(getResources().getColor(R.color.white));
        tabView.setTabViewHeight(48);
        tabView.setImageViewTextViewMargin(4);
        tabView.setTextViewSize(12);
        tabView.setImageViewWidth(dip2px(24));
        tabView.setImageViewHeight(dip2px(24));
        tabView.setTabViewGravity(Gravity.TOP);
        tabView.setTabViewChild(tabViewChildList, getSupportFragmentManager());
        tabView.setTabViewDefaultPosition(0);
        quanxian();
        ToastUtils.Speak("您好,欢迎使用EsayGo,祝您抢票愉快");

    }

    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
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



    /**
     * 作者：CnPeng
     * 时间：2018/7/12 上午8:02
     * 功用：初始化点击事件
     * 说明：
     */
    private void initClickListener() {
        //CnPeng 2018/7/12 上午7:08 跳转到通知设置界面
                try {
                    // 根据isOpened结果，判断是否需要提醒用户跳转AppInfo页面，去打开App通知权限
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
                    intent.putExtra(EXTRA_APP_PACKAGE, getPackageName());
                    intent.putExtra(EXTRA_CHANNEL_ID, getApplicationInfo().uid);

                    //这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
                    intent.putExtra("app_package", getPackageName());
                    intent.putExtra("app_uid", getApplicationInfo().uid);

                    // 小米6 -MIUI9.6-8.0.0系统，是个特例，通知设置界面只能控制"允许使用通知圆点"——然而这个玩意并没有卵用，我想对雷布斯说：I'm not ok!!!
                    //  if ("MI 6".equals(Build.MODEL)) {
                    //      intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    //      Uri uri = Uri.fromParts("package", getPackageName(), null);
                    //      intent.setData(uri);
                    //      // intent.setAction("com.android.settings/.SubSettings");
                    //  }
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    // 出现异常则跳转到应用设置界面：锤子坚果3——OC105 API25
                    Intent intent = new Intent();

                    //下面这种方案是直接跳转到当前应用的设置界面。
                    //https://blog.csdn.net/ysy950803/article/details/71910806
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
    }

    /**
     * 作者：CnPeng
     * 时间：2018/7/12 上午9:02
     * 功用：检查是否已经开启了通知权限
     * 说明：
     */
    private void checkNotifySetting() {
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        // areNotificationsEnabled方法的有效性官方只最低支持到API 19，低于19的仍可调用此方法不过只会返回true，即默认为用户已经开启了通知。
        boolean isOpened = manager.areNotificationsEnabled();
        if (!isOpened) {
            ToastUtils.Speak("您还未开启通知权限，请先去开启");
            initClickListener();
        }
    }

}
