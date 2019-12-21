package com.esaygo.app.di.component;

import android.app.Activity;


import com.esaygo.app.di.module.FragmentModule;
import com.esaygo.app.di.scope.FragmentScope;
import com.esaygo.app.module.main.views.BookFragment;
import com.esaygo.app.module.main.views.HomeFragment;
import com.esaygo.app.module.main.views.MeFragment;

import dagger.Component;

/**
 * 描述:FragmentComponent
 */
@FragmentScope
@Component(dependencies = AppComponent.class, modules = FragmentModule.class)
public interface FragmentComponent {

    Activity getActivity();

    void inject(HomeFragment homeFragment);

    void inject(BookFragment bookFragment);

    void inject(MeFragment meFragment);


}
