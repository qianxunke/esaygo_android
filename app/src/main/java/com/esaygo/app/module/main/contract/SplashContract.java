package com.esaygo.app.module.main.contract;


import com.esaygo.app.common.base.BaseContract;

/**
 * @author zzq  作者 E-mail:   soleilyoyiyi@gmail.com
 * @date 创建时间：2017/5/23 9:45
 * 描述:欢迎界面Contract
 */

public interface SplashContract {
    interface View extends BaseContract.BaseView {
        void showSplash();

        void showCountDown(int count);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {

        void getSplashData();

        void setCountDown();

    }
}
