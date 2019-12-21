package com.esaygo.app.rx;



import com.esaygo.app.utils.network.common.HttpResponseBase;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:RxUtils
 */
public class RxUtils {
    /**
     * 统一线程处理
     *
     * @param <T>
     * @return
     */
    public static <T> FlowableTransformer<T, T> rxSchedulerHelper() {    //compose简化线程 统一处理线程
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 生成Flowable
     * @param <T>
     * @return
     */
    public static <T> Flowable<T> createData(final T t) {
        return Flowable.create(emitter -> {
            try {
                if(((HttpResponseBase)t).code==200) {
                    emitter.onNext(t);
                    emitter.onComplete();
                }else {
                    emitter.onError((new Exception(((HttpResponseBase)t).message)));
                }
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        }, BackpressureStrategy.BUFFER);
    }
}
