package com.jianbo.toolkit.http.callback;

import com.jianbo.toolkit.http.base.ReqResult;
import com.jianbo.toolkit.http.CallManager;
import com.jianbo.toolkit.http.base.ICallBack;
import com.jianbo.toolkit.http.rxhttp.HttpExpFactory;
import com.jianbo.toolkit.http.rxhttp.RxReqManager;
import com.jianbo.toolkit.prompt.AppUtils;

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
        if (AppUtils.isNotNull(callBack)) {
            try {
                ReqResult<T> result = callBack.convertSuccess(responseBody);
                if (AppUtils.isNotNull(result)) {
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
