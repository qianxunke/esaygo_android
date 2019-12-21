package com.esaygo.app.module.main.contract;

import android.content.Context;

import com.esaygo.app.common.base.BaseContract;
import com.esaygo.app.module.main.bean.FeatureItem;

import java.util.List;

public interface MainContract {

    /**
     * 给View提示
     */
    interface View extends BaseContract.BaseView{
        void showUpdate(String rsp);

        void showModules(List<FeatureItem> featureItems);
    }

    /**
     * 执行升级操作
     * @param <T>
     */
    interface Presenter<T> extends BaseContract.BasePresenter<T>{

        void doneUpdate(String local_version);

        void doneGetModules(Context context);
    }


}
