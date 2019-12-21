package com.esaygo.app.module.main.views;

import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.esaygo.app.R;
import com.esaygo.app.common.constan.Constans;
import com.esaygo.app.module.main.views.adapter.TaskAdapter;
import com.esaygo.app.module.ticket.model.TaskDetails;
import com.esaygo.app.module.user.UserModel;
import com.esaygo.app.utils.ToastUtils;
import com.esaygo.app.utils.network.support.ApiConstants;
import com.gyf.immersionbar.ImmersionBar;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.Response;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class BookFragment extends ImBaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.title)
    TextView title;

    List<TaskDetails> taskDetailsList;
    TaskAdapter taskAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_book;
    }

    public static BookFragment getInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        BookFragment fragment = new BookFragment();
        fragment.mBundle = bundle;
        return fragment;
    }


    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this)
                .statusBarDarkFont(true, 0.2f) //原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
                .init();
    }

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        title.setText("任务列表");
        taskDetailsList = new ArrayList<>();

        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                loadData();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        taskAdapter = new TaskAdapter(R.layout.item_book_task, taskDetailsList);
        recyclerView.setAdapter(taskAdapter);
    }

    public void loadData() {
        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.put("Authorization",UserModel.getCurrentUser().getToken());
        OkGo.<String>get(ApiConstants.API_BASE_URL + "book/task/userList")
                .headers(httpHeaders)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = JSONObject.parseObject(response.body());
                            if (jsonObject.getInteger("code") == Constans.API_RESULT_OK) {
                                List<TaskDetails> taskDetails = JSONArray.parseArray(jsonObject.getString("data"), TaskDetails.class);
                                showTask(taskDetails);
                            } else {
                                ToastUtils.showWarningToast("请重新刷新看看");
                            }
                        } catch (Exception e) {
                            ToastUtils.showWarningToast(e.toString());
                            ToastUtils.showWarningToast(e.toString());
                        }
                    }
                });
    }

    private void showTask(List<TaskDetails> data) {
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
        taskDetailsList.clear();
        taskAdapter.notifyDataSetChanged();
        if (data != null && data.size() > 0) {
            gone(R.id.rl_nodata);
            taskDetailsList.addAll(data);
            taskAdapter.notifyDataSetChanged();
        } else {
            visible(R.id.rl_nodata);

        }
    }
}
