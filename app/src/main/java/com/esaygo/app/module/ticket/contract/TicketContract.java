package com.esaygo.app.module.ticket.contract;

import com.esaygo.app.common.base.BaseContract;
import com.esaygo.app.module.ticket.model.TaskDetails;
import com.esaygo.app.utils.network.common.HttpResponseBase;

import java.util.List;

public interface TicketContract {

    /**
     * 给View提示
     */
    interface View extends BaseContract.BaseView {
        void shoAddTaskResult(HttpResponseBase<Object> data);

        void showLogin12306(HttpResponseBase<Object> datas);

        void showUserPresenters(HttpResponseBase<List<com.esaygo.app.module.user.bean.Presenter>> datas);
    }

    /**
     * 执行升级操作
     *
     * @param <T>
     */
    interface Presenter<T> extends BaseContract.BasePresenter<T> {

        void doneAddTask(TaskDetails taskDetails);

        void doneLogin12306(String account,String pwd);

        void doneGetUserPresenter();

    }




}