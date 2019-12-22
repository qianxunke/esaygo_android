package com.esaygo.app.module.main.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.esaygo.app.PdaApplication;
import com.esaygo.app.R;
import com.esaygo.app.common.base.BaseFragment;
import com.esaygo.app.module.user.UserModel;
import com.esaygo.app.module.user.view.LoginActivity;
import com.gyf.immersionbar.ImmersionBar;
import com.tencent.android.tpush.XGPushManager;

import butterknife.BindView;

public class MeFragment extends  ImBaseFragment {

    @BindView(R.id.txt_go)
    TextView txt_go;
    @BindView(R.id.title)
    TextView title;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_me;
    }

    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this)
                .statusBarDarkFont(true, 0.2f) //原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
                .init();
    }
    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    public static MeFragment getInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        MeFragment fragment = new MeFragment();
        fragment.mBundle = bundle;
        return fragment;
    }

    @Override
    protected void loadData() {
        super.loadData();
    }

    @Override
    public void initWidget() {
        super.initWidget();
        title.setText("我的");
        txt_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XGPushManager.delAccount(mContext, UserModel.getCurrentUser().getMobile_phone());
                UserModel.deleteUserInfo();
                PdaApplication.getInstance().removeAllActivitys();
                startActivity(new Intent(mContext, LoginActivity.class));
            }
        });
    }
}
