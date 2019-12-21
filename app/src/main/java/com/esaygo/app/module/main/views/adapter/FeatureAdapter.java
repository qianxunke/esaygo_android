package com.esaygo.app.module.main.views.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.esaygo.app.R;
import com.esaygo.app.module.main.bean.FeatureItem;


import java.util.List;

public class FeatureAdapter extends BaseQuickAdapter<FeatureItem, BaseViewHolder> {

    public FeatureAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FeatureItem item) {
        ImageView img_feature = helper.getView(R.id.img_feature);
        Glide.with(mContext)
                .load("android.resource://com.ftlexpress.faxiangpda/raw/" + item.getLogo_title().trim())
                .into(img_feature);
        helper.setText(R.id.tx_feature_name, item.getTitle());
        helper.setText(R.id.tx_feature_num, item.getTask_count() > 99 ? "99+" : item.getTask_count() + "");
        helper.setVisible(R.id.tx_feature_num, item.getTask_count() > 0);
    }
}
