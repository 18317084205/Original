package com.jianbo.toolkit.http.rxhttp;

import com.jianbo.toolkit.http.okhttp.OkInterceptor;
import com.jianbo.toolkit.http.callback.ICallBack;
import com.jianbo.toolkit.prompt.Utils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jianbo on 2018/4/10.
 */

public class RxHttp<T> extends IRxHttp<T> {
    public static final String TAG = "RxHttp";

    private RxHttp(RxService rxService) {
        super(rxService);
    }

    public void get(String tag, String url, Map<String, String> headers, Map<String, String> params, ICallBack<T> callBack) {
        rxGet(tag, url, headers, params, callBack);
    }

    public void get(String tag, String url, ICallBack<T> callBack) {
        rxGet(tag, url, callBack);
    }

    public void post(String tag, String url, Map<String, String> headers, Map<String, String> params, ICallBack<T> callBack) {
        rxPost(tag, url, headers, params, callBack);
    }

    public void cancel(String tag) {
        rxCancel(tag);
    }

    public void cancelAll() {
        rxCancelAll();
    }

    public static final class Builder {
        private String baseUrl = "http://192.168.1.1/";
        private Converter.Factory converterFactory;
        private Retrofit.Builder retrofitBuilder;
        private OkHttpClient.Builder okHttpBuilder;

        public Builder() {
            okHttpBuilder = new OkHttpClient.Builder();
            retrofitBuilder = new Retrofit.Builder();
        }

        public Builder connectTimeout(int timeout) {
            if (timeout > 0) {
                okHttpBuilder.connectTimeout(timeout, TimeUnit.SECONDS);
            }
            return this;
        }

        public Builder writeTimeout(int timeout) {
            if (timeout > 0) {
                okHttpBuilder.writeTimeout(timeout, TimeUnit.SECONDS);
            }
            return this;
        }

        public Builder readTimeout(int timeout) {
            if (timeout > 0) {
                okHttpBuilder.readTimeout(timeout, TimeUnit.SECONDS);
            }
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder addConverterFactory(Converter.Factory factory) {
            this.converterFactory = factory;
            return this;
        }

        public Builder addHeader(Map<String, String> headers) {
            okHttpBuilder.addInterceptor(new OkInterceptor(Utils.checkNotNull(headers, "header == null")));
            return this;
        }

        public RxHttp build() {

            retrofitBuilder.baseUrl(baseUrl);

            if (converterFactory == null) {
                converterFactory = GsonConverterFactory.create();
            }

            retrofitBuilder.addConverterFactory(converterFactory);

            retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());

            retrofitBuilder.client(okHttpBuilder.build());
            return new RxHttp(retrofitBuilder.build().create(RxService.class));
        }
    }
}
