package com.esaygo.app.common.base;

import android.text.TextUtils;


import com.esaygo.app.common.constan.Constans;
import com.esaygo.app.utils.AppUtils;
import com.esaygo.app.utils.LogUtils;
import com.esaygo.app.utils.network.NetworkUtils;
import com.esaygo.app.utils.network.common.HttpResponseBase;
import com.esaygo.app.utils.network.exception.ApiException;

import java.net.SocketTimeoutException;

import io.reactivex.subscribers.ResourceSubscriber;
import retrofit2.HttpException;

/**
 * 描述:统一处理订阅者
 */

public abstract class BaseSubscriber<T> extends ResourceSubscriber<T> {
    private BaseContract.BaseView mView;
     String mMsg;

    public BaseSubscriber(BaseContract.BaseView view) {
        this.mView = view;
    }


    public abstract void onSuccess(T t);

    public void onFailure(int code, String message) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!NetworkUtils.isConnected(AppUtils.getAppContext())) {
            // Logger.d("没有网络");
            mView.showError("网络异常，请检查网络");
        }
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onNext(T response) {
        if (mView == null) return;
        mView.complete();
        if (((HttpResponseBase<T>) response).code != 200) {
            if (((HttpResponseBase<T>) response).code == Constans.LOGIN_CONFIT) {
                mView.showError(Constans.LOGIN_CONFIT+"");
            } else {
                mView.showError(((HttpResponseBase<T>) response).message);
            }
        } else {
            onSuccess(response);
        }
    }


    @Override
    public void onError(Throwable e) {
        if (mView == null) return;
        mView.complete();//完成操作
        if (mMsg != null && !TextUtils.isEmpty(mMsg)) {
            mView.showError(mMsg);
        } else if (e instanceof ApiException) {
            mView.showError(e.toString());
        } else if (e instanceof SocketTimeoutException) {
            mView.showError("服务器响应超时ヽ(≧Д≦)ノ");
        } else if (e instanceof HttpException) {
            mView.showError("数据加载失败ヽ(≧Д≦)ノ");
        } else {
            mView.showError(e.getMessage());
            LogUtils.e("ERROR:" + e.toString());
        }
    }
}
