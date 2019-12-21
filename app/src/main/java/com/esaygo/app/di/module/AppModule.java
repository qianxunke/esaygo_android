package com.esaygo.app.di.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * 描述:App模型
 */
@Module
public class AppModule {
    private Context mContext;

    public AppModule(Context context) {
        this.mContext = context;
    }

    @Provides
    public Context provideContext() {
        return mContext;
    }
}
