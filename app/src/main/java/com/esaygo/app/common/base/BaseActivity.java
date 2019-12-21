package com.esaygo.app.common.base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;


import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import com.esaygo.app.PdaApplication;
import com.esaygo.app.R;
import com.esaygo.app.common.constan.Constans;
import com.esaygo.app.di.component.ActivityComponent;
import com.esaygo.app.di.component.DaggerActivityComponent;
import com.esaygo.app.di.module.ActivityModule;
import com.esaygo.app.module.user.UserModel;
import com.esaygo.app.module.user.view.LoginActivity;
import com.esaygo.app.rx.RxBus;
import com.esaygo.app.rx.event.Event;
import com.esaygo.app.utils.AppUtils;
import com.esaygo.app.utils.ToastUtils;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.dialog.v2.SelectDialog;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import me.leefeng.promptlibrary.PromptDialog;


/**
 * 基础Activity
 */
public abstract class BaseActivity<T extends BaseContract.BasePresenter> extends RxAppCompatActivity implements BaseContract.BaseView {

    @Inject
    protected T mPresenter;
    protected Toolbar mToolbar;//Toolbar
    protected Context mContext;//上下文环境
    protected TextView titleText;
    protected TextView right_title;
    protected boolean mBack = true;
    protected Handler mHandler;
    private Disposable mDisposable;
    protected PromptDialog promptDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mContext = this;
        ButterKnife.bind(this);
        mToolbar = findViewById(R.id.toolbar);
        mHandler = new Handler();
        initStatusBar();
        initInject();
        initPresenter();
        initVariables();
        PdaApplication.getInstance().addActivity(this);
        if (mToolbar != null) {
            titleText = findViewById(R.id.title);
            right_title = findViewById(R.id.right_title);
            //初始化Toolbar
            initToolbar();
            //让组件支持Toolbar
            setSupportActionBar(mToolbar);
            if (mBack) mToolbar.setNavigationOnClickListener(v -> finish());
        }
        initDatas();
        initWidget();
        initExit();
    }


    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

    protected ActivityComponent getActivityComponent() {
        return DaggerActivityComponent.builder()
                .appComponent(PdaApplication.getInstance().getAppComponent())
                .activityModule(getActivityModule())
                .build();
    }


    /**
     * 退出应用
     */
    private void initExit() {
        mDisposable = RxBus.INSTANCE.toDefaultFlowable(Event.ExitEvent.class, exitEvent -> {
            if (exitEvent.exit == -1) {
                finish();
            }
        });
    }

    /**
     * 注入依赖
     */
    protected void initInject() {

    }

    protected void initRecyclerView() {

    }

    /**
     * 完成请求
     */
    protected void finishTask() {
    }

    /**
     * 初始化StatusBar
     */
    protected void initStatusBar() {
        ImmersionBar.with(this)
                .transparentStatusBar()  //透明状态栏，不写默认透明色
                // .transparentNavigationBar()  //透明导航栏，不写默认黑色(设置此方法，fullScreen()方法自动为true)
                //  .transparentBar()             //透明状态栏和导航栏，不写默认状态栏为透明色，导航栏为黑色（设置此方法，fullScreen()方法自动为true）
                //  .statusBarColor(R.color.colorPrimary)     //状态栏颜色，不写默认透明色
                //  .navigationBarColor(R.color.colorPrimary) //导航栏颜色，不写默认黑色
                //  .barColor(R.color.colorPrimary)  //同时自定义状态栏和导航栏颜色，不写默认状态栏为透明色，导航栏为黑色
                //  .statusBarAlpha(0.3f)  //状态栏透明度，不写默认0.0f
                // .navigationBarAlpha(0.4f)  //导航栏透明度，不写默认0.0F
                // .barAlpha(0.3f)  //状态栏和导航栏透明度，不写默认0.0f
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                //  .navigationBarDarkIcon(true) //导航栏图标是深色，不写默认为亮色
                //  .autoDarkModeEnable(true) //自动状态栏字体和导航栏图标变色，必须指定状态栏颜色和导航栏颜色才可以自动变色哦
                //  .autoStatusBarDarkModeEnable(true,0.2f) //自动状态栏字体变色，必须指定状态栏颜色才可以自动变色哦
                //   .autoNavigationBarDarkModeEnable(true,0.2f) //自动导航栏图标变色，必须指定导航栏颜色才可以自动变色哦
                //  .flymeOSStatusBarFontColor(R.color.btn3)  //修改flyme OS状态栏字体颜色
                //  .fullScreen(true)      //有导航栏的情况下，activity全屏显示，也就是activity最下面被导航栏覆盖，不写默认非全屏
                //  .hideBar(BarHide.FLAG_HIDE_BAR)  //隐藏状态栏或导航栏或两者，不写默认不隐藏
                //  .addViewSupportTransformColor(toolbar)  //设置支持view变色，可以添加多个view，不指定颜色，默认和状态栏同色，还有两个重载方法
                //  .titleBar(view)    //解决状态栏和布局重叠问题，任选其一
                //  .titleBarMarginTop(view)     //解决状态栏和布局重叠问题，任选其一
                //  .statusBarView(view)  //解决状态栏和布局重叠问题，任选其一
                .fitsSystemWindows(true)    //解决状态栏和布局重叠问题，任选其一，默认为false，当为true时一定要指定statusBarColor()，不然状态栏为透明色，还有一些重载方法
                //  .supportActionBar(true) //支持ActionBar使用
                //  .statusBarColorTransform(R.color.orange)  //状态栏变色后的颜色
                //  .navigationBarColorTransform(R.color.orange) //导航栏变色后的颜色
                // .barColorTransform(R.color.orange)  //状态栏和导航栏变色后的颜色
                // .removeSupportView(toolbar)  //移除指定view支持
                // .removeSupportAllView() //移除全部view支持
                //  .navigationBarEnable(true)   //是否可以修改导航栏颜色，默认为true
                //   .navigationBarWithKitkatEnable(true)  //是否可以修改安卓4.4和emui3.x手机导航栏颜色，默认为true
                //   .navigationBarWithEMUI3Enable(true) //是否可以修改emui3.x手机导航栏颜色，默认为true
                .keyboardEnable(true)  //解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
                //  .keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)  //单独指定软键盘模式
                .init();

        //   StatusBarUtil.setTranslucent((Activity) mContext, AppUtils.getColor(R.color.colorPrimary));

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //如果用以下这种做法则不保存状态，再次进来的话会显示默认tab
        //总是执行这句代码来调用父类去保存视图层的状态
        super.onSaveInstanceState(outState);
    }

    /**
     * 初始化Presenter
     */
    private void initPresenter() {
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
    }


    @Override
    public void showError(String msg) {
        promptDialog.dismiss();
        if (msg.equals(Constans.LOGIN_CONFIT + "")) {
            SelectDialog.show(mContext, getString(R.string.notify_invite), getString(R.string.your_identity_has_expired_please_relogin), getString(R.string.confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    promptDialog.dismiss();
                    //    promptDialog.showLoading("请稍等...");
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            UserModel.deleteUserInfo();
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            PdaApplication.getInstance().removeAllActivitys();
                            startActivity(intent);
                        }
                    });
                }
            });
        } else {
            ToastUtils.showErrorToast(msg);
        }
    }

    @Override
    public void complete() {

    }

    /**
     * 销毁
     */
    @Override
    protected void onDestroy() {
        if (mPresenter != null) mPresenter.detachView();
        PdaApplication.getInstance().removeActivity(this);
        super.onDestroy();
        if (!mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    /**
     * 初始化Toolbar
     */
    protected void initToolbar() {
        if (mBack) mToolbar.setNavigationIcon(R.mipmap.black_return);
        mToolbar.setTitle("");
    }

    /**
     * 布局文件
     *
     * @return 布局文件
     */
    protected abstract
    @LayoutRes
    int getLayoutId();

    /**
     * 初始化控件
     */
    protected void initWidget() {
        promptDialog = new PromptDialog(this);
    }


    protected void initView() {

    }

    /**
     * 加载数据
     */
    protected void loadData() {
    }

    /**
     * 初始化数据
     */
    protected void initDatas() {

    }

    /**
     * 初始化变量
     */
    protected void initVariables() {
    }

    /**
     * 隐藏View
     *
     * @param views 视图
     */
    protected void gone(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 显示View
     *
     * @param views 视图
     */
    protected void visible(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 隐藏View
     *
     * @param id
     */
    protected void gone(final @IdRes int... id) {
        if (id != null && id.length > 0) {
            for (int resId : id) {
                View view = $(resId);
                if (view != null)
                    gone(view);
            }
        }

    }

    /**
     * 显示View
     *
     * @param id
     */
    protected void visible(final @IdRes int... id) {
        if (id != null && id.length > 0) {
            for (int resId : id) {
                View view = $(resId);
                if (view != null)
                    visible(view);
            }
        }
    }

    private View $(@IdRes int id) {
        View view;
        view = this.findViewById(id);
        return view;
    }

    public int getWindowHeigh(Context context) {
        // 获取屏幕分辨率
        WindowManager wm = (WindowManager) (context.getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int mScreenHeigh = dm.heightPixels;
        return mScreenHeigh;
    }

    protected void showSearchDialog(String value) {
        //  searchFragment.show(getSupportFragmentManager(), SearchFragment.TAG);
    }

    /**
     * 隐藏软键盘
     */
    protected   void hideSoftInput() {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
       try {
           imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
       }catch (Exception e){
           e.printStackTrace();
       }
    }

}
