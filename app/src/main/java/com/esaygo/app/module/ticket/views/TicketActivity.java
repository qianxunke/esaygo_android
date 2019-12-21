package com.esaygo.app.module.ticket.views;

import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.esaygo.app.R;
import com.esaygo.app.common.base.BaseActivity;
import com.esaygo.app.common.constan.Constans;
import com.esaygo.app.module.ticket.contract.TicketContract;
import com.esaygo.app.module.ticket.model.TaskDetails;
import com.esaygo.app.module.ticket.presenter.TicketPresenter;
import com.esaygo.app.module.user.UserModel;
import com.esaygo.app.module.user.bean.Presenter;
import com.esaygo.app.utils.ToastUtils;
import com.esaygo.app.utils.dateselect.multi.MultiSelectActivity;
import com.esaygo.app.utils.network.common.HttpResponseBase;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zaaach.citypicker.model.City;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class TicketActivity extends BaseActivity<TicketPresenter> implements TicketContract.View {


    @BindView(R.id.tx_start_station)
    TextView tx_start_station;
    @BindView(R.id.im_address_change)
    ImageView im_address_change;
    @BindView(R.id.tx_end_station)
    TextView tx_end_station;


    @BindView(R.id.tx_train_dates)
    TextView tx_train_dates;
    @BindView(R.id.tx_select_dates)
    TextView tx_select_dates;

    @BindView(R.id.tx_train_trips)
    TextView tx_train_trips;
    @BindView(R.id.tx_select_trips)
    TextView tx_select_trips;

    @BindView(R.id.tx_train_seat_types)
    TextView tx_train_seat_types;
    @BindView(R.id.tx_select_seat_types)
    TextView tx_select_seat_types;

    @BindView(R.id.recyclerView)
    RecyclerView showRecyclerView;
    @BindView(R.id.txt_add_presenter)
    TextView txt_add_presenter;

    @BindView(R.id.cb_scan)
    CheckBox cb_scan;
    @BindView(R.id.cb_chooise)
    CheckBox cb_chooise;

    List<SeatType> seatTypes;
    String trips = "";
    SeatAdapter seatAdapter;

    @BindView(R.id.txt_go)
    TextView txt_go;

    String selectDates = "";
    City startCity;
    City endCity;
    String purpose_codes = "";


    List<Presenter> showPresenterList;
    ShowPresenterAdapter showPresenterAdapter;

    List<Presenter> selectPresenterList;
    SelectPresenterAdapter selectPresenterAdapter;


    public final static int GETSELECT_DATES_REQUEST = 10011;

    public final static int GETSELECT_DATES_RESPONSE = 10012;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_train_details;
    }

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    @Override
    public void shoAddTaskResult(HttpResponseBase<Object> data) {
        promptDialog.dismiss();
        if (data.code == Constans.API_RESULT_OK) {
            ToastUtils.showSucessToast(data.message);
            finish();
        } else {
            ToastUtils.showErrorToast(data.message);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getSerializableExtra("startCity") != null) {
            startCity = (City) getIntent().getSerializableExtra("startCity");
            tx_start_station.setText(startCity.getName());
        }

        if (getIntent().getSerializableExtra("endCity") != null) {
            endCity = (City) getIntent().getSerializableExtra("endCity");
            tx_end_station.setText(endCity.getName());
        }

        if (getIntent().getStringExtra("goTime") != null) {
            selectDates = getIntent().getStringExtra("goTime");
            tx_train_dates.setText(selectDates);
        }

        if (getIntent().getStringExtra("purpose_codes") != null) {
            purpose_codes = getIntent().getStringExtra("purpose_codes");
            if (purpose_codes.equals("ADULT")) {
                cb_scan.setEnabled(false);
                cb_chooise.setEnabled(true);
                cb_chooise.setChecked(false);
            } else {
                cb_chooise.setEnabled(false);
                cb_scan.setEnabled(true);
                cb_scan.setChecked(false);
            }
        }
        if (getIntent().getStringExtra("trips") != null) {
            trips = getIntent().getStringExtra("trips");
            tx_train_trips.setText(trips);
        }


    }

    @Override
    public void showLogin12306(HttpResponseBase<Object> datas) {
        promptDialog.dismiss();
        if (datas.code == Constans.API_RESULT_OK) {
            mPresenter.doneGetUserPresenter();
        } else {
            ToastUtils.showErrorToast(datas.message);
            doneShowLogin();
        }
    }

    @Override
    public void showUserPresenters(HttpResponseBase<List<Presenter>> datas) {
        promptDialog.dismiss();
        if (datas.code == Constans.API_RESULT_OK) {
            if (datas.data != null && datas.data.size() > 0) {
                selectPresenterList.clear();
                selectPresenterList.addAll(datas.data);
                selectPresenterAdapter.notifyDataSetChanged();
            } else {
                //让用户登陆12306账户
                ToastUtils.showErrorToast("获取联系人失败，请先登陆12306");
                doneShowLogin();
            }
        } else {
            ToastUtils.showErrorToast(datas.message);
            doneShowLogin();
        }
    }

    @Override
    protected void initToolbar() {
        mBack = true;
        super.initToolbar();
        titleText.setText("添加抢票任务");

    }

    @Override
    protected void initWidget() {
        super.initWidget();
        showPresenterList = new ArrayList<>();
        showPresenterAdapter = new ShowPresenterAdapter(R.layout.item_show_presenter, showPresenterList);
        selectPresenterList = new ArrayList<>();
        selectPresenterAdapter = new SelectPresenterAdapter(R.layout.item_select_presenter, selectPresenterList);

        seatTypes = new ArrayList<>();
        seatAdapter = new SeatAdapter(R.layout.item_seat_type, seatTypes);


        showRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        showRecyclerView.setAdapter(showPresenterAdapter);


        txt_add_presenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog();
            }
        });

        tx_select_seat_types.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSeatTypeDialog();
            }
        });
        tx_select_trips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showWarningToast("目前不支持选择车次，敬请期待下个版本");
            }
        });
        tx_select_dates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置启动intent以及对应的请求码
                startActivityForResult(new Intent(mContext, MultiSelectActivity.class), GETSELECT_DATES_REQUEST);
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
        txt_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneAddTask();
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
                    purpose_codes = "ADULT";
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
                    purpose_codes = "0X00";
                }
            }
        });
        mPresenter.doneGetUserPresenter();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //建议添加逻辑判断（使请求码与结果码配对，确保返回结果是请求码所请求的）
        if (requestCode == GETSELECT_DATES_REQUEST && resultCode == GETSELECT_DATES_RESPONSE) {
            String da = data.getStringExtra("dates");
            if (da != null && !da.isEmpty()) {
                selectDates = da;
                tx_train_dates.setText(selectDates);
            }
        }
    }

    public class SelectPresenterAdapter extends BaseQuickAdapter<Presenter, BaseViewHolder> {


        public SelectPresenterAdapter(int layoutResId, List data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, Presenter item) {

            ImageView selectImageview = helper.getView(R.id.iv_choose);
            selectImageview.setSelected(item.isSelect());
            helper.setText(R.id.tx_item_select_presenter_name, item.getPassenger_name());
            helper.setText(R.id.tx_item_select_presenter_idnum, item.getPassenger_id_no());
            helper.setText(R.id.tx_item_select_presenter_type, item.getPassenger_type_name());
            helper.setOnClickListener(R.id.iv_choose, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (item.isSelect()) {
                        item.setSelect(false);
                    } else {
                        item.setSelect(true);
                    }
                    boolean isHave = false;
                    for (int i = 0; i < showPresenterList.size(); i++) {
                        if (showPresenterList.get(i).getPassenger_id_no().equals(item.getPassenger_id_no())) {
                            showPresenterList.remove(i);
                            isHave = true;
                            break;
                        }
                    }
                    if (!isHave) {
                        showPresenterList.add(item);
                    }
                    selectPresenterAdapter.notifyDataSetChanged();

                }
            });
        }
    }

    public class ShowPresenterAdapter extends BaseQuickAdapter<Presenter, BaseViewHolder> {

        public ShowPresenterAdapter(int layoutResId, List data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, Presenter item) {

            helper.setText(R.id.tx_show_presenter_name, item.getPassenger_name());
            helper.setText(R.id.tx_show_presenter_idnum, item.getPassenger_id_no());
            helper.setText(R.id.tx_show_presenter_type, item.getPassenger_type_name());
            helper.setOnClickListener(R.id.iv_delete, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPresenterList.remove(helper.getPosition());
                    for (int i = 0; i < selectPresenterList.size(); i++) {
                        if (selectPresenterList.get(i).getPassenger_id_no().equals(item.getPassenger_id_no())) {
                            selectPresenterList.get(i).setSelect(false);
                            break;
                        }
                    }
                    showPresenterAdapter.notifyDataSetChanged();
                }
            });

        }
    }

    private void doneShowLogin() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_12306_login)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    public void convertView(final ViewHolder holder, final BaseNiceDialog dialog) {
                        EditText accountE = holder.getView(R.id.ed_12306_account);
                        EditText accountP = holder.getView(R.id.ed_12306_pwd);
                        UserModel.User user = UserModel.getCurrentUser();
                        if (user.getTran_user_account() != null && !user.getTran_user_account().isEmpty()) {
                            accountE.setText(user.getTran_user_account());
                            accountP.setText(user.getTran_user_pwd());
                        }
                        holder.setOnClickListener(R.id.tx_cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.tx_ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                promptDialog.showLoading("登陆中...");
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mPresenter.doneLogin12306(accountE.getText().toString(), accountP.getText().toString());
                                    }
                                }, Constans.TIME_1500);
                            }
                        });

                    }
                })
                .setMargin(10)
                .setOutCancel(true)
                .show(getSupportFragmentManager());
    }


    private void showSelectDialog() {
        if (selectPresenterList.size() == 0) {
            doneShowLogin();
            return;
        }
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_select_presenter)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    public void convertView(final ViewHolder holder, final BaseNiceDialog dialog) {
                        RecyclerView recyclerView = holder.getView(R.id.xrecleview_order_list);
                        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                        recyclerView.setAdapter(selectPresenterAdapter);
                        selectPresenterAdapter.notifyDataSetChanged();
                        holder.setOnClickListener(R.id.tx_ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                showPresenterAdapter.notifyDataSetChanged();
                            }
                        });

                    }
                })
                .setMargin(10)
                .setOutCancel(true)
                .show(getSupportFragmentManager());

    }


    public class SeatAdapter extends BaseQuickAdapter<SeatType, BaseViewHolder> {

        public SeatAdapter(int layoutResId, List data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, SeatType item) {

            helper.setText(R.id.tx_item_select_seat_name, item.name);

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

    class SeatType {
        String name;
        String value;
        boolean isSelect;

        public SeatType(String name, String value, boolean isSelect) {
            super();
            this.name = name;
            this.value = value;
            this.isSelect = isSelect;
        }

    }

    /*
        PASSENGER_TICKER_STR = {
                    '一等座': 'M',
                    '特等座': 'P',
                    '二等座': 'O',
                    '商务座': 9,
                    '硬座': 1,
                    '无座': 1,
                    '软座': 2,
                    '软卧': 4,
                    '硬卧': 3,
        }
     */
    private void UpdateSeatList() {
        String tr = tx_train_trips.getText().toString();
        seatTypes.clear();
        if (tr.contains("K") && (tr.contains("D") || tr.contains("G"))) {
            seatTypes.add(new SeatType("一等座", "M", false));
            seatTypes.add(new SeatType("特等座", "P", false));
            seatTypes.add(new SeatType("二等座", "O", false));
            seatTypes.add(new SeatType("商务座", "9", false));
            seatTypes.add(new SeatType("硬座", "1", false));
            seatTypes.add(new SeatType("无座", "1", false));
            seatTypes.add(new SeatType("软座", "2", false));
            seatTypes.add(new SeatType("软卧", "4", false));
            seatTypes.add(new SeatType("硬卧", "3", false));
        } else {
            if (tr.contains("K")) {
                seatTypes.add(new SeatType("硬座", "1", false));
                seatTypes.add(new SeatType("无座", "1", false));
                seatTypes.add(new SeatType("软座", "2", false));
                seatTypes.add(new SeatType("软卧", "4", false));
                seatTypes.add(new SeatType("硬卧", "3", false));
            } else {
                seatTypes.add(new SeatType("一等座", "M", false));
                seatTypes.add(new SeatType("特等座", "P", false));
                seatTypes.add(new SeatType("二等座", "O", false));
                seatTypes.add(new SeatType("商务座", "9", false));
            }
        }
    }

    private void showSeatTypeDialog() {

        if (tx_train_trips.getText().toString().isEmpty()) {
            ToastUtils.showWarningToast("请先选择车次");
            return;
        }
        UpdateSeatList();
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_select_presenter)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    public void convertView(final ViewHolder holder, final BaseNiceDialog dialog) {
                        RecyclerView recyclerView = holder.getView(R.id.xrecleview_order_list);
                        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                        recyclerView.setAdapter(seatAdapter);
                        seatAdapter.notifyDataSetChanged();
                        holder.setOnClickListener(R.id.tx_ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                String seats = "";
                                for (int i = 0; i < seatTypes.size(); i++) {
                                    if (seatTypes.get(i).isSelect) {
                                        if (seats.isEmpty()) {
                                            seats = seatTypes.get(i).name;
                                        } else {
                                            seats += "," + seatTypes.get(i).name;
                                        }
                                    }
                                }
                                tx_train_seat_types.setText(seats);
                            }
                        });

                    }
                })
                .setMargin(10)
                .setOutCancel(false)
                .show(getSupportFragmentManager());
    }


    private void doneAddTask() {
        //判断日期日期是否已选
        if (selectDates.equals("")) {
            ToastUtils.showWarningToast("请先选择出发日期");
            return;
        }
        //车次
        if (trips.equals("")) {
            ToastUtils.showWarningToast("请先选择车次");
            return;
        }
        //联系人
        if (showPresenterList.size() == 0) {
            ToastUtils.showWarningToast("请先选择乘车人");
            return;
        }
        //座位
        String seat = "";
        for (int i = 0; i < seatTypes.size(); i++) {
            if (seatTypes.get(i).isSelect) {
                if (seat.equals("")) {
                    seat = seatTypes.get(i).value;
                } else {
                    seat += "," + seatTypes.get(i).value;
                }
            }
        }
        if (seat.equals("")) {
            ToastUtils.showWarningToast("请先选择座位");
            return;
        }

        TaskDetails taskDetails = new TaskDetails();
        taskDetails.setTask(new TaskDetails.Task());
        taskDetails.getTask().setSeat_types(seat);
        taskDetails.getTask().setUser_id(UserModel.getCurrentUser().getUser_id());
        taskDetails.getTask().setOk_no("");
        taskDetails.getTask().setFind_from(startCity.getCode());
        taskDetails.getTask().setFind_to(endCity.getCode());
        taskDetails.getTask().setStatus(0);
        taskDetails.getTask().setTrain_dates(selectDates);
        taskDetails.getTask().setTrips(trips);
        taskDetails.getTask().setType(purpose_codes);
        taskDetails.getTask().setTask_id("");
        //添加乘车人
        TaskDetails.Task_passenger[] task_passengers = new TaskDetails.Task_passenger[showPresenterList.size()];
        for (int i = 0; i < showPresenterList.size(); i++) {
            TaskDetails.Task_passenger taskPassenger = new TaskDetails.Task_passenger();
            taskPassenger.setAllEncStr(showPresenterList.get(i).getAllEncStr());
            taskPassenger.setId_num(showPresenterList.get(i).getPassenger_id_no());
            taskPassenger.setName(showPresenterList.get(i).getPassenger_name());
            taskPassenger.setTask_id("");
            taskPassenger.setId("");
            taskPassenger.setTel_num(showPresenterList.get(i).getMobile_no());
            taskPassenger.setSeat_num("");
            task_passengers[i] = taskPassenger;
        }

        taskDetails.setTask_passenger(task_passengers);

        promptDialog.showLoading("提交中...");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPresenter.doneAddTask(taskDetails);
            }
        }, Constans.TIME_1500);
    }

}
