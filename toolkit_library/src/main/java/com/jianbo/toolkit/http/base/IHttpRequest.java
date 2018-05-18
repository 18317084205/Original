package com.jianbo.toolkit.http.base;

import com.jianbo.toolkit.http.base.ICallBack;

import java.util.Map;

/**
 * Created by Jianbo on 2018/4/3.
 */

public abstract class IHttpRequest {

    public abstract int connectTimeout();

    public abstract int readTimeOut();

    public abstract int writeTimeout();

    public abstract String baseUrl();

    public abstract Map<String, String> header();

    public abstract <T> void post(String tag, String url, Map<String, String> headers, Map<String, String> params, ICallBack<T> callback);

    public abstract <T> void get(String tag, String url, Map<String, String> headers, Map<String, String> params, ICallBack<T> callback);

    public abstract <T> void get(String url, ICallBack<T> callback);

    public abstract void cancel(String tag);

    public abstract void cancelAll();

}
