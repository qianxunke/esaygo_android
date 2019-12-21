package com.esaygo.app.utils.network.support;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Logger拦截器
 */
public final class LoggerInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    @Override
    public Response intercept(Chain chain) throws IOException {
        if (ApiConstants.isDebug) {
            Request request = chain.request();
            Buffer buffer = new Buffer();
            if(request.body()!=null) {
                request.body().writeTo(buffer);
            }
            Logger.d(String.format("Sending request %s\nheaders: %s \n",
                    request.url(), request.headers()));
            Logger.json(buffer.readString(UTF8));
            long t1 = System.nanoTime();
            Response response = chain.proceed(chain.request());
            long t2 = System.nanoTime();
            Logger.d(String.format(Locale.getDefault(), "Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));
            MediaType mediaType = response.body().contentType();
            String content = response.body().string();
            Logger.json(content);
            return response
                    .newBuilder()
                    .body(ResponseBody.create(mediaType, content))
                    .build();
        } else {
            return chain.proceed(chain.request());
        }
    }
}
