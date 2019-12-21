package com.esaygo.app.module.main.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esaygo.app.R;
import com.esaygo.app.module.query.views.QueryActivity;
import com.esaygo.app.utils.ToastUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class HomeFragment extends ImBaseFragment implements CalendarView.OnCalendarSelectListener,
        CalendarView.OnYearChangeListener {

    @BindView(R.id.tx_start_station)
    TextView tx_start_station;
    @BindView(R.id.tx_date_chooise)
    TextView tx_date_chooise;
    @BindView(R.id.im_address_change)
    ImageView im_address_change;
    @BindView(R.id.txt_go)
    TextView txt_go;

    @BindView(R.id.tv_month_day)
    TextView mTextMonthDay;
    @BindView(R.id.tv_year)
    TextView mTextYear;
    @BindView(R.id.tv_lunar)
    TextView mTextLunar;
    @BindView(R.id.tv_current_day)
    TextView mTextCurrentDay;
    @BindView(R.id.calendarView)
    CalendarView mCalendarView;

    @BindView(R.id.rl_tool)
    RelativeLayout mRelativeTool;
    @BindView(R.id.fl_current)
    FrameLayout fl_current;
    @BindView(R.id.tx_end_station)
    TextView tx_end_station;

    private int mYear;
    @BindView(R.id.calendarLayout)
    CalendarLayout mCalendarLayout;

    @BindView(R.id.cb_scan)
    CheckBox cb_scan;
    @BindView(R.id.cb_chooise)
    CheckBox cb_chooise;


    List<HotCity> hotCities;

    private int anim;
    private int theme;

    private City startCity;
    private City endCity;
    private String selectTime;
    private String  purpose_codes="ADULT";

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this)
                .statusBarDarkFont(true, 0.2f) //原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
                .init();
    }

    public static HomeFragment getInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        HomeFragment fragment = new HomeFragment();
        fragment.mBundle = bundle;
        return fragment;
    }

    @Override
    public void initWidget() {
        super.initWidget();

        theme = R.style.CustomTheme;
        anim = R.style.CustomAnim;
        startCity = new City("北京", "", "beijing", "BJP");
        endCity = new City("上海", "", "shanghai", "SHH");
        hotCities = new ArrayList<>();
        hotCities.add(new HotCity("北京", "北京", "BJP"));
        hotCities.add(new HotCity("上海", "上海", "SHH"));
        hotCities.add(new HotCity("广州", "广东", "GZQ"));
        hotCities.add(new HotCity("贵阳", "贵州", "GIW"));
        hotCities.add(new HotCity("杭州", "浙江", "HZH"));
        tx_date_chooise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCalendarLayout.isExpand()) {
                    mCalendarLayout.expand();
                }else {
                    mCalendarLayout.shrink();
                }
            }
        });
        tx_start_station.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CityPicker.from(getSupportActivity())
                        .enableAnimation(true)
                        .setAnimationStyle(anim)
                        .setLocatedCity(null)
                        .setHotCities(hotCities)
                        .setOnPickListener(new OnPickListener() {
                            @Override
                            public void onPick(int position, City data) {
                                startCity = data;
                                // currentTV.setText(String.format("当前城市：%s，%s", data.getName(), data.getCode()));
                                tx_start_station.setText(startCity.getName());
                            }

                            @Override
                            public void onCancel() {
                                // Toast.makeText(getApplicationContext(), "取消选择", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onLocate() {
                                //开始定位，这里模拟一下定位
                                /*
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        CityPicker.from(getSupportActivity()).locateComplete(new LocatedCity("深圳", "北京", "101280601"), LocateState.SUCCESS);
                                    }
                                }, 3000);
                                 */
                            }
                        })
                        .show();


            }
        });

        tx_end_station.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CityPicker.from(getSupportActivity())
                        .enableAnimation(true)
                        .setAnimationStyle(anim)
                        .setLocatedCity(null)
                        .setHotCities(hotCities)
                        .setOnPickListener(new OnPickListener() {
                            @Override
                            public void onPick(int position, City data) {
                                endCity = data;
                                // currentTV.setText(String.format("当前城市：%s，%s", data.getName(), data.getCode()));
                                tx_end_station.setText(endCity.getName());
                            }

                            @Override
                            public void onCancel() {
                                // Toast.makeText(getApplicationContext(), "取消选择", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onLocate() {
                                //开始定位，这里模拟一下定位
                                /*
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        CityPicker.from(getSupportActivity()).locateComplete(new LocatedCity("深圳", "北京", "101280601"), LocateState.SUCCESS);
                                    }
                                }, 3000);
                                 */
                            }
                        })
                        .show();


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

        //普通票
        cb_scan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_scan.setEnabled(false);
                    cb_chooise.setEnabled(true);
                    cb_chooise.setChecked(false);
                    purpose_codes="ADULT";
                }

            }
        });
        cb_chooise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_chooise.setEnabled(false);
                    cb_scan.setEnabled(true);
                    cb_scan.setChecked(false);
                    purpose_codes="0X00";
                }
            }
        });

        txt_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, QueryActivity.class);
                intent.putExtra("goTime",selectTime);
                intent.putExtra("startCity",startCity);
                intent.putExtra("endCity",endCity);
                intent.putExtra("purpose_codes",purpose_codes);
                mContext.startActivity(intent);
            }
        });


        /*
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

         */
        fl_current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // mCalendarView.scrollToCurrent();
                if (!mCalendarLayout.isExpand()) {
                    mCalendarLayout.expand();
                }else {
                    mCalendarLayout.shrink();
                }
            }
        });

        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnYearChangeListener(this);
       // mCalendarView.setS(mCalendarView.getCurYear(),mCalendarView.getCurMonth(),mCalendarView.getCurDay());
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
        mTextLunar.setText("今日");
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));
        selectTime=mCalendarView.getCurYear()+"-"+mCalendarView.getCurMonth() + "-" + mCalendarView.getCurDay();
        tx_date_chooise.setText(mTextMonthDay.getText().toString());
        initDateData();
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
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();
        String month=calendar.getMonth()+"";
        if(calendar.getMonth()<10){
            month="0"+calendar.getMonth();
        }
        String day=calendar.getDay()+"";
        if(calendar.getDay()<10){
            day="0"+calendar.getDay();
        }
        selectTime=calendar.getYear()+"-"+month + "-" + day;
        tx_date_chooise.setText(mTextMonthDay.getText().toString());
        if (isClick) {
            if (mCalendarLayout.isExpand()) {
                mCalendarLayout.shrink();
            }
        }
    }

    @Override
    public void onYearChange(int year) {
        mTextMonthDay.setText(String.valueOf(year));
    }


}
