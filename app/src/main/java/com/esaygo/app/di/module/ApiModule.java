package com.esaygo.app.di.module;


import com.esaygo.app.di.qualifier.ApiUrl;
import com.esaygo.app.utils.PrefsUtils;
import com.esaygo.app.utils.network.api.ApiService;
import com.esaygo.app.utils.network.helper.OkHttpHelper;
import com.esaygo.app.utils.network.helper.RetrofitHelper;
import com.esaygo.app.utils.network.support.ApiConstants;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 描述:Api网络模型
 */
@Module
public class ApiModule {
    public Retrofit createRetrofit(Retrofit.Builder builder, OkHttpClient client, String url) {
        return builder
                .baseUrl(url)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Singleton
    @Provides
    public OkHttpClient provideOkHttpClient() {
        return OkHttpHelper.getInstance().getOkHttpClient();
    }

    @Singleton
    @Provides
    public Retrofit.Builder provideRetrofitBuilder() {
        return new Retrofit.Builder();
    }

    @Singleton
    @Provides
    public RetrofitHelper provideRetrofitHelper(ApiService apiService) {
        return new RetrofitHelper(apiService);
    }


    @Singleton
    @Provides
    @ApiUrl
    public Retrofit provideApiRetrofit(Retrofit.Builder builder, OkHttpClient client) {
        return createRetrofit(builder, client, getAPI_BASE_URL());
    }

    @Singleton
    @Provides
    public ApiService provideApiService(@ApiUrl Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }

    private static String getAPI_BASE_URL() {
        return ApiConstants.API_BASE_URL;
    }

}
