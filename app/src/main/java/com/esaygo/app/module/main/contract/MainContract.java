package com.esaygo.app.module.main.contract;

import android.content.Context;

import com.esaygo.app.common.base.BaseContract;
import com.esaygo.app.module.main.bean.FeatureItem;
import com.esaygo.app.module.main.presenter.MainPresenter;
import com.esaygo.app.utils.network.common.HttpResponseBase;

import java.util.List;

public interface MainContract {

    /**
     * 给View提示
     */
    interface View extends BaseContract.BaseView{
        void showUpdate(HttpResponseBase<MainPresenter.UpdateInfo> datas);

        void showModules(List<FeatureItem> featureItems);
    }

    /**
     * 执行升级操作
     * @param <T>
     */
    interface Presenter<T> extends BaseContract.BasePresenter<T>{

        void doneUpdate();

        void doneGetModules(Context context);
    }


}
