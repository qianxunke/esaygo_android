package com.esaygo.app.module.query.contract;

import android.content.Context;

import com.esaygo.app.common.base.BaseContract;
import com.esaygo.app.module.query.modle.TrainBean;
import com.esaygo.app.utils.network.common.HttpResponseBase;


import java.util.List;

public interface QueryContract {

    /**
     * 给View提示
     */
    interface View extends BaseContract.BaseView {
        void showQuery(HttpResponseBase<List<TrainBean>> data);
    }

    /**
     * 执行升级操作
     *
     * @param <T>
     */
    interface Presenter<T> extends BaseContract.BasePresenter<T> {

        void donequery(String form,String to,String date,String purpose_codes);

    }


}
