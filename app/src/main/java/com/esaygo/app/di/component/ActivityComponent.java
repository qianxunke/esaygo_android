package com.esaygo.app.di.component;

import android.app.Activity;


import com.esaygo.app.di.module.ActivityModule;
import com.esaygo.app.di.scope.ActivityScope;
import com.esaygo.app.module.main.views.DataChooiseActivity;
import com.esaygo.app.module.main.views.MainActivity;
import com.esaygo.app.module.main.views.SplashActivity;
import com.esaygo.app.module.query.views.QueryActivity;
import com.esaygo.app.module.ticket.views.TicketActivity;
import com.esaygo.app.module.user.view.LoginActivity;
import com.esaygo.app.module.user.view.RegisterActivity;
import com.esaygo.app.utils.dateselect.multi.MultiSelectActivity;

import dagger.Component;

/**
 * 描述:ActivityComponent
 */
@ActivityScope
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    Activity getActivity();

    void inject(LoginActivity loginActivity);

    void inject(MainActivity  mainActivity);

    void inject(DataChooiseActivity dataChooiseActivity);

    void inject(QueryActivity queryActivity);

    void inject(MultiSelectActivity multiSelectActivity);

    void inject(TicketActivity ticketActivity);

    void inject(RegisterActivity registerActivity);

    void inject(SplashActivity splashActivity);
}
