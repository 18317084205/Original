package com.jianbo.toolkit.http.okhttp;

import com.jianbo.toolkit.http.IHttpRequest;
import com.jianbo.toolkit.http.RequestManager;
import com.jianbo.toolkit.http.RequestUtils;
import com.jianbo.toolkit.http.callback.ICallBack;
import com.jianbo.toolkit.prompt.LogUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Jianbo on 2018/4/9.
 */

public class OkHttpRequest extends IHttpRequest {

    private OkHttpClient okHttpClient;
    private String baseUrl;

    public OkHttpRequest() {
        okHttpClient = new OkHttpClient();
        okHttpClient.newBuilder()
                .addInterceptor(new OkInterceptor(header()))
                .connectTimeout(connectTimeout(), TimeUnit.SECONDS)
                .readTimeout(readTimeOut(), TimeUnit.SECONDS)
                .writeTimeout(writeTimeout(), TimeUnit.SECONDS).build();
        baseUrl = baseUrl();
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

    protected <T> void get(String tag, String url, Map<String, String> params, ICallBack<T> callback) {
        get(tag, url, new HashMap<String, String>(), params, callback);
    }

    @Override
    public <T> void get(String tag, String url, Map<String, String> headers, Map<String, String> params,
                        ICallBack<T> callback) {
        sendStartCallback(callback);
        String doUrl = RequestUtils.urlJoint(baseUrl + url, params);
        executeGet(tag, doUrl, headers, params, callback);
    }

    private <T> void executeGet(String tag, String url, Map<String, String> headers, Map<String, String> params, ICallBack<T> callback) {
        Headers heads = Headers.of(headers);
        Request request = new Request.Builder().tag(tag).url(url).headers(heads).build();
        request(tag, url, params, callback, request);
    }

    private <T> void request(final String tag, final String url, final Map<String, String> params, final ICallBack<T> callback, Request request) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                RequestManager.getInstance().removeReq(tag, url, params);
                LogUtils.e("onFailure", call.request().toString());
                LogUtils.e("onFailure", e.getMessage());
                sendFailResultCallback(e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtils.e("onResponse", response.toString());
                RequestManager.getInstance().removeReq(tag, url, params);
                int responseCode = response.code();
                if (responseCode != 404 && responseCode < 500) {
                    if (callback != null) {
                        try {
                            T data = callback.convertSuccess(response.body());
                            sendSuccessResultCallback(data, callback);
                        } catch (Exception e) {
                            sendFailResultCallback(e, callback);
                        }
                    }
                } else {
                    sendFailResultCallback(new Exception(response.toString()), callback);
                }
            }
        });
    }

    @Override
    protected <T> void get(String tag, String url, ICallBack<T> callback) {
        sendStartCallback(callback);
        executeGet(tag, url, new HashMap<String, String>(), new HashMap<String, String>(), callback);
    }

    protected <T> void post(String tag, String url, Map<String, String> params, ICallBack<T> callback) {
        post(tag, url, new HashMap<String, String>(), params, callback);
    }

    @Override
    public <T> void post(String tag, String url, Map<String, String> headers, Map<String, String> params,
                         ICallBack<T> callback) {
        sendStartCallback(callback);
        Headers heads = Headers.of(headers);
        RequestBody requestBody = RequestBody.create(null, RequestUtils.upMap(params));
        Request request = new Request.Builder().url(baseUrl + url).headers(heads).post(requestBody).build();
        request(tag, url, params, callback, request);
    }

    @Override
    public void cancel(String tag) {
        for (Call call : okHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : okHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    @Override
    public void cancelAll() {
        for (Call call : okHttpClient.dispatcher().queuedCalls()) {
            call.cancel();
        }
        for (Call call : okHttpClient.dispatcher().runningCalls()) {
            call.cancel();
        }
    }

    public <T> void sendSuccessResultCallback(final T data, final ICallBack callBack) {
        sendSuccessCallback(data, callBack);
        sendCompleteCallback(callBack);
    }

    public void sendFailResultCallback(final Exception e, final ICallBack callBack) {
        sendFailCallback(e, callBack);
        sendCompleteCallback(callBack);
    }
}
