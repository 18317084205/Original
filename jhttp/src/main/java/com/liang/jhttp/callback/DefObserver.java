package com.liang.jhttp.callback;


import com.liang.jhttp.CallManager;
import com.liang.jhttp.base.ICallBack;
import com.liang.jhttp.base.ReqResult;
import com.liang.jhttp.rxhttp.HttpExpFactory;
import com.liang.jhttp.rxhttp.RxReqManager;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public abstract class DefObserver<T> implements Observer<ResponseBody> {

    private String tag;
    private ICallBack<T> callBack;

    public DefObserver(String tag, ICallBack<T> callBack) {
        this.tag = tag;
        this.callBack = callBack;
    }

    @Override
    public void onSubscribe(Disposable d) {
        RxReqManager.addDisposable(d, tag);
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        if (callBack != null) {
            try {
                ReqResult<T> result = callBack.convertSuccess(responseBody);
                if (result != null) {
                    CallManager.sendSuccess(result, callBack);
                }
            } catch (Exception e) {
                CallManager.sendFail(HttpExpFactory.handleException(e), callBack);
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        CallManager.sendFail(e, callBack);
    }

    @Override
    public void onComplete() {
        CallManager.sendComplete(callBack);
    }
}
