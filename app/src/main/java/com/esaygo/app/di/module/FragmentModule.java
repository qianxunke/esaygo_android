package com.esaygo.app.di.module;

import android.app.Activity;


import androidx.fragment.app.Fragment;

import com.esaygo.app.di.scope.FragmentScope;

import dagger.Module;
import dagger.Provides;

/**
 * 描述:Fragment模型
 */
@Module
public class FragmentModule {

    private Fragment mFragment;

    public FragmentModule(Fragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    @FragmentScope
    public Activity provideActivity() {
        return mFragment.getActivity();
    }
}
