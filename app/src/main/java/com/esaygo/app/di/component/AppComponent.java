package com.esaygo.app.di.component;

import android.content.Context;


import com.esaygo.app.di.module.ApiModule;
import com.esaygo.app.di.module.AppModule;
import com.esaygo.app.utils.network.helper.RetrofitHelper;

import javax.inject.Singleton;

import dagger.Component;


/**
 * @author qianxunke
 * @email  736567805@qq.com
 * @date 2019-12-02 17:24
 * @desc 描述:AppComponent
 */
@Singleton
@Component(modules = {AppModule.class, ApiModule.class})
public interface AppComponent {
    Context getContext();

    RetrofitHelper getRetrofitHelper();

}
