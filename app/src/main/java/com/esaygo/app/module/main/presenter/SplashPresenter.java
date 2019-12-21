package com.esaygo.app.module.main.presenter;




import com.esaygo.app.common.base.RxPresenter;
import com.esaygo.app.module.main.contract.SplashContract;
import com.esaygo.app.rx.RxUtils;
import com.esaygo.app.utils.network.helper.RetrofitHelper;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;

/**
 * @author zzq  作者 E-mail:   soleilyoyiyi@gmail.com
 * @date 创建时间：2017/5/23 9:44
 * 描述:启动界面Presenter
 */

public class SplashPresenter extends RxPresenter<SplashContract.View> implements SplashContract.Presenter<SplashContract.View> {
    private RetrofitHelper mRetrofitHelper;


    @Inject
    public SplashPresenter(RetrofitHelper retrofitHelper) {
        mRetrofitHelper = retrofitHelper;
    }

    @Override
    public void getSplashData() {
        /*
        BaseSubscriber<Splash> subscriber = mRetrofitHelper.getSplash()
                .compose(RxUtils.rxSchedulerHelper())
                .subscribeWith(new BaseSubscriber<Splash>(mView) {
                    @Override
                    public void onSuccess(Splash splash) {
                        if (splash.code == 0)
                            mView.showSplash(splash);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                            mView.showError(message);
                    }
                });
        addSubscribe(subscriber);
*/

    }

    /**
     * 5s 倒计时
     */
    @Override
    public void setCountDown() {
        final Long count = 3L;
        Disposable subscribe = Flowable.interval(0, 1, TimeUnit.SECONDS)
                .map(aLong -> count - aLong)
                .take(count + 1)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(aLong -> mView.showCountDown(aLong.intValue()));
        addSubscribe(subscribe);
    }

}
