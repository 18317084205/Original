package com.liang.jhttp.base;

import okhttp3.ResponseBody;

/**
 * Created by Jianbo on 2018/4/3.
 */


public abstract class ICallBack<T> extends IObserver<T> {


    public ICallBack() {
    }

    public void onStart() {
    }

    public void onComplete() {

    }

    public abstract ReqResult<T> convertSuccess(ResponseBody responseBody) throws Exception;

    public abstract void onSuccess(int code, T result, String msg);

    public void onFailure(int code, String msg) {
    }

    public void onProgress(float progress, long downloaded, long total) {
    }

    public boolean cache() {
        return false;
    }
}
