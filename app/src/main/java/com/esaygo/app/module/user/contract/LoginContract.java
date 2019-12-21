package com.esaygo.app.module.user.contract;



import com.esaygo.app.common.base.BaseContract;
import com.esaygo.app.module.user.presenter.LoginPresenter;
import com.esaygo.app.utils.network.common.HttpResponseBase;


public interface LoginContract {
    /**
     * 给View提示
     */
    interface View extends BaseContract.BaseView{
        void showLoginResult(HttpResponseBase<LoginPresenter.LoginResult> datas);
        void showRegisterResult(HttpResponseBase<LoginPresenter.LoginResult> datas);
        void showSendCodeResult(HttpResponseBase<Object> datas);

    }

    /**
     * 执行登录操作
     * @param <T>
     */
    interface Presenter<T> extends BaseContract.BasePresenter<T>{

        void doneLogin(String phone, String code);

        void doneGetCode(String phone);

        void doneRegister(String phone,String code,String userNike);

    }
}
