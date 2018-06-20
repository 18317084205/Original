package com.liang.jhttp.okhttp;

import com.liang.jhttp.BuildConfig;
import com.liang.jhttp.CallManager;
import com.liang.jhttp.ParamsUtils;
import com.liang.jhttp.ReqManager;
import com.liang.jhttp.base.ICallBack;
import com.liang.jhttp.base.ReqResult;
import com.liang.jhttp.callback.BitmapCallback;
import com.liang.jhttp.callback.FileCallback;
import com.liang.jhttp.rxhttp.HttpExpFactory;
import com.liang.jhttp.utils.LogUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.HttpException;

/**
 * Created by Jianbo on 2018/4/9.
 */

public abstract class OkRequest extends com.liang.jhttp.base.Request {
    private static final String TAG = OkRequest.class.getSimpleName();
    private OkHttpClient okHttpClient;
    private String baseUrl;

    public OkRequest() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new PInterceptor(commonHeaders()));
        httpClient.connectTimeout(connectTimeout(), TimeUnit.SECONDS);
        httpClient.readTimeout(readTimeOut(), TimeUnit.SECONDS);
        httpClient.writeTimeout(writeTimeout(), TimeUnit.SECONDS);

        if (BuildConfig.DEBUG) {
            httpClient.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS));
//            httpClient.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }
        okHttpClient = httpClient.build();
        baseUrl = baseUrl();
        LogUtils.d(TAG, baseUrl);
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
    public Map<String, String> commonHeaders() {
        return new HashMap<>();
    }

    protected <T> void get(String tag, String url, ICallBack<T> callback) {
        get(tag, url, new HashMap<String, String>(), callback);
    }

    public <T> void get(String tag, String url, Map<String, String> params, ICallBack<T> callback) {
        get(tag, url, new HashMap<String, String>(), params, callback);
    }

    @Override
    public <T> void get(String tag, String url, Map<String, String> headers, Map<String, String> params,
                        ICallBack<T> callback) {
        if (isWay(tag, url, params)) return;
        CallManager.sendStart(callback);
        String doUrl = ParamsUtils.urlJoint(baseUrl + url, params);
        Headers heads = Headers.of(headers);
        Request request = new Request.Builder().tag(tag).url(doUrl).headers(heads).build();
        request(tag, doUrl, params, callback, request);
    }

    @Override
    public <T> void get(String url, ICallBack<T> callback) {
        if (isWay(url)) return;
        CallManager.sendStart(callback);
        Request request = new Request.Builder().tag(url).url(url).build();
        request(url, url, new HashMap<String, String>(), callback, request);
    }

    public <T> void post(String tag, String url, Map<String, String> params, ICallBack<T> callback) {
        post(tag, url, new HashMap<String, String>(), params, callback);
    }

    @Override
    public <T> void post(String tag, String url, Map<String, String> headers, Map<String, String> params,
                         ICallBack<T> callback) {
        if (isWay(tag, url, params)) return;
        CallManager.sendStart(callback);
        Headers heads = Headers.of(headers);
//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), ParamsUtils.upMap(params));
//        LogUtils.e("requestBody", "requestBody:" + requestBody.toString() + ParamsUtils.upMap(params));
        Request request = new Request.Builder().url(baseUrl + url).headers(heads).post(ParamsUtils.upFormBody(params)).build();
        request(tag, url, params, callback, request);
    }

    @Override
    public void download(String url, FileCallback callback) {
        get(url, callback);
    }

    @Override
    public void bitmap(String url, BitmapCallback callback) {
        get(url, callback);
    }

    @Override
    public void cancel(String tag) {
        super.cancel(tag);
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
        super.cancelAll();
        for (Call call : okHttpClient.dispatcher().queuedCalls()) {
            call.cancel();
        }
        for (Call call : okHttpClient.dispatcher().runningCalls()) {
            call.cancel();
        }
    }

    public <T> void sendSuccessResultCallback(ReqResult<T> data, ICallBack callBack) {
        CallManager.sendSuccess(data, callBack);
        CallManager.sendComplete(callBack);
    }

    public void sendFailResultCallback(Exception e, ICallBack callBack) {
        LogUtils.e(TAG, e.getMessage());
        CallManager.sendFail(e, callBack);
        CallManager.sendComplete(callBack);
    }

    private <T> void request(final String tag, final String url, final Map<String, String> params, final ICallBack<T> callback, Request request) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ReqManager.getInstance().removeReq(tag, url, params);
                sendFailResultCallback(HttpExpFactory.handleException(e), callback);
            }

            @Override
            public void onResponse(Call call, Response response) {
                ReqManager.getInstance().removeReq(tag, url, params);
                if (response.isSuccessful()) {
                    if (callback != null) {
                        try {
                            ReqResult<T> data = callback.convertSuccess(response.body());
                            sendSuccessResultCallback(data, callback);
                        } catch (Exception e) {
                            sendFailResultCallback(HttpExpFactory.handleException(e), callback);
                        }
                    }
                } else {
                    HttpException httpException = new HttpException(retrofit2.Response.error(response.body(), response));
                    sendFailResultCallback(HttpExpFactory.handleException(httpException), callback);
                }
            }
        });
    }
}
