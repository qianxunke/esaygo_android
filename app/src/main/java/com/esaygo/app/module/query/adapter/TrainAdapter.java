package com.esaygo.app.module.query.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.esaygo.app.R;
import com.esaygo.app.module.query.modle.TrainBean;
import com.esaygo.app.utils.StatonUtils;

import java.util.List;

public class TrainAdapter extends BaseQuickAdapter<TrainBean, BaseViewHolder> {


    public TrainAdapter(List data) {
        super(R.layout.item_train, data);

    }

    public TrainAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, TrainBean item) {

        //判断是高铁还是慢车，K开头慢车 ，其他快车
        if (item.getNum().startsWith("K")) {
            helper.setVisible(R.id.ll_item_gaotie, false);
            helper.setVisible(R.id.ll_item_manche, true);
            helper.setText(R.id.tx_item_train_yingzuo, "硬座(" + item.getYz() + ")");
            helper.setText(R.id.tx_item_train_yingwo, "硬卧(" + item.getYw() + ")");
            helper.setText(R.id.tx_item_train_ruanwo, "软卧(" + item.getRw() + ")");
            helper.setText(R.id.tx_item_train_wuzuo, "无座(" + item.getWz() + ")");
        } else {
            helper.setVisible(R.id.ll_item_manche, false);
            helper.setVisible(R.id.ll_item_gaotie, true);
            helper.setText(R.id.tx_item_train_erdengzuo, "二等座(" + item.getYw() + ")");
            helper.setText(R.id.tx_item_train_yidengzuo, "一等座(" + item.getRw() + ")");
            helper.setText(R.id.tx_item_train_shangwuzuo, "商务座(" + item.getWz() + ")");
        }

        if (item.getCost_time() != null) {
            String[] s = item.getCost_time().split(":");
            helper.setText(R.id.tx_item_train_spend_time, s[0] + "小时" + s[1] + "分钟");
            int day = Integer.parseInt(s[0]) / 24;

            helper.setText(R.id.tx_item_train_spend_days, "+" + day);
            helper.setVisible(R.id.tx_item_train_spend_days, day > 0);

        }

        helper.setText(R.id.tx_item_train_start_time, item.getStart_time() + "");
        helper.setText(R.id.tx_item_train_start_station, StatonUtils.getStationFromCode(item.getFrom()) + "");
        helper.setText(R.id.tx_item_train_end_time, item.getEnd_time() + "");
        helper.setText(R.id.tx_item_train_end_station, StatonUtils.getStationFromCode(item.getTo()) + "");
        helper.setText(R.id.tx_item_train_number, item.getNum() + "");
    }
}
