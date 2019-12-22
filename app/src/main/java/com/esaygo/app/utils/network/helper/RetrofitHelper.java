package com.esaygo.app.utils.network.helper;




import com.esaygo.app.module.main.presenter.MainPresenter;
import com.esaygo.app.module.query.modle.TrainBean;
import com.esaygo.app.module.query.presenter.QueryPresenter;
import com.esaygo.app.module.ticket.model.TaskDetails;
import com.esaygo.app.module.ticket.presenter.TicketPresenter;
import com.esaygo.app.module.user.UserModel;
import com.esaygo.app.module.user.bean.AuthCredential;
import com.esaygo.app.module.user.bean.Presenter;
import com.esaygo.app.module.user.presenter.LoginPresenter;
import com.esaygo.app.utils.network.api.ApiService;
import com.esaygo.app.utils.network.common.HttpResponseBase;
import com.esaygo.app.utils.network.common.HttpResquestBase;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Path;


/**
 * @author qianxunke
 * @email  736567805@qq.com
 * @date 2019-11-29 14:24
 * @desc RetrofitHelper 帮助类
 */
public class RetrofitHelper {
    private final ApiService mApiService;


    public RetrofitHelper(ApiService apiService) {
        this.mApiService = apiService;

    }

    public Flowable<HttpResponseBase<LoginPresenter.LoginResult>> doneLogin(AuthCredential authCredential) {
        return mApiService.doneApiLogin(authCredential);
    }
    public Flowable<HttpResponseBase<Object>> doneGetCode(LoginPresenter.SendCode sendCode) {
        return mApiService.doneGetCode(sendCode);
    }

    public Flowable<HttpResponseBase<LoginPresenter.LoginResult>> doneRegister(LoginPresenter.RegisterPar registerPar) {
        return mApiService.doneRegister(registerPar);
    }


    public Flowable<HttpResponseBase<List<TrainBean>>> doneQueryTrains(QueryPresenter.QueryBean queryBean) {
        return mApiService.doneQueryTrains(queryBean.train_date,queryBean.find_from,queryBean.find_to,queryBean.purpose_codes);
    }

    public Flowable<HttpResponseBase<List<Presenter>>> doneGetUserPresenter() {
        return mApiService.doneGetUserPresenter();
    }

    public Flowable<HttpResponseBase<Object>> doneAddTask(TaskDetails taskDetails) {
        return mApiService.doneAddTask(taskDetails);
    }

    public Flowable<HttpResponseBase<Object>> doneLogin12306(TicketPresenter.Login12306Request login12306Request) {
        return mApiService.doneLogin12306(login12306Request);
    }

    public Flowable<HttpResponseBase<MainPresenter.UpdateInfo>> doneUpdate() {
        return mApiService.doneUpdate();
    }



}
