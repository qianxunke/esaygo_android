package com.esaygo.app.module.query.presenter;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.esaygo.app.common.base.BaseSubscriber;
import com.esaygo.app.common.base.RxPresenter;
import com.esaygo.app.module.query.contract.QueryContract;
import com.esaygo.app.module.query.modle.TrainBean;
import com.esaygo.app.module.user.bean.AuthCredential;
import com.esaygo.app.rx.RxUtils;
import com.esaygo.app.utils.network.common.HttpResponseBase;
import com.esaygo.app.utils.network.helper.RetrofitHelper;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.cookie.store.CookieStore;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class QueryPresenter extends RxPresenter<QueryContract.View> implements QueryContract.Presenter<QueryContract.View> {


    RetrofitHelper mRetrofitHelper;

    @Inject
    public QueryPresenter(RetrofitHelper retrofitHelper) {
        mRetrofitHelper = retrofitHelper;
    }


    @Override
    public void donequery(String form, String to, String date, String purpose_codes) {
       /*
        QueryBean queryBean = new QueryBean();
        queryBean.find_from = form;
        queryBean.find_to = to;
        queryBean.purpose_codes = purpose_codes;
        queryBean.train_date = date;

        BaseSubscriber<HttpResponseBase<List<TrainBean>>> subscriber =
                mRetrofitHelper.doneQueryTrains(queryBean)
                        .compose(RxUtils.rxSchedulerHelper())
                        .subscribeWith(new BaseSubscriber<HttpResponseBase<List<TrainBean>>>(mView) {
                            @Override
                            public void onSuccess(HttpResponseBase<List<TrainBean>> o) {
                                mView.showQuery(o);
                            }


                            @Override
                            public void onError(Throwable e) {
                                //  e.fillInStackTrace();
                                //  mView.showError(e.getMessage());
                                super.onError(e);
                            }

                        });

        */
        OkGo.<String>get("https://kyfw.12306.cn/otn/leftTicket/init").execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {

               /* HttpUrl httpUrl = HttpUrl.parse("https://kyfw.12306.cn/otn/leftTicket/init");
                Cookie.Builder builder = new Cookie.Builder();
                Cookie cookie = builder.name("Cookie").value().domain(httpUrl.host()).build();
                CookieStore cookieStore = OkGo.getInstance().getCookieJar().getCookieStore();
                cookieStore.saveCookie(httpUrl, cookie);
                */
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.put("Host", "kyfw.12306.cn");
               // httpHeaders.put("User-Agent",);
                httpHeaders.put("Content-Type","application/json;charset=UTF-8");
                httpHeaders.put("Cookie",response.headers().toMultimap().get("Set-Cookie").toString());
                httpHeaders.put("X-Cdn-Src-Port",response.headers().get("X-Cdn-Src-Port"));
                httpHeaders.put("Connection","keep-alive");
              //  httpHeaders.put("");
                OkGo.<String>get("https://kyfw.12306.cn/otn/leftTicket/queryZ?leftTicketDTO.train_date=" + date + "&leftTicketDTO.from_station=" + form + "&leftTicketDTO.to_station=" + to + "&purpose_codes=" + purpose_codes).headers(httpHeaders)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                try {
                                    if (response.code() == 200) {
                                        HttpResponseBase<List<TrainBean>> o = new HttpResponseBase<>();
                                        o.code = 200;
                                        o.message="ok";
                                        o.data = formatQueryMessage(response.body());
                                        mView.showQuery(o);
                                    } else {
                                        mView.showError("服务器维护中...");
                                    }

                                } catch (Exception e) {
                                 //   mView.showError(e.toString());
                                }

                            }
                        });

            }
        });
      //  addSubscribe(subscriber);
    }

    public static class QueryBean {
        public String find_from;
        public String find_to;
        public String train_date;
        public String purpose_codes;


        public String getFind_from() {
            return find_from;
        }

        public void setFind_from(String find_from) {
            this.find_from = find_from;
        }

        public String getFind_to() {
            return find_to;
        }

        public void setFind_to(String find_to) {
            this.find_to = find_to;
        }

        public String getTrain_date() {
            return train_date;
        }

        public void setTrain_date(String train_date) {
            this.train_date = train_date;
        }

        public String getPurpose_codes() {
            return purpose_codes;
        }

        public void setPurpose_codes(String purpose_codes) {
            this.purpose_codes = purpose_codes;
        }
    }

    /**
     * 格式化输出列车信息
     *
     * @param s
     * @param
     * @return
     */
    public static List<TrainBean> formatQueryMessage(String s) {
        List<TrainBean> list = new ArrayList<>();
        JSONObject jsonObject = JSONObject.parseObject(s);
        JSONObject dataJson = jsonObject.getJSONObject("data");
        List<String> tmp = (List) dataJson.get("result");
        for (int i = 0; i < tmp.size(); i++) {
            String[] ss = tmp.get(i).split("\\|");
            TrainBean t = new TrainBean();
            t.setSecret_str(ss[0]);
            t.setTrain_code(ss[2]);
            t.setNum(ss[3]);
            t.setFrom(ss[4]);
            t.setTo(ss[5]);
            t.setFind_from(ss[6]);
            t.setFind_to(ss[7]);
            t.setStart_time(ss[8]);
            t.setEnd_time(ss[9]);
            t.setCost_time(ss[10]);
            t.setCan_buy(ss[11]);
            t.setTrain_date(ss[13]);
            t.setWz(ss[26]);
            t.setYz(ss[29]);
            t.setRz(ss[24]);
            t.setYw(ss[28]);
            t.setDw(ss[33]);
            t.setRw(ss[23]);
            t.setGjrw(ss[21]);
            t.setEdz(ss[30]);
            t.setYdz(ss[31]);
            t.setSwtdz(ss[32]);
            list.add(t);
        }
        return list;
    }
}
