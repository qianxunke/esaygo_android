package com.esaygo.app.module.main.presenter;

import android.content.Context;

import com.esaygo.app.R;
import com.esaygo.app.common.base.RxPresenter;
import com.esaygo.app.module.main.bean.FeatureItem;
import com.esaygo.app.module.main.contract.MainContract;
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
    public void doneUpdate(String local_version) {

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
}
