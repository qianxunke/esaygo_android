package com.esaygo.app.module.query.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.esaygo.app.R;
import com.esaygo.app.common.base.BaseActivity;
import com.esaygo.app.module.query.adapter.TrainAdapter;
import com.esaygo.app.module.query.contract.QueryContract;
import com.esaygo.app.module.query.modle.TrainBean;
import com.esaygo.app.module.query.presenter.QueryPresenter;
import com.esaygo.app.module.ticket.views.TicketActivity;
import com.esaygo.app.utils.network.common.HttpResponseBase;
import com.gyf.immersionbar.ImmersionBar;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.zaaach.citypicker.model.City;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

import static com.esaygo.app.common.constan.Constans.API_RESULT_OK;

public class QueryActivity extends BaseActivity<QueryPresenter> implements QueryContract.View, CalendarView.OnCalendarSelectListener,
        CalendarView.OnYearChangeListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.fl_current)
    FrameLayout fl_current;
    @BindView(R.id.tv_current_day)
    TextView tv_current_day;
    @BindView(R.id.calendarView)
    CalendarView mCalendarView;

    @BindView(R.id.tx_start_station)
    TextView tx_start_station;
    @BindView(R.id.im_address_change)
    ImageView im_address_change;
    @BindView(R.id.tx_end_station)
    TextView tx_end_station;
    @BindView(R.id.im_return)
    ImageView im_return;


    City startCity;
    City endCity;
    List<TrainBean> trainBeanList;
    TrainAdapter trainAdapter;
    String goTime = "";
    String purpose_codes = "ADULT";

    @Override
    protected void initToolbar() {
        super.initToolbar();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_query;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCity = (City) getIntent().getSerializableExtra("startCity");
        endCity = (City) getIntent().getSerializableExtra("endCity");
        if (startCity == null) {
            startCity = new City("北京", "", "beijing", "BJP");
        }

        if (endCity == null) {
            endCity = new City("上海", "", "shanghai", "SHH");
        }
        goTime = getIntent().getStringExtra("goTime");
        purpose_codes = getIntent().getStringExtra("purpose_codes");
        tx_start_station.setText(startCity.getName());
        tx_end_station.setText(endCity.getName());
        loadData();
    }

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }


    @Override
    protected void initWidget() {
        super.initWidget();
        ImmersionBar.with(this).init();
        trainBeanList = new ArrayList<>();
        //  refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        // refreshLayout.setRefreshFooter(new ClassicsFooter(this));
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
        trainAdapter = new TrainAdapter(trainBeanList);
        recyclerView.setAdapter(trainAdapter);
        trainAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(mContext, TicketActivity.class);
                intent.putExtra("startCity", startCity);
                intent.putExtra("endCity", endCity);
                intent.putExtra("goTime", goTime);
                intent.putExtra("purpose_codes", purpose_codes);
                intent.putExtra("trips", trainBeanList.get(position).getNum());
                startActivity(intent);
            }
        });

        fl_current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCalendarView.getVisibility() == View.GONE) {
                    mCalendarView.setVisibility(View.VISIBLE);
                } else {
                    mCalendarView.setVisibility(View.GONE);
                }
            }
        });
        im_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        im_address_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                City city = new City(startCity.getName(), startCity.getProvince(), startCity.getPinyin(), startCity.getCode());
                startCity = endCity;
                endCity = city;
                tx_start_station.setText(startCity.getName());
                tx_end_station.setText(endCity.getName());
            }
        });

        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnYearChangeListener(this);
        tv_current_day.setText(String.valueOf(mCalendarView.getCurDay()));
        if (goTime.isEmpty()) {
            goTime = mCalendarView.getCurYear() + "-" + mCalendarView.getCurMonth() + "-" + mCalendarView.getCurDay();
        }


    }

    @Override
    protected void loadData() {
        super.loadData();
        mPresenter.donequery(startCity.getCode(), endCity.getCode(), goTime, purpose_codes);
    }


    @Override
    public void showQuery(HttpResponseBase<List<TrainBean>> data) {
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
        trainBeanList.clear();
        trainAdapter.notifyDataSetChanged();
        if (data.code == API_RESULT_OK) {
            if (data.data != null && data.data.size() > 0) {
                gone(R.id.rl_nodata);
                trainBeanList.addAll(data.data);
                trainAdapter.notifyDataSetChanged();
            } else {
                visible(R.id.rl_nodata);

            }
        }

    }


    private void initDateData() {
        int year = mCalendarView.getCurYear();
        int month = mCalendarView.getCurMonth();


        Map<String, Calendar> map = new HashMap<>();
        map.put(getSchemeCalendar(year, month, mCalendarView.getCurDay(), 0xFF40db25, "今").toString(),
                getSchemeCalendar(year, month, mCalendarView.getCurDay(), 0xFF40db25, "今"));
        /*
        map.put(getSchemeCalendar(year, month, 6, 0xFFe69138, "事").toString(),
                getSchemeCalendar(year, month, 6, 0xFFe69138, "事"));
        map.put(getSchemeCalendar(year, month, 9, 0xFFdf1356, "议").toString(),
                getSchemeCalendar(year, month, 9, 0xFFdf1356, "议"));
        map.put(getSchemeCalendar(year, month, 13, 0xFFedc56d, "记").toString(),
                getSchemeCalendar(year, month, 13, 0xFFedc56d, "记"));
        map.put(getSchemeCalendar(year, month, 14, 0xFFedc56d, "记").toString(),
                getSchemeCalendar(year, month, 14, 0xFFedc56d, "记"));
        map.put(getSchemeCalendar(year, month, 15, 0xFFaacc44, "假").toString(),
                getSchemeCalendar(year, month, 15, 0xFFaacc44, "假"));
        map.put(getSchemeCalendar(year, month, 18, 0xFFbc13f0, "记").toString(),
                getSchemeCalendar(year, month, 18, 0xFFbc13f0, "记"));
        map.put(getSchemeCalendar(year, month, 25, 0xFF13acf0, "假").toString(),
                getSchemeCalendar(year, month, 25, 0xFF13acf0, "假"));
        map.put(getSchemeCalendar(year, month, 27, 0xFF13acf0, "多").toString(),
                getSchemeCalendar(year, month, 27, 0xFF13acf0, "多"));

         */
        //此方法在巨大的数据量上不影响遍历性能，推荐使用
        mCalendarView.setSchemeDate(map);
    }


    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        calendar.addScheme(new Calendar.Scheme());
        calendar.addScheme(0xFF008800, "假");
        calendar.addScheme(0xFF008800, "节");
        return calendar;
    }


    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        if (isClick) {
            goTime = calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay();
            loadData();
            if (mCalendarView.getVisibility() == View.GONE) {
                mCalendarView.setVisibility(View.VISIBLE);
            } else {
                mCalendarView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onYearChange(int year) {
        //  mTextMonthDay.setText(String.valueOf(year));
    }


}
