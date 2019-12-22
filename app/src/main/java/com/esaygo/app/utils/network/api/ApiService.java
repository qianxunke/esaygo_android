package com.esaygo.app.utils.network.api;



import com.esaygo.app.module.main.presenter.MainPresenter;
import com.esaygo.app.module.query.modle.TrainBean;
import com.esaygo.app.module.query.presenter.QueryPresenter;
import com.esaygo.app.module.ticket.model.TaskDetails;
import com.esaygo.app.module.ticket.presenter.TicketPresenter;
import com.esaygo.app.module.user.bean.AuthCredential;
import com.esaygo.app.module.user.bean.Presenter;
import com.esaygo.app.module.user.presenter.LoginPresenter;
import com.esaygo.app.utils.network.common.HttpResponseBase;
import com.esaygo.app.utils.network.common.HttpResquestBase;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiService {

    /**
     * 登陆接口
     */
    @POST("user/login")
    Flowable<HttpResponseBase<LoginPresenter.LoginResult>> doneApiLogin(@Body AuthCredential authCredential);

    @POST("user/code")
    Flowable<HttpResponseBase<Object>> doneGetCode(@Body LoginPresenter.SendCode sendCodel);

    @POST("user/register")
    Flowable<HttpResponseBase<LoginPresenter.LoginResult>> doneRegister(@Body LoginPresenter.RegisterPar registerPar);

    @GET("query/train/list")
    Flowable<HttpResponseBase<List<TrainBean>>> doneQueryTrains(@Query("train_date") String train_date,@Query("find_from") String find_from,@Query("find_to") String find_to,@Query("purpose_codes") String purpose_codes);

    @POST("book/task/add")
    Flowable<HttpResponseBase<Object>> doneAddTask(@Body TaskDetails taskDetails);

    @POST("user/login12306")
    Flowable<HttpResponseBase<Object>> doneLogin12306(@Body TicketPresenter.Login12306Request login12306Request);

    @POST("user/app/update")
    Flowable<HttpResponseBase<MainPresenter.UpdateInfo>> doneUpdate();



    @GET("user/presenters")
    Flowable<HttpResponseBase<List<Presenter>>> doneGetUserPresenter();

    /**
     * 1.[进出口] 条码损坏
     */
    @GET("tms/warehouse/damaged-box/{parcelId}")
    Flowable<String> getDamagedBox(@Path("parcelId") String parcelId);

    /**
     * 3.1.[出口]针对出口的箱子，出库前扫描，检查是否已经确认过
     */
    @GET("tms/warehouse/export/verity/check/{id}")
    Flowable<Void> palletizeVerityCheck(@Path("id") String palletId);


    /**
     * 4.1.[出口]针对出口的进行出库，出库单板数量的查询
     */
    @POST("tms/warehouse/stock-out/export/find-pallet-quantity/{destockingId}/")
    Flowable<Integer> findPalletQuantity(@Path("destockingId") String destockingId);
    /**
     * 4.2.[出口]针对出口的进行出库，进行板的确认
     */
    @POST("tms/warehouse/stock-out/export/confirm/{destockingId}/{palletId}")
    Flowable<Object> palletToCar(@Path("destockingId") String destockingId, @Path("palletId") String palletId);

}
