package com.esaygo.app.module.user.view;

import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.esaygo.app.PdaApplication;
import com.esaygo.app.R;
import com.esaygo.app.common.base.BaseActivity;
import com.esaygo.app.common.constan.Constans;
import com.esaygo.app.module.main.views.MainActivity;
import com.esaygo.app.module.user.UserModel;
import com.esaygo.app.module.user.contract.LoginContract;
import com.esaygo.app.module.user.presenter.LoginPresenter;
import com.esaygo.app.rx.RxBus;
import com.esaygo.app.rx.event.Event;
import com.esaygo.app.utils.PrefsUtils;
import com.esaygo.app.utils.ToastUtils;
import com.esaygo.app.utils.network.common.HttpResponseBase;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;

public class RegisterActivity extends BaseActivity<LoginPresenter> implements LoginContract.View {

    @BindView(R.id.et_phone)
    EditText et_phone;
    @BindView(R.id.et_code)
    EditText et_code;
    @BindView(R.id.txt_code)
    TextView txt_code;
    @BindView(R.id.txt_register)
    TextView txt_register;
    @BindView(R.id.et_invite_code)
    EditText et_invite_code;
    @BindView(R.id.iv_choose)
    CheckBox iv_choose;

    @BindView(R.id.iv_left)
    ImageView iv_left;

    private TimeCount time;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initToolbar() {
        mBack = false;
        super.initToolbar();

    }

    @Override
    public void showRegisterResult(HttpResponseBase<LoginPresenter.LoginResult> datas) {
        promptDialog.dismiss();
        if (datas.code == Constans.API_RESULT_OK) {
            // PdaApplication.getInstance().removeAllActivitys();
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
            ToastUtils.showSucessToast(datas.message);
            ToastUtils.Speak(datas.message);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(mContext, MainActivity.class));
                    finish();
                }
            }, 200);

        } else {

            ToastUtils.showErrorToast(datas.message);
        }
    }

    @Override
    public void showLoginResult(HttpResponseBase<LoginPresenter.LoginResult> datas) {


    }

    @Override
    public void showSendCodeResult(HttpResponseBase<Object> datas) {
        if (datas.code == Constans.API_RESULT_OK) {
            ToastUtils.showSucessToast(datas.message);
        } else {
            ToastUtils.showErrorToast(datas.message);
        }
    }


    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }


    @Override
    protected void initWidget() {
        super.initWidget();
        String default_user_name = PrefsUtils.getInstance().getString(UserModel.DEFAULT_USER_NAME, "");
        if (!default_user_name.equals("")) {
            et_phone.setText(default_user_name);
            et_phone.setSelection(et_phone.getText().length());
        }
        iv_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iv_choose.isClickable()){
                    iv_choose.setSelected(true);
                }else {
                    iv_choose.setSelected(false);
                }
            }
        });

        txt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPhone(et_phone.getText().toString())) {
                    return;
                }
                if (et_invite_code.getText().toString().isEmpty()) {
                    ToastUtils.showWarningToast("请输入您的昵称，方便称号");
                    return;
                }
                if (et_code.getText().toString().isEmpty()) {
                    ToastUtils.showWarningToast("请先输入验证码");
                    return;
                }
                promptDialog.showLoading(getString(R.string.logging_in));
                mPresenter.doneRegister(et_phone.getText().toString(), et_code.getText().toString(), et_invite_code.getText().toString());
            }
        });
        time = new TimeCount(60000, 1000);
        txt_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPhone(et_phone.getText().toString())) {
                    return;
                }
                mPresenter.doneGetCode(et_phone.getText().toString());
                time.start();
            }
        });


        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_choose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                iv_choose.setChecked(isChecked);
                if (isChecked) {
                    txt_register.setEnabled(true);
                    txt_register.setBackground(getResources().getDrawable(R.drawable.bg_login_btn));
                }else {
                    txt_register.setEnabled(true);
                    txt_register.setBackground(getResources().getDrawable(R.drawable.bg_invite_friend_used_btn));
                }

            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();//注释掉这行,back键不退出activity
        PdaApplication.getInstance().removeAllActivitys();
    }

    /**
     * 短信倒计时
     */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            txt_code.setClickable(false);
            txt_code.setText(((millisUntilFinished - 1) / 1000) + "s");
        }

        @Override
        public void onFinish() {
            txt_code.setText("获取验证码");
            txt_code.setClickable(true);
        }
    }

    public boolean isPhone(String phone) {
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        if (phone.length() != 11) {
            ToastUtils.showWarningToast("手机号应为11位数");
            return false;
        } else {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(phone);
            boolean isMatch = m.matches();
            if (!isMatch) {
                ToastUtils.showWarningToast("请填入正确的手机号");
            }
            return isMatch;
        }
    }
}
