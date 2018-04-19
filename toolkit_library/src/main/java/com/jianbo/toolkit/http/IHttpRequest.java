package com.jianbo.toolkit.http;

import android.support.annotation.NonNull;

import com.jianbo.toolkit.http.callback.ICallBack;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by Jianbo on 2018/4/3.
 */

public abstract class IHttpRequest {

    public abstract int connectTimeout();

    public abstract int readTimeOut();

    public abstract int writeTimeout();

    public abstract String baseUrl();

    public abstract Map<String, String> header();

    protected abstract <T> void post(String tag, String url, Map<String, String> headers, Map<String, String> params, ICallBack<T> callback);

    protected abstract <T> void get(String tag, String url, Map<String, String> headers, Map<String, String> params, ICallBack<T> callback);

    protected abstract <T> void get(String tag, String url, ICallBack<T> callback);

    protected abstract void cancel(String tag);

    protected abstract void cancelAll();

    protected <T> void sendSuccessCallback( T data, final ICallBack callback) {
        Observable.just(data).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<T>() {
            @Override
            public void accept(T t) throws Exception {
                if (checkCallBackNotNull(callback)) {
                    callback.onSuccess(t);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {

            }
        });

    }

    protected void sendFailCallback(Exception e, final ICallBack callback) {
        Observable.just(e).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Exception>() {
            @Override
            public void accept(Exception e1) throws Exception {
                if (checkCallBackNotNull(callback)) {
                    callback.onFailure(e1);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {

            }
        });
    }

    protected void sendStartCallback(final ICallBack callback) {
        Observable.just("start").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (checkCallBackNotNull(callback)) {
                    callback.onStart();
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {

            }
        });

    }

    protected void sendCompleteCallback(final ICallBack callback) {
        Observable.just("complete").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (checkCallBackNotNull(callback)) {
                    callback.onComplete();
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {

            }
        });
    }

    protected void sendProgress(final float progress, final long downloaded, final long total, final ICallBack callback) {
        Observable.just("Progress").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (checkCallBackNotNull(callback)) {
                    callback.onProgress(progress, downloaded, total);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {

            }
        });

    }

    private boolean checkCallBackNotNull(ICallBack callback) {
        return callback != null;
    }

}
