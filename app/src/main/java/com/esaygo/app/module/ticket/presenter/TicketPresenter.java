package com.esaygo.app.module.ticket.presenter;

import com.esaygo.app.common.base.BaseSubscriber;
import com.esaygo.app.common.base.RxPresenter;
import com.esaygo.app.common.constan.Constans;
import com.esaygo.app.module.query.modle.TrainBean;
import com.esaygo.app.module.ticket.contract.TicketContract;
import com.esaygo.app.module.ticket.model.TaskDetails;
import com.esaygo.app.module.user.UserModel;
import com.esaygo.app.module.user.bean.Presenter;
import com.esaygo.app.rx.RxUtils;
import com.esaygo.app.utils.network.common.HttpResponseBase;
import com.esaygo.app.utils.network.helper.RetrofitHelper;

import java.util.List;

import javax.inject.Inject;

public class TicketPresenter extends RxPresenter<TicketContract.View> implements TicketContract.Presenter<TicketContract.View> {


    RetrofitHelper mRetrofitHelper;

    @Inject
    public TicketPresenter(RetrofitHelper retrofitHelper) {
        mRetrofitHelper = retrofitHelper;
    }


    @Override
    public void doneAddTask(TaskDetails taskDetails) {
        BaseSubscriber<HttpResponseBase<Object>> subscriber =
                mRetrofitHelper.doneAddTask(taskDetails)
                        .compose(RxUtils.rxSchedulerHelper())
                        .subscribeWith(new BaseSubscriber<HttpResponseBase<Object>>(mView) {
                            @Override
                            public void onSuccess(HttpResponseBase<Object> o) {
                                mView.shoAddTaskResult(o);
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                            }

                        });
        addSubscribe(subscriber);
    }

    @Override
    public void doneLogin12306(String account,String pwd) {
        UserModel.User user= UserModel.getCurrentUser();
        Login12306Request login12306Request=new Login12306Request();
        login12306Request.setUser_id(user.getUser_id());
        login12306Request.setTran_user_account(account);
        login12306Request.setTran_user_pwd(pwd);
        BaseSubscriber<HttpResponseBase<Object>> subscriber =
                mRetrofitHelper.doneLogin12306(login12306Request)
                        .compose(RxUtils.rxSchedulerHelper())
                        .subscribeWith(new BaseSubscriber<HttpResponseBase<Object>>(mView) {
                            @Override
                            public void onSuccess(HttpResponseBase<Object> o) {
                                mView.showLogin12306(o);
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                            }

                        });
        addSubscribe(subscriber);

    }


    @Override
    public void doneGetUserPresenter() {

        BaseSubscriber<HttpResponseBase<List<Presenter>>> subscriber =
                mRetrofitHelper.doneGetUserPresenter()
                        .compose(RxUtils.rxSchedulerHelper())
                        .subscribeWith(new BaseSubscriber<HttpResponseBase<List<Presenter>>>(mView) {
                            @Override
                            public void onSuccess(HttpResponseBase<List<Presenter>> o) {
                                mView.showUserPresenters(o);
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                            }

                        });
        addSubscribe(subscriber);

    }

    public static class Login12306Request{

        /**
         * {
         * "user_id":"a773b4c0-666f-4f4a-876a-1e3ed840b5e1",
         * "tran_user_account":"dh17862709691",
         * "tran_user_pwd":"736567805"
         * }
         */
        String user_id;
        String tran_user_account;
        String tran_user_pwd;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getTran_user_account() {
            return tran_user_account;
        }

        public void setTran_user_account(String tran_user_account) {
            this.tran_user_account = tran_user_account;
        }

        public String getTran_user_pwd() {
            return tran_user_pwd;
        }

        public void setTran_user_pwd(String tran_user_pwd) {
            this.tran_user_pwd = tran_user_pwd;
        }
    }

}
