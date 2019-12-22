package com.esaygo.app.module.main.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.Nullable;

import com.esaygo.app.PdaApplication;
import com.esaygo.app.R;
import com.esaygo.app.di.component.DaggerActivityComponent;
import com.esaygo.app.di.module.ActivityModule;
import com.esaygo.app.module.main.contract.SplashContract;
import com.esaygo.app.module.main.presenter.SplashPresenter;
import com.esaygo.app.module.user.UserModel;
import com.esaygo.app.module.user.view.LoginActivity;
import com.esaygo.app.utils.StatusBarUtil;
import com.esaygo.app.utils.ToastUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends RxAppCompatActivity implements SplashContract.View {
    @Inject
    SplashPresenter mPresenter;
    @BindView(R.id.iv_splash)
    ImageView mIvSplash;
    @BindView(R.id.tv_count_down)
    TextView mTvCountDown;
    @BindView(R.id.ll_count_down)
    LinearLayout mLlCountDown;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        //设置透明
        StatusBarUtil.setTransparent(this);
        ButterKnife.bind(this);
        initInject();
        initWidget();
        loadData();

    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) mPresenter.detachView();
        super.onDestroy();
    }

    private void loadData() {
        mPresenter.getSplashData();
        mPresenter.setCountDown();
    }

    private void initWidget() {
        RxView.clicks(mLlCountDown)
                .throttleFirst(2, TimeUnit.SECONDS)//3秒内响应第一次发射数据
                .compose(bindToLifecycle())
                .subscribe(object -> redirect());
    }

    /**
     * 注入依赖
     */
    private void initInject() {
        DaggerActivityComponent.builder()
                .appComponent(PdaApplication.getInstance().getAppComponent())
                .activityModule(new ActivityModule(this))
                .build()
                .inject(this);
        mPresenter.attachView(this);//依赖 保持p和v生命周期一致
    }

    @Override
    public void showError(String msg) {
        //设置默认图片
        mIvSplash.setImageResource(R.mipmap.splash);
    }


    @Override
    public void complete() {

    }

    @Override
    public void showSplash() {
        /*
        if (!splash.data.isEmpty()) {
            int pos = new Random().nextInt(splash.data.size());
            Glide.with(this)
                    .load(splash.data.get(pos).thumb)
                    //.load("http://i0.hdslb.com/bfs/archive/ba17d4df28fb0c28c8f596082d7328b4415ee28b.png")
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .into(mIvSplash);
        } else {
            mIvSplash.setImageResource(R.mipmap.ic_default_bg);
        }
        */
    }

    @Override
    public void showCountDown(int count) {
        mTvCountDown.setText(count + "");
        if (count == 0) {
            redirect();
        }
    }

    /**
     * 跳转到首页
     */
    private void redirect() {
        //    boolean flag = PrefsUtils.getInstance().getBoolean(SyncStateContract.Constants.IS_LOGINED_FLAG, false);
        //   flag = false;
        if (UserModel.getCurrentUser() != null) {
            XGPushManager.appendAccount(PdaApplication.getInstance(), UserModel.getCurrentUser().getMobile_phone(),
                    new XGIOperateCallback() {
                        @Override
                        public void onSuccess(Object data, int flag) {
                            Log.d("TPush", "注册成功，设备token为：" + data);
                        }

                        @Override
                        public void onFail(Object data, int errCode, String msg) {
                            Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
                        }
                    });
          //  TagAliasOperatorHelper.getInstance().handleNewAction(getApplicationContext(), UserModel.getCurrentUser().getUser_id()+"");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

    }
}
