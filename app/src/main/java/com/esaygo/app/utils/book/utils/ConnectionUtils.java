package com.esaygo.app.utils.book.utils;

import android.util.Log;

import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 连接工具类
 */
public class ConnectionUtils {

    /**
     * 获取HTTP会话实例
     */
    public static OkHttpClient  getClient(int timeOut) {
         final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                        Log.i("saveFromResponse",list.toString());
                        if(list.size()>0) {
                            cookieStore.put(httpUrl.host(), list);
                        }
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                        List<Cookie> cookies = cookieStore.get(httpUrl.host());
                        Log.i("loadForRequest",cookies==null?"空":cookies.toString());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .build();

        return okHttpClient;
    }

    /**
     * 伪造Get请求头的方法
     */

    public static class Conversion{
        OkHttpClient okHttpClient;
        Headers headers;

        public OkHttpClient getOkHttpClient() {
            return okHttpClient;
        }

        public void setOkHttpClient(OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
        }

        public Headers getHeaders() {
            return headers;
        }

        public void setHeaders(Headers headers) {
            this.headers = headers;
        }
    }


    /**
     * 伪造Post请求头的方法
     */
    public static HttpPost setReqHeader(HttpPost post,Header[] headers) {
        post.setHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400");
        post.setHeader("Host", "kyfw.12306.cn");
        post.setHeader("X-Requested-With", "XMLHttpRequest");
        post.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        if(headers!=null&&headers.length>0){
            post.setHeaders(headers);
        }
        return post;
    }



}
