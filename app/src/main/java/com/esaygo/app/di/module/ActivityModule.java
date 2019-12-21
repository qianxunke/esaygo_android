package com.esaygo.app.di.module;

import android.app.Activity;


import com.esaygo.app.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * 描述:Activity模型
 */
@Module
public class ActivityModule {
    private Activity mActivity;

    public ActivityModule(Activity activity) {
        this.mActivity = activity;
    }

    @Provides
    @ActivityScope
    public Activity provideActivity() {
        return mActivity;
    }
}
