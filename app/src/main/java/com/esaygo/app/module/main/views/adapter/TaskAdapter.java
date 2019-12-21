package com.esaygo.app.module.main.views.adapter;

import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.esaygo.app.R;
import com.esaygo.app.common.constan.Constans;
import com.esaygo.app.module.query.modle.TrainBean;
import com.esaygo.app.module.ticket.model.TaskDetails;
import com.esaygo.app.module.user.UserModel;
import com.esaygo.app.utils.StatonUtils;
import com.esaygo.app.utils.ToastUtils;
import com.esaygo.app.utils.network.support.ApiConstants;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.List;

public class TaskAdapter extends BaseQuickAdapter<TaskDetails, BaseViewHolder> {

    public TaskAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TaskDetails item) {


        helper.setText(R.id.tx_station_name, StatonUtils.getStationFromCode(item.getTask().getFind_from()) + "-" + StatonUtils.getStationFromCode(item.getTask().getFind_to()));

        helper.setText(R.id.tx_checi, item.getTask().getTrips());

        String chengche = "";
        for (int i = 0; i < item.getTask_passenger().length; i++) {
            chengche += item.getTask_passenger()[i].getName() + " ";
        }
        chengche = item.getTask().getTrain_dates() + "出发，乘车人：" + chengche;
        helper.setText(R.id.tx_miaoshu, chengche);

        if (item.getTask().getStatus() == 0) {
            helper.setText(R.id.tx_status, "抢票失败");
            helper.setVisible(R.id.tx_action, false);
        } else if (item.getTask().getStatus() == 1 || item.getTask().getStatus() == 2 || item.getTask().getStatus() == 4) {
            helper.setText(R.id.tx_status, item.getTask().getStatus() == 4 ? "待抢票" : "抢票中");
            helper.setText(R.id.tx_action, "取消抢票");
            helper.setOnClickListener(R.id.tx_action, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    OkGo.<String>post(ApiConstants.API_BASE_URL + "book/task/updateStatus?taskId=" + item.getTask().getTask_id() + "&status=5")
                            .headers("Authorization", UserModel.getCurrentUser().getToken())
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {
                                    v.setEnabled(true);
                                    try {
                                        JSONObject jsonObject = JSONObject.parseObject(response.body());
                                        if (jsonObject.getInteger("code") == Constans.API_RESULT_OK) {
                                            helper.setText(R.id.tx_status, "已取消");
                                            helper.setVisible(R.id.tx_action, false);
                                        } else {
                                            ToastUtils.showWarningToast(jsonObject.getString("message") + "");
                                        }
                                    } catch (Exception e) {
                                        ToastUtils.showWarningToast(e.toString());
                                    }
                                }
                            });
                }
            });
            //  helper.setVisible(R.id.tx_action,false);
        } else if (item.getTask().getStatus() == 3) {
            helper.setText(R.id.tx_status, "抢票成功");
            helper.setVisible(R.id.tx_action, false);
        } else if (item.getTask().getStatus() == 5) {
            helper.setText(R.id.tx_status, "已取消");
            helper.setVisible(R.id.tx_action, false);
        }

    }
}
