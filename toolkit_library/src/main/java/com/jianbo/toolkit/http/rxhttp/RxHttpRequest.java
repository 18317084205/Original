package com.jianbo.toolkit.http.rxhttp;

import android.support.v4.BuildConfig;

import com.jianbo.toolkit.http.RequestManager;
import com.jianbo.toolkit.http.callback.DefObserver;
import com.jianbo.toolkit.http.base.ICallBack;
import com.jianbo.toolkit.http.base.IHttpRequest;
import com.jianbo.toolkit.http.okhttp.HeaderInterceptor;
import com.jianbo.toolkit.rxjava.RxUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jianbo on 2018/4/10.
 */

public abstract class RxHttpRequest extends IHttpRequest {
    private OkHttpClient.Builder okHttpBuilder;
    private Retrofit.Builder retrofitBuilder;
    private RxService apiService;

    public RxHttpRequest() {
        okHttpBuilder = new OkHttpClient.Builder();
        okHttpBuilder.addInterceptor(new HeaderInterceptor(header()));
        okHttpBuilder.connectTimeout(connectTimeout(), TimeUnit.SECONDS);
        okHttpBuilder.readTimeout(readTimeOut(), TimeUnit.SECONDS);
        okHttpBuilder.writeTimeout(writeTimeout(), TimeUnit.SECONDS);

        if (BuildConfig.DEBUG) {
            okHttpBuilder.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS));
            okHttpBuilder.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }

        retrofitBuilder = new Retrofit.Builder();

        retrofitBuilder.baseUrl(baseUrl());
        retrofitBuilder.addConverterFactory(converterFactory());

        retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());

        retrofitBuilder.client(okHttpBuilder.build());
        apiService = retrofitBuilder.build().create(RxService.class);
    }

    @Override
    public int connectTimeout() {
        return 10;
    }

    @Override
    public int readTimeOut() {
        return 10;
    }

    @Override
    public int writeTimeout() {
        return 10;
    }

    @Override
    public String baseUrl() {
        return "http://192.168.0.1/";
    }

    @Override
    public Map<String, String> header() {
        return new HashMap<>();
    }

    protected Converter.Factory converterFactory() {
        return GsonConverterFactory.create();
    }

    protected <T> void post(String tag, String url, Map<String, String> params, ICallBack<T> callback) {
        post(tag, url, new HashMap<String, String>(), params, callback);
    }

    @Override
    public <T> void post(final String tag, final String url, Map<String, String> headers, final Map<String, String> params, ICallBack<T> callBack) {
        apiService.postResponse(url, headers, params)
                .compose(RxUtils.<ResponseBody>observableTransformer())
                .onErrorResumeNext(new RxFactory.HttpResponseFunc<ResponseBody>())
                .subscribe(new DefObserver(tag, callBack){
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        RequestManager.getInstance().removeReq(tag, url, params);
                    }
                    @Override
                    public void onComplete() {
                        super.onComplete();
                        RequestManager.getInstance().removeReq(tag, url, params);
                    }
                });
    }

    protected <T> void get(String tag, String url, Map<String, String> params, ICallBack<T> callback) {
        get(tag, url, new HashMap<String, String>(), params, callback);
    }

    @Override
    public <T> void get(final String tag, final String url, Map<String, String> headers, final Map<String, String> params, ICallBack<T> callback) {
        apiService.getResponse(url, headers, params)
                .compose(RxUtils.<ResponseBody>observableTransformer())
                .onErrorResumeNext(new RxFactory.HttpResponseFunc<ResponseBody>())
                .subscribe(new DefObserver<T>(tag, callback){

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        RequestManager.getInstance().removeReq(tag, url, params);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        RequestManager.getInstance().removeReq(tag, url, params);
                    }
                });
    }


    @Override
    public <T> void get(final String url, ICallBack<T> callback) {
        apiService.get(url)
                .compose(RxUtils.<ResponseBody>observableTransformer())
                .onErrorResumeNext(new RxFactory.HttpResponseFunc<ResponseBody>())
                .subscribe(new DefObserver(url, callback){

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        RequestManager.getInstance().removeRequest(url);
                    }
                    @Override
                    public void onComplete() {
                        super.onComplete();
                        RequestManager.getInstance().removeRequest(url);
                    }
                });;
    }

    @Override
    public void cancel(String tag) {
        RxRequestManager.rxCancel(tag);
    }

    @Override
    public void cancelAll() {
        RxRequestManager.rxCancelAll();
    }

}
