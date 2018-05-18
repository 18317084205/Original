package com.jianbo.toolkit.http.callback;

import com.jianbo.toolkit.http.HttpResult;
import com.jianbo.toolkit.http.ICallManager;
import com.jianbo.toolkit.http.base.ICallBack;
import com.jianbo.toolkit.http.rxhttp.RxRequestManager;
import com.jianbo.toolkit.prompt.ApplicationUtils;

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
        RxRequestManager.addDisposable(d, tag);
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        if (ApplicationUtils.isNotNull(callBack)) {
            try {
                HttpResult<T> result = callBack.convertSuccess(responseBody);
                if (ApplicationUtils.isNotNull(result)) {
                    ICallManager.sendSuccess(result, callBack);
                }
            } catch (Exception e) {
                e.printStackTrace();
                ICallManager.sendFail(e, callBack);
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        ICallManager.sendFail((Exception) e, callBack);
    }

    @Override
    public void onComplete() {
        ICallManager.sendComplete(callBack);
    }
}
