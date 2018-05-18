package com.jianbo.toolkit.http;

import android.content.Context;

import com.jianbo.toolkit.http.base.ICallBack;
import com.jianbo.toolkit.http.base.IHttpRequest;
import com.jianbo.toolkit.http.callback.BitmapCallback;
import com.jianbo.toolkit.http.callback.FileCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jianbo on 2018/4/3.
 */

public class HttpUtils {
    private IHttpRequest mHttpRequest;
    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    public static void init(Context context, IHttpRequest httpRequest) {
        sContext = context;
        getInstance().httpRequest(httpRequest);
    }

    private static class createHttpUtils {
        private static final HttpUtils httpUtils = new HttpUtils();
    }

    public static HttpUtils getInstance() {
        return createHttpUtils.httpUtils;
    }

    public HttpUtils httpRequest(IHttpRequest httpRequest) {
        this.mHttpRequest = httpRequest;
        return this;
    }

    public void download(String url, FileCallback callback) {
        if (isWay(url)) return;
        mHttpRequest.get(url, callback);
    }

    public void bitmap(String url, BitmapCallback callback) {
        if (isWay(url)) return;
        mHttpRequest.get(url, callback);
    }

    public <T> void get(String tag, String url, ICallBack<T> callback) {
        if (isWay(tag, url, new HashMap<String, String>())) return;
        get(tag, url, new HashMap<String, String>(), callback);
    }

    public <T> void get(String tag, String url, Map<String, String> params, ICallBack<T> callback) {
        get(tag, url, new HashMap<String, String>(), params, callback);
    }

    public <T> void get(String tag, String url, Map<String, String> headers, Map<String, String> params, ICallBack<T> callback) {
        if (isWay(tag, url, params)) return;
        mHttpRequest.get(tag, url, headers, params, callback);
    }

    public <T> void post(String tag, String url, Map<String, String> params, ICallBack<T> callback) {
        post(tag, url, new HashMap<String, String>(), params, callback);
    }

    public <T> void post(String tag, String url, Map<String, String> headers, Map<String, String> params, ICallBack<T> callback) {
        if (isWay(tag, url, params)) return;
        mHttpRequest.post(tag, url, headers, params, callback);
    }

    private boolean isWay(String url) {
        return !RequestManager.getInstance().addRequest(url);
    }

    private boolean isWay(String tag, String url, Map<String, String> params) {
        return !RequestManager.getInstance().addRequest(tag, url, params);
    }

    public void cancel(String tag) {
        mHttpRequest.cancel(tag);
        RequestManager.getInstance().removeReq(tag);
    }

    public void cancelAll() {
        mHttpRequest.cancelAll();
        RequestManager.getInstance().removeAll();
    }
}
