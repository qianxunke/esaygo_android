package com.esaygo.app.module.user.presenter;


import com.esaygo.app.common.base.BaseSubscriber;
import com.esaygo.app.common.base.RxPresenter;
import com.esaygo.app.common.constan.Constans;
import com.esaygo.app.module.user.UserModel;
import com.esaygo.app.module.user.bean.AuthCredential;
import com.esaygo.app.module.user.contract.LoginContract;
import com.esaygo.app.rx.RxUtils;
import com.esaygo.app.utils.PrefsUtils;
import com.esaygo.app.utils.network.common.HttpResponseBase;
import com.esaygo.app.utils.network.helper.RetrofitHelper;


import javax.inject.Inject;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;


public class LoginPresenter extends RxPresenter<LoginContract.View> implements LoginContract.Presenter<LoginContract.View> {
    RetrofitHelper mRetrofitHelper;

    @Inject
    public LoginPresenter(RetrofitHelper retrofitHelper) {
        mRetrofitHelper = retrofitHelper;
    }

    @Override
    public void doneLogin(String phone, String code) {
        BaseSubscriber<HttpResponseBase<LoginResult>> subscriber =
                mRetrofitHelper.doneLogin(new AuthCredential(phone, code))
                        .compose(RxUtils.rxSchedulerHelper())
                        .subscribeWith(new BaseSubscriber<HttpResponseBase<LoginResult>>(mView) {
                            @Override
                            public void onSuccess(HttpResponseBase<LoginResult> o) {
                                //  parsingJwt(o);
                                if (o.code == Constans.API_RESULT_OK) {
                                    String token = o.data.token;
                                    o.data.user.setToken(token);
                                    UserModel.insertCurrentUser(o.data.user);

                                }
                                mView.showLoginResult(o);
                            }


                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                            }

                        });
        addSubscribe(subscriber);
    }

    @Override
    public void doneGetCode(String phone) {
        BaseSubscriber<HttpResponseBase<Object>> subscriber =
                mRetrofitHelper.doneGetCode(new SendCode(phone))
                        .compose(RxUtils.rxSchedulerHelper())
                        .subscribeWith(new BaseSubscriber<HttpResponseBase<Object>>(mView) {
                            @Override
                            public void onSuccess(HttpResponseBase<Object> o) {
                                //  parsingJwt(o);
                                mView.showSendCodeResult(o);
                            }


                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                            }

                        });
        addSubscribe(subscriber);
    }

    @Override
    public void doneRegister(String phone, String code, String userNike) {
        BaseSubscriber<HttpResponseBase<LoginResult>> subscriber =
                mRetrofitHelper.doneRegister(new RegisterPar(userNike,phone, code))
                        .compose(RxUtils.rxSchedulerHelper())
                        .subscribeWith(new BaseSubscriber<HttpResponseBase<LoginResult>>(mView) {
                            @Override
                            public void onSuccess(HttpResponseBase<LoginResult> o) {
                                //  parsingJwt(o);
                                if (o.code == Constans.API_RESULT_OK) {
                                    String token = o.data.token;
                                    o.data.user.setToken(token);
                                    UserModel.insertCurrentUser(o.data.user);

                                }
                                mView.showRegisterResult(o);
                            }


                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                            }

                        });
        addSubscribe(subscriber);
    }

    public static class SendCode {

       /* {
            "telephone":"18334142052",
                "code":""
        }

        */

        public SendCode(String telephone) {
            this.telephone = telephone;
        }

        String telephone;
        String code;

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }


    public static class LoginResult {
        String token;
        UserModel.User user;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public UserModel.User getUser() {
            return user;
        }

        public void setUser(UserModel.User user) {
            this.user = user;
        }
    }


    public static class RegisterPar {

        /**
         * {
         * "nike_name":"千寻客",
         * "user_name":"10010",
         * "password":"123456",
         * "mobile_phone":"18334142052",
         * "user_email":"736567805@qq.com",
         * "verification_code":"770304"
         * }
         */

        String nike_name;
        String user_name;
        String password;
        String mobile_phone;
        String user_email;
        String verification_code;

        public RegisterPar(String nike_name, String mobile_phone, String verification_code) {
            this.nike_name = nike_name;
            this.mobile_phone = mobile_phone;
            this.verification_code = verification_code;
            this.password="123456";
            this.user_email="123456@qq.com";
            this.user_name="10086";
        }

        public String getNike_name() {
            return nike_name;
        }

        public void setNike_name(String nike_name) {
            this.nike_name = nike_name;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getMobile_phone() {
            return mobile_phone;
        }

        public void setMobile_phone(String mobile_phone) {
            this.mobile_phone = mobile_phone;
        }

        public String getUser_email() {
            return user_email;
        }

        public void setUser_email(String user_email) {
            this.user_email = user_email;
        }

        public String getVerification_code() {
            return verification_code;
        }

        public void setVerification_code(String verification_code) {
            this.verification_code = verification_code;
        }
    }


}
