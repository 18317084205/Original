package com.jianbo.toolkit.http.rxhttp;

import com.jianbo.toolkit.http.callback.ICallBack;
import com.jianbo.toolkit.http.IHttpRequest;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Converter;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jianbo on 2018/4/10.
 */

public abstract class RxHttpRequest extends IHttpRequest {
    private RxHttp rxHttp;

    public RxHttpRequest() {
        rxHttp = new RxHttp.Builder()
                .connectTimeout(connectTimeout())
                .readTimeout(readTimeOut())
                .writeTimeout(writeTimeout())
                .baseUrl(baseUrl())
                .addConverterFactory(converterFactory())
                .addHeader(header()).build();
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
    public <T> void post(String tag, String url, Map<String, String> headers, Map<String, String> params, ICallBack<T> callBack) {
        rxHttp.post(tag, url, headers, params, callBack);
    }

    protected <T> void get(String tag, String url, Map<String, String> params, ICallBack<T> callback) {
        get(tag, url, new HashMap<String, String>(), params, callback);
    }

    @Override
    public <T> void get(String tag, String url, Map<String, String> headers, Map<String, String> params, ICallBack<T> callback) {
        rxHttp.get(tag, url, headers, params, callback);
    }


    @Override
    public <T> void get(String tag, String url, ICallBack<T> callback) {
        rxHttp.get(tag, url, callback);
    }

    @Override
    public void cancel(String tag) {
        rxHttp.cancel(tag);
    }

    @Override
    public void cancelAll() {
        rxHttp.cancelAll();
    }

}
