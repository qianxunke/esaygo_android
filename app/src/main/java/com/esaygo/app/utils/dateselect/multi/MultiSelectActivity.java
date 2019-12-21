package com.esaygo.app.utils.dateselect.multi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.esaygo.app.R;
import com.esaygo.app.common.base.BaseActivity;
import com.esaygo.app.module.main.presenter.SplashPresenter;
import com.esaygo.app.module.ticket.views.TicketActivity;
import com.esaygo.app.utils.ToastUtils;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class MultiSelectActivity extends BaseActivity<SplashPresenter> implements
        CalendarView.OnCalendarMultiSelectListener,
        CalendarView.OnCalendarInterceptListener,
        CalendarView.OnYearChangeListener,
        CalendarView.OnMonthChangeListener,
        CalendarView.OnCalendarSelectListener,
        View.OnClickListener {

    private static final long ONE_DAY = 1000 * 3600 * 24;

    TextView mTextMonthDay;

    TextView mTextYear;

    TextView mTextLunar;

    TextView mTextCurrentDay;

    CalendarView mCalendarView;

    RelativeLayout mRelativeTool;
    private int mYear;
    CalendarLayout mCalendarLayout;

    String selectDates;
    @BindView(R.id.txt_go)
    TextView txt_go;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_multi;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initWidget() {
        super.initWidget();
        mTextMonthDay = findViewById(R.id.tv_month_day);
        mTextYear = findViewById(R.id.tv_year);
        mTextLunar = findViewById(R.id.tv_lunar);
        mRelativeTool = findViewById(R.id.rl_tool);
        mCalendarView = findViewById(R.id.calendarView);
        mTextCurrentDay = findViewById(R.id.tv_current_day);
        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCalendarLayout.isExpand()) {
                    mCalendarLayout.expand();
                    return;
                }
                mCalendarView.showYearSelectLayout(mYear);
                mTextLunar.setVisibility(View.GONE);
                mTextYear.setVisibility(View.GONE);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });
        findViewById(R.id.fl_current).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.scrollToCurrent();
            }
        });
        mCalendarLayout = findViewById(R.id.calendarLayout);
        mCalendarView.setOnCalendarMultiSelectListener(this);
        mCalendarView.setOnYearChangeListener(this);
        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setMaxMultiSelectSize(3);
        //设置日期拦截事件，当前有效
        mCalendarView.setOnCalendarInterceptListener(this);
        mCalendarView.setOnMonthChangeListener(this);
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
        mTextLunar.setText("今日");
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));
        findViewById(R.id.iv_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.clearMultiSelect();
            }
        });
        txt_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,TicketActivity.class);
                intent.putExtra("dates",selectDates);
                setResult(TicketActivity.GETSELECT_DATES_RESPONSE,intent);
                finish();
            }
        });

    }


    @Override
    public void onClick(View v) {

    }


    @Override
    public void onCalendarMultiSelectOutOfRange(Calendar calendar) {
        Log.e("OutOfRange", "OutOfRange" + calendar);
    }

    @Override
    public void onMultiSelectOutOfSize(Calendar calendar, int maxSize) {
        ToastUtils.showWarningToast("最多只能选"+maxSize+"个日期");
      //  Toast.makeText(this, "超过最大选择数量 ：" + maxSize, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarMultiSelect(Calendar calendar, int curSize, int maxSize) {
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();
        Log.e("onDateSelected", "  -- " + calendar.getYear() +
                "  --  " + calendar.getMonth() +
                "  -- " + calendar.getDay() +
                "  --  " + "  --   " + calendar.getScheme());
        if (selectDates == null) {
            selectDates = "";
        }

        String d = calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay();
        String arr[] = selectDates.split(",");
        boolean isHave = false;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(d)) {
                //有的话就去掉
                arr[i] = "";
                isHave = true;
                break;
            }
        }
        selectDates = "";
        for (int i = 0; i < arr.length; i++) {
            if (selectDates.isEmpty()) {
                if(arr[i].isEmpty()){
                    continue;
                }
                selectDates = arr[i];
            } else {
                selectDates = selectDates + "," + arr[i];
            }
        }
        if (!isHave) {
            selectDates = selectDates + "," + d;
        }

    }


    @Override
    public void onYearChange(int year) {
        mTextMonthDay.setText(String.valueOf(year));
    }


    @Override
    public void onMonthChange(int year, int month) {
        Log.e("onMonthChange", "  -- " + year + "  --  " + month);
    }

    /**
     * 屏蔽某些不可点击的日期，可根据自己的业务自行修改
     * 如 calendar > 2018年1月1日 && calendar <= 2020年12月31日
     *
     * @param calendar calendar
     * @return 是否屏蔽某些不可点击的日期，MonthView和WeekView有类似的API可调用
     */
    @Override
    public boolean onCalendarIntercept(Calendar calendar) {
        //Log.e("onCalendarIntercept", calendar.toString());
        String cM = calendar.getMonth() < 10 ? ("0" + calendar.getMonth()) : calendar.getMonth() + "";
        String cD = calendar.getDay() < 10 ? ("0" + calendar.getDay()) : calendar.getDay() + "";
        String uM = mCalendarView.getCurMonth() < 10 ? ("0" + mCalendarView.getCurMonth()) : mCalendarView.getCurMonth() + "";
        String uD = mCalendarView.getCurDay() < 10 ? ("0" + mCalendarView.getCurDay()) : mCalendarView.getCurDay() + "";

        String s1 = calendar.getYear() + cM + cD;
        String s2 = mCalendarView.getCurYear() + "" + uM + uD;
        if (s1.compareTo(s2) < 0) {
            return true;  //不可选
        }
        java.util.Calendar date = java.util.Calendar.getInstance();

        date.set(calendar.getYear(), calendar.getMonth() - 1, calendar.getDay());//

        long startTimeMills = date.getTimeInMillis();//获得起始时间戳

        date.set(mCalendarView.getCurYear(), mCalendarView.getCurMonth() - 1, mCalendarView.getCurDay());//

        long endTimeMills = date.getTimeInMillis();//获得结束时间戳

        if ((int) ((startTimeMills - endTimeMills) / ONE_DAY) > 60) {
            return true;
        }
        return false;
    }

    @Override
    public void onCalendarInterceptClick(Calendar calendar, boolean isClick) {
        ToastUtils.showWarningToast("该日期不可选择");
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {

        Log.e("onDateSelected", "  -- " + calendar.getYear() +
                "  --  " + calendar.getMonth() +
                "  -- " + calendar.getDay() +
                "  --  " + isClick + "  --   " + calendar.getScheme());
    }


}
