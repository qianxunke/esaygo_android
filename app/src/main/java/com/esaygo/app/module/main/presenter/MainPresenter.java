package com.esaygo.app.module.main.presenter;

import android.content.Context;

import com.esaygo.app.R;
import com.esaygo.app.common.base.BaseSubscriber;
import com.esaygo.app.common.base.RxPresenter;
import com.esaygo.app.module.main.bean.FeatureItem;
import com.esaygo.app.module.main.contract.MainContract;
import com.esaygo.app.rx.RxUtils;
import com.esaygo.app.utils.network.common.HttpResponseBase;
import com.esaygo.app.utils.network.helper.RetrofitHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MainPresenter extends RxPresenter<MainContract.View> implements MainContract.Presenter<MainContract.View>  {
    RetrofitHelper mRetrofitHelper;

    @Inject
    public MainPresenter(RetrofitHelper retrofitHelper) {
        mRetrofitHelper = retrofitHelper;
    }


    @Override
    public void doneUpdate() {
        BaseSubscriber<HttpResponseBase<UpdateInfo>> subscriber =
                mRetrofitHelper.doneUpdate()
                        .compose(RxUtils.rxSchedulerHelper())
                        .subscribeWith(new BaseSubscriber<HttpResponseBase<UpdateInfo>>(mView) {
                            @Override
                            public void onSuccess(HttpResponseBase<UpdateInfo> o) {
                                mView.showUpdate(o);
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                            }

                        });
        addSubscribe(subscriber);
    }




    @Override
    public void doneGetModules(Context context) {
        List<FeatureItem> featureItems=new ArrayList<>();
        featureItems.add(new FeatureItem("1",context.getString(R.string.btn_scan_box_stock_in_text),"client_tran_storage",0));
        featureItems.add(new FeatureItem("2","BOX BARCODE","client_tran_storage",0));
        featureItems.add(new FeatureItem("3",context.getString(R.string.btn_import_scan_box_prepare_text),"client_tran_storage",0));
        featureItems.add(new FeatureItem("4",context.getString(R.string.btn_export_scan_palletize_text),"client_tran_storage",0));
        featureItems.add(new FeatureItem("5",context.getString(R.string.btn_export_palletize_verity_text),"client_tran_storage",0));
        featureItems.add(new FeatureItem("6",context.getString(R.string.btn_stockout_scan_text),"client_tran_storage",0));
        mView.showModules(featureItems);
    }


    /**
     * message UpdateInfo {
     *      string version=1;
     * 	 string url=2;
     * 	 string message=3;
     * 	 int64 is_force=4;
     * }
     */

    public static class UpdateInfo{
        String version;
        String url;
        String message;
        int is_force;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getIs_force() {
            return is_force;
        }

        public void setIs_force(int is_force) {
            this.is_force = is_force;
        }
    }
}
