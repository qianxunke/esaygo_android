package com.esaygo.app.module.main.views;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.esaygo.app.R;
import com.esaygo.app.common.constan.Constans;
import com.esaygo.app.module.main.views.adapter.TaskAdapter;
import com.esaygo.app.module.query.modle.TrainBean;
import com.esaygo.app.module.query.presenter.QueryPresenter;
import com.esaygo.app.module.ticket.model.TaskDetails;
import com.esaygo.app.module.ticket.views.TicketActivity;
import com.esaygo.app.module.user.UserModel;
import com.esaygo.app.utils.ToastUtils;
import com.esaygo.app.utils.book.bean.BookResult;
import com.esaygo.app.utils.book.bean.CheckVCodeResult;
import com.esaygo.app.utils.book.bean.LoginCheckResult;
import com.esaygo.app.utils.book.utils.BookUtils;
import com.esaygo.app.utils.book.utils.ConnectionUtils;
import com.esaygo.app.utils.book.utils.LoginUtils;
import com.esaygo.app.utils.book.utils.QueryUtils;
import com.esaygo.app.utils.book.vcode.IdentifyVCode;
import com.esaygo.app.utils.network.support.ApiConstants;
import com.gyf.immersionbar.ImmersionBar;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.Response;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class BookFragment extends ImBaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.fillStatusBarView)
    View statusBarView;
    RecyclerView messageRecycleView;
    List<TaskDetails> taskDetailsList;
    TaskAdapter taskAdapter;

    List<Day> dayList;
    DayAdapter dayAdapter;
    boolean isTask = false;

    List<String> messages;
    MassageAdapter massageAdapter;
    MyHandler myHandler;

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
        myHandler = new MyHandler();

        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
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
        dayList = new ArrayList<>();
        dayAdapter = new DayAdapter(R.layout.item_seat_type, dayList);

        messages = new ArrayList<>();
        massageAdapter = new MassageAdapter(R.layout.item_runing_massage, messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        taskAdapter = new TaskAdapter(R.layout.item_book_task, taskDetailsList, this);
        recyclerView.setAdapter(taskAdapter);
    }

    public void loadData() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put("Authorization", UserModel.getCurrentUser().getToken());
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


    public class DayAdapter extends BaseQuickAdapter<Day, BaseViewHolder> {

        public DayAdapter(int layoutResId, List data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, Day item) {

            helper.setText(R.id.tx_item_select_seat_name, item.day);

            ImageView selectImageview = helper.getView(R.id.iv_choose);
            selectImageview.setSelected(item.isSelect);

            helper.setOnClickListener(R.id.iv_choose, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.isSelect) {
                        item.isSelect = false;
                    } else {
                        item.isSelect = true;
                    }
                    notifyDataSetChanged();
                }
            });

        }
    }


    public static class Day {
        String day;
        boolean isSelect;

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }
    }

    public void startTicke(TaskDetails taskDetails) {
        //先让用户选择日期
        String[] days = taskDetails.getTask().getTrain_dates().split(",");
        dayList.clear();
        for (int i = 0; i < days.length; i++) {
            Day dayItem = new Day();
            dayItem.setDay(days[i]);
            dayItem.setSelect(false);
            dayList.add(dayItem);
        }
        //
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_select_ticket)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    public void convertView(final ViewHolder holder, final BaseNiceDialog dialog) {
                        RecyclerView recyclerView = holder.getView(R.id.xrecleview_order_list);
                        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                        recyclerView.setAdapter(dayAdapter);
                        dayAdapter.notifyDataSetChanged();
                        EditText input = holder.getView(R.id.tx_input);
                        holder.setOnClickListener(R.id.tx_cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.tx_ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                List<String> days = new ArrayList<>();
                                for (int i = 0; i < dayList.size(); i++) {
                                    if (dayList.get(i).isSelect) {
                                        days.add(dayList.get(i).day);
                                    }
                                }
                                if (days.size() == 0) {
                                    ToastUtils.showWarningToast("请先选择抢票日期");
                                    return;
                                }
                                int shulv = 0;
                                if (input.getText().toString().isEmpty()) {
                                    shulv = 1000;
                                } else {
                                    shulv = new Double(Double.parseDouble(input.getText().toString()) * 1000).intValue();
                                }
                                final int shudu=shulv;
                                promptDialog.showLoading("请稍等...");
                                OkGo.<String>post(ApiConstants.API_BASE_URL + "book/task/updateStatus?taskId=" + taskDetails.getTask().getTask_id() + "&status=6")
                                        .headers("Authorization", UserModel.getCurrentUser().getToken())
                                        .execute(new StringCallback() {
                                            @Override
                                            public void onSuccess(Response<String> response) {
                                                v.setEnabled(true);
                                                try {
                                                    JSONObject jsonObject = JSONObject.parseObject(response.body());
                                                    if (jsonObject.getInteger("code") == Constans.API_RESULT_OK) {
                                                        promptDialog.dismiss();
                                                        dialog.dismiss();
                                                        messages.clear();
                                                        massageAdapter.notifyDataSetChanged();
                                                        doneTicket(taskDetails, days, shudu);
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

                    }
                })
                .setMargin(16)
                .setOutCancel(false)
                .show(getChildFragmentManager());
        //判断当前抢票任务是否有效

    }

    private void doneTicket(TaskDetails taskDetails, List<String> datas, int shulv) {
        isTask = true;
        (new MyThead(taskDetails, datas, shulv)).start();
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_ticket_running)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    public void convertView(final ViewHolder holder, final BaseNiceDialog dialog) {
                        messageRecycleView = holder.getView(R.id.xrecleview_order_list);
                        messageRecycleView.setLayoutManager(new LinearLayoutManager(mContext));
                        messageRecycleView.setAdapter(massageAdapter);
                        massageAdapter.notifyDataSetChanged();
                        holder.setOnClickListener(R.id.tx_ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isTask = false;
                                dialog.dismiss();
                            }
                        });

                    }
                })
                .setMargin(16)
                .setOutCancel(false)
                .show(getChildFragmentManager());

    }


    public class MassageAdapter extends BaseQuickAdapter<String, BaseViewHolder> {


        public MassageAdapter(int layoutResId, List data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {

            helper.setText(R.id.tx_massage_contxt, item);

        }
    }


    public class MyThead extends Thread {
        TaskDetails taskDetails;
        List<String> datas;
        int shulv;

        public MyThead(TaskDetails taskDetails, List<String> datas, int shulv) {
            super();
            this.taskDetails = taskDetails;
            this.datas = datas;
            this.shulv = shulv;
        }

        @Override
        public void run() {
            try {
                //登陆
                OkHttpClient okHttpClient = ConnectionUtils.getClient(15000);
                Message massage = new Message();
                //登陆3次，如果不成功，先查询最后再登陆
                int loginError = 0;
                massage = new Message();
                massage.what = 1;
                massage.obj = "先尝试登陆，如果成功，直接抢....";
                myHandler.sendMessage(massage);

                while (true) {
                    if (loginError > 3) {
                        massage = new Message();
                        massage.what = 1;
                        massage.obj = "登陆失败三次，先放弃登陆，直接先抢票，抢到票再登陆购买....";
                        myHandler.sendMessage(massage);
                        break;
                    }
                    //开始登陆
                    LoginCheckResult loginCheckResult = new LoginCheckResult();
                    okHttpClient = ConnectionUtils.getClient(15000);
                    loginCheckResult = LoginUtils.CheckCodeAndLogin(okHttpClient);

                    if (!loginCheckResult.isGetCode()) {
                        massage = new Message();
                        massage.what = 1;
                        massage.obj = "获取验证码失败，重试....";
                        myHandler.sendMessage(massage);
                        Thread.sleep(1000);
                        loginError++;
                        continue;
                    }
                    if (!loginCheckResult.isLocalCode()) {
                        massage = new Message();
                        massage.what = 1;
                        massage.obj = "云打码失败，重试....";
                        myHandler.sendMessage(massage);
                        Thread.sleep(1000);
                        loginError++;
                        continue;
                    }
                    if (!loginCheckResult.isCodeCheck()) {
                        massage = new Message();
                        massage.what = 1;
                        massage.obj = "验证码验证失败，重试....";
                        myHandler.sendMessage(massage);
                        Thread.sleep(1000);
                        loginError++;
                        continue;
                    }
                    if (!loginCheckResult.isCheckUser()) {
                        massage = new Message();
                        massage.what = 1;
                        massage.obj = "登陆12306失败，重试....";
                        myHandler.sendMessage(massage);
                        Thread.sleep(1000);
                        loginError++;
                        continue;
                    }
                    if (!loginCheckResult.isGetToken()) {
                        massage = new Message();
                        massage.what = 1;
                        massage.obj = "获取token失败，重试....";
                        myHandler.sendMessage(massage);
                        Thread.sleep(1000);
                        loginError++;
                        continue;
                    }
                    if (!loginCheckResult.isCheckToken()) {
                        massage = new Message();
                        massage.what = 1;
                        massage.obj = "校验token失败，重试....";
                        myHandler.sendMessage(massage);
                        Thread.sleep(1000);
                        loginError++;
                        continue;
                    }
                    break;
                }


                if (loginError > 3) {
                    massage = new Message();
                    massage.what = 1;
                    massage.obj = "登陆失败三次，先放弃登陆，直接先抢票，抢到票再登陆购买....";
                    myHandler.sendMessage(massage);
                    okHttpClient = ConnectionUtils.getClient(15000);
                }else {
                    massage = new Message();
                    massage.what = 1;
                    massage.obj = "登陆成功....";
                    myHandler.sendMessage(massage);
                }
                massage = new Message();
                massage.what = 1;
                massage.obj = "开始监控票....";
                myHandler.sendMessage(massage);
                //初始化查询
                Request request = new Request.Builder()
                        .url("https://kyfw.12306.cn/otn/leftTicket/init")
                        .addHeader("User-Agent",
                                "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400")
                        .addHeader("Host", "kyfw.12306.cn")
                        .addHeader("X-Requested-With", "XMLHttpRequest")
                        .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                        .build();
                okhttp3.Response response = null;
                try {
                    response = okHttpClient.newCall(request).execute();
                    String[] splite = response.body().string().split("\n");
                    String Query = "";
                    for (int i = 0; i < splite.length; i++) {
                        if (splite[i].contains("CLeftTicketUrl")) {
                            Query = splite[i].substring(splite[i].indexOf("'") + 1, splite[i].length() - 2);
                        }
                    }
                    if (Query.isEmpty()) {
                        Query = "leftTicket/query";
                    }
                    boolean OK = false;
                    int numcode = 0;
                    while (!OK && isTask) {
                        numcode++;
                        massage = new Message();
                        massage.what = 1;
                        massage.obj = "第 " + numcode + " 次刷票";
                        myHandler.sendMessage(massage);
                        if(numcode%10==0){
                            massage = new Message();
                            massage.what = 2;
                            massage.obj = taskDetails.getTask().getTask_id();
                            myHandler.sendMessage(massage);
                        }
                        int i = 0;
                        for (i = 0; i < datas.size() && isTask; i++) {
                            List<TrainBean> trainBeanList = QueryUtils.queryTrainMessage(okHttpClient, "https://kyfw.12306.cn/otn/" + Query, datas.get(i), taskDetails.getTask().getFind_from(), taskDetails.getTask().getFind_to(), taskDetails.getTask().getType());
                            if (trainBeanList != null && trainBeanList.size() > 0) {
                                for (int j = 0; j < trainBeanList.size() && isTask; j++) {
                                    if (taskDetails.getTask().getTrips().contains(trainBeanList.get(j).getNum()) && trainBeanList.get(j).getCan_buy().equals("Y")) {
                                        massage = new Message();
                                        massage.what = 1;
                                        massage.obj = "发现 " + trainBeanList.get(j).getNum() + " 有票，尝试购买...";
                                        myHandler.sendMessage(massage);
                                        //如果选的座有
                                        boolean ishaveSet = false;
                                        List<String> seatList = new ArrayList<>();
                                        if (trainBeanList.get(j).getNum().contains("D") || trainBeanList.get(j).getNum().contains("G")) {
                                            if (!trainBeanList.get(j).getEdz().isEmpty() && !trainBeanList.get(j).getEdz().contains("无")) {
                                                if (taskDetails.getTask().getSeat_types().contains("O")) {
                                                    seatList.add("O");
                                                    ishaveSet = true;
                                                }
                                            }
                                            if (!trainBeanList.get(j).getYdz().isEmpty() && !trainBeanList.get(j).getYdz().contains("无")) {
                                                if (taskDetails.getTask().getSeat_types().contains("M")) {
                                                    seatList.add("M");
                                                    ishaveSet = true;
                                                }
                                            }
                                            if (!trainBeanList.get(j).getSwtdz().isEmpty() && !trainBeanList.get(j).getSwtdz().contains("无")) {
                                                if (taskDetails.getTask().getSeat_types().contains("9")) {
                                                    seatList.add("9");
                                                    ishaveSet = true;
                                                }
                                            }

                                        } else {
                                            if (!trainBeanList.get(j).getYz().isEmpty() && !trainBeanList.get(j).getYz().contains("无")) {
                                                if (taskDetails.getTask().getSeat_types().contains("1")) {
                                                    seatList.add("1");
                                                    ishaveSet = true;
                                                }
                                            }
                                            if (!trainBeanList.get(j).getWz().isEmpty() && !trainBeanList.get(j).getWz().contains("无")) {
                                                if (taskDetails.getTask().getSeat_types().contains("S")) {
                                                    seatList.add("S");
                                                    ishaveSet = true;
                                                }
                                            }
                                            if (!trainBeanList.get(j).getYw().isEmpty() && !trainBeanList.get(j).getYw().contains("无")) {
                                                if (taskDetails.getTask().getSeat_types().contains("3")) {
                                                    seatList.add("3");
                                                    ishaveSet = true;
                                                }
                                            }
                                            if (!trainBeanList.get(j).getRw().isEmpty() && !trainBeanList.get(j).getRw().contains("无")) {
                                                if (taskDetails.getTask().getSeat_types().contains("4")) {
                                                    seatList.add("4");
                                                    ishaveSet = true;
                                                }
                                            }
                                            if (!trainBeanList.get(j).getRz().isEmpty() && !trainBeanList.get(j).getRz().contains("无")) {
                                                if (taskDetails.getTask().getSeat_types().contains("2")) {
                                                    seatList.add("2");
                                                    ishaveSet = true;
                                                }
                                            }
                                        }

                                        if (!ishaveSet) {
                                            massage = new Message();
                                            massage.what = 1;
                                            massage.obj = "没有匹配到您选的座位，或者您的选的座无票，再次刷票...";
                                            myHandler.sendMessage(massage);
                                            continue;
                                        }

                                        //开始买票
                                        //检查用户是否已经登陆
                                        BookResult bookResult = new BookResult();
                                        // 设置绑定会话的客户端
                                        bookResult.setClient(okHttpClient);
                                        // 检查登录状态
                                        bookResult = BookUtils.checkUserStatus(bookResult);
                                        if (!bookResult.isCheckUser()) {
                                            //用户没登陆，重新登陆
                                            int runLoginError = 0;
                                            while (true && isTask) {
                                                if (runLoginError > 5) {
                                                    massage = new Message();
                                                    massage.what = 1;
                                                    massage.obj = "登陆失败五次，只能暂时放弃了....";
                                                    myHandler.sendMessage(massage);
                                                    break;
                                                }
                                                //开始登陆
                                                LoginCheckResult loginCheckResult = new LoginCheckResult();
                                                okHttpClient = ConnectionUtils.getClient(15000);
                                                loginCheckResult = LoginUtils.CheckCodeAndLogin(okHttpClient);

                                                if (!loginCheckResult.isGetCode()) {
                                                    massage = new Message();
                                                    massage.what = 1;
                                                    massage.obj = "获取验证码失败，重试....";
                                                    myHandler.sendMessage(massage);
                                                    Thread.sleep(1000);
                                                    runLoginError++;
                                                    continue;
                                                }
                                                if (!loginCheckResult.isLocalCode()) {
                                                    massage = new Message();
                                                    massage.what = 1;
                                                    massage.obj = "云打码失败，重试....";
                                                    myHandler.sendMessage(massage);
                                                    Thread.sleep(1000);
                                                    runLoginError++;
                                                    continue;
                                                }
                                                if (!loginCheckResult.isCodeCheck()) {
                                                    massage = new Message();
                                                    massage.what = 1;
                                                    massage.obj = "验证码验证失败，重试....";
                                                    myHandler.sendMessage(massage);
                                                    Thread.sleep(1000);
                                                    runLoginError++;
                                                    continue;
                                                }
                                                if (!loginCheckResult.isCheckUser()) {
                                                    massage = new Message();
                                                    massage.what = 1;
                                                    massage.obj = "登陆12306失败，重试....";
                                                    myHandler.sendMessage(massage);
                                                    Thread.sleep(1000);
                                                    runLoginError++;
                                                    continue;
                                                }
                                                if (!loginCheckResult.isGetToken()) {
                                                    massage = new Message();
                                                    massage.what = 1;
                                                    massage.obj = "获取token失败，重试....";
                                                    myHandler.sendMessage(massage);
                                                    Thread.sleep(1000);
                                                    runLoginError++;
                                                    continue;
                                                }
                                                if (!loginCheckResult.isCheckToken()) {
                                                    massage = new Message();
                                                    massage.what = 1;
                                                    massage.obj = "校验token失败，重试....";
                                                    myHandler.sendMessage(massage);
                                                    Thread.sleep(1000);
                                                    runLoginError++;
                                                    continue;
                                                }
                                                break;
                                            }
                                            if (runLoginError > 5) {
                                                continue;
                                            }
                                            // 设置绑定会话的客户端
                                            bookResult.setClient(okHttpClient);
                                            // 检查登录状态
                                            bookResult = BookUtils.checkUserStatus(bookResult);
                                        }
                                        massage = new Message();
                                        massage.what = 1;
                                        massage.obj = "正在检查是否有未完成订单...";
                                        myHandler.sendMessage(massage);
                                        // 检查是否有未完成订单
                                        bookResult = BookUtils.submitOrder(bookResult, trainBeanList.get(j), taskDetails);
                                        if (!bookResult.isSubmitOrder()) {
                                            massage = new Message();
                                            massage.what = 1;
                                            massage.obj = "检查是否有未完成订单出错，重试...";
                                            myHandler.sendMessage(massage);
                                            //重试
                                            bookResult = BookUtils.submitOrder(bookResult, trainBeanList.get(j), taskDetails);
                                        }
                                        // 从InitDc获取必要信息
                                        bookResult = BookUtils.getInitDc(bookResult);
                                        // System.out.println(bookResult.getInitDcInfo());
                                        if (!bookResult.isInitDc()) {
                                            // 获取乘客信息
                                            bookResult = BookUtils.getInitDc(bookResult);
                                            if(!bookResult.isInitDc()){
                                                massage = new Message();
                                                massage.what = 1;
                                                massage.obj = " 获取文本信息出错...";
                                                myHandler.sendMessage(massage);
                                                continue;
                                            }
                                        }
                                        massage = new Message();
                                        massage.what = 1;
                                        massage.obj = "正在请求联系人...";
                                        myHandler.sendMessage(massage);
                                        bookResult = BookUtils.getPassenger("POST",bookResult);
                                        if (!bookResult.isPassenger()) {
                                            massage = new Message();
                                            massage.what = 1;
                                            massage.obj = "请求联系人失败，重试...";
                                            myHandler.sendMessage(massage);
                                            bookResult = BookUtils.getPassenger("PUT",bookResult);
                                            if(!bookResult.isPassenger()){
                                                massage = new Message();
                                                massage.what = 1;
                                                massage.obj = "请求联系人再次失败，账号可能已经被其他应用强挤...";
                                                myHandler.sendMessage(massage);
                                                continue;
                                            }
                                        }
                                        massage = new Message();
                                        massage.what = 1;
                                        massage.obj = "正在提交订单...";
                                        myHandler.sendMessage(massage);
                                        //  提交订单
                                        int tijiao = 5;
                                        while (tijiao > 0 && isTask) {
                                            boolean isOk = false;
                                            for (int k = 0; k < seatList.size(); k++) {
                                                bookResult = BookUtils.checkOrderInfo(bookResult, taskDetails, seatList.get(i));
                                                if (bookResult.isCheckOrderInfo()) {
                                                    isOk = true;
                                                    break;
                                                }
                                                Thread.sleep(2000);
                                            }
                                            tijiao--;
                                            if (isOk) {
                                                massage = new Message();
                                                massage.what = 1;
                                                massage.obj = "订单请求提交成功...";
                                                myHandler.sendMessage(massage);
                                                break;
                                            }
                                            massage = new Message();
                                            massage.what = 1;
                                            massage.obj = "订单请求提交失败，尝试再次提交...";
                                            myHandler.sendMessage(massage);
                                        }
                                        if (!bookResult.isCheckOrderInfo()) {
                                            massage = new Message();
                                            massage.what = 1;
                                            massage.obj = "订单请求提交连续出错5次，取消提交...";
                                            myHandler.sendMessage(massage);
                                            continue;
                                        }
                                        massage = new Message();
                                        massage.what = 1;
                                        massage.obj = "正在请求订单队列...";
                                        myHandler.sendMessage(massage);
                                        bookResult = BookUtils.getConfirmSingleForQueue(bookResult);
                                        if (!bookResult.isConfirmSingleForQueue()) {
                                            bookResult = BookUtils.getConfirmSingleForQueue(bookResult);
                                            if(!bookResult.isConfirmSingleForQueue()){
                                                massage = new Message();
                                                massage.what = 1;
                                                massage.obj = "请求订单队列,失败，重试...";
                                                myHandler.sendMessage(massage);
                                                continue;
                                            }
                                        }else {
                                            massage = new Message();
                                            massage.what = 1;
                                            massage.obj = "请求订单队列,失败，重试...";
                                            myHandler.sendMessage(massage);
                                            continue;
                                        }
                                        massage = new Message();
                                        massage.what = 1;
                                        massage.obj = "正在查询排队时间...";
                                        myHandler.sendMessage(massage);
                                        bookResult = BookUtils.getQueryOrderTime(bookResult);
                                        if (bookResult.isQueryOrderTime()) {
                                            bookResult = BookUtils.getQueryOrderTime(bookResult);
                                            if(!bookResult.isQueryOrderTime()){
                                                massage = new Message();
                                                massage.what = 1;
                                                massage.obj = "查询排队时间,失败，重试...";
                                                myHandler.sendMessage(massage);
                                                continue;
                                            }
                                        }else {
                                            massage = new Message();
                                            massage.what = 1;
                                            massage.obj = "正在查询排队时间,失败，重试...";
                                            myHandler.sendMessage(massage);
                                            continue;
                                        }
                                        massage = new Message();
                                        massage.what = 1;
                                        massage.obj = "订票成功...后面查看订单状态，这里无需等待，直接去12306支付吧";
                                        myHandler.sendMessage(massage);
                                        massage = new Message();
                                        massage.what = 3;
                                        massage.obj =taskDetails.getTask().getTask_id();
                                        myHandler.sendMessage(massage);
                                        ToastUtils.Speak("订票成功...后面查看订单状态，这里无需等待，直接去12306支付吧");
                                        ToastUtils.showSucessToast("订票成功...后面查看订单状态，这里无需等待，直接去12306支付吧");
                                        try {
                                            massage = new Message();
                                            massage.what = 1;
                                            massage.obj = "正在查看订单状态...";
                                            myHandler.sendMessage(massage);
                                            if (!bookResult.isResultOrderForQueue()) {
                                                bookResult = BookUtils.getResultOrderForQueue(bookResult);
                                            }
                                            bookResult = BookUtils.getOrderMsg(bookResult);
                                            if (bookResult.isFinish()) {
                                                Log.i("------", "恭喜您，订票成功，请在30分钟内登录12306完成支付！");
                                            }
                                            massage = new Message();
                                            massage.what = 1;
                                            massage.obj = "恭喜您，订票成功，请在30分钟内登录12306完成支付！";
                                            myHandler.sendMessage(massage);
                                            massage = new Message();
                                            massage.what = 3;
                                            massage.obj =taskDetails.getTask().getTask_id();
                                            myHandler.sendMessage(massage);
                                            ToastUtils.Speak("恭喜您，订票成功，请在30分钟内登录12306完成支付！");
                                            ToastUtils.showSucessToast("恭喜您，订票成功，请在30分钟内登录12306完成支付！");
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        OK = true;
                                    }
                                }
                                if (OK) {
                                    break;
                                }
                            }
                            Thread.sleep(shulv);
                            if (OK) {
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    isTask=false;
                    massage = new Message();
                    massage.what = 1;
                    massage.obj = "任务出错....请您重新开启";
                    myHandler.sendMessage(massage);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    class MyHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                messages.add(msg.obj.toString());
                massageAdapter.notifyDataSetChanged();
                messageRecycleView.scrollToPosition(massageAdapter.getItemCount() - 1);
            }else if(msg.what==2){
                OkGo.<String>post(ApiConstants.API_BASE_URL + "book/task/updateStatus?taskId=" + msg.obj + "&status=6")
                        .headers("Authorization", UserModel.getCurrentUser().getToken())
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                try {
                                    JSONObject jsonObject = JSONObject.parseObject(response.body());
                                    if (jsonObject.getInteger("code") == Constans.API_RESULT_OK) {
                                       Log.i("更新任务状态成功",System.currentTimeMillis()+"");
                                    } else {
                                        Log.i("更新任务状态失败",System.currentTimeMillis()+"");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                   // ToastUtils.showWarningToast(e.toString());
                                }
                            }
                        });
            }else if(msg.what==3){
                OkGo.<String>post(ApiConstants.API_BASE_URL + "book/task/updateStatus?taskId=" + msg.obj + "&status=3")
                        .headers("Authorization", UserModel.getCurrentUser().getToken())
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                try {
                                    JSONObject jsonObject = JSONObject.parseObject(response.body());
                                    if (jsonObject.getInteger("code") == Constans.API_RESULT_OK) {
                                        Log.i("更新任务状态成功",System.currentTimeMillis()+"");
                                    } else {
                                        Log.i("更新任务状态失败",System.currentTimeMillis()+"");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    // ToastUtils.showWarningToast(e.toString());
                                }
                            }
                        });

            }


        }
    }

}
