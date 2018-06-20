package com.liang.jhttp;

import com.liang.jhttp.base.ICallBack;
import com.liang.jhttp.base.ReqResult;
import com.liang.jhttp.execption.HttpThrowable;
import com.liang.jhttp.utils.RxUtils;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

public class CallManager {

    public static <T> void sendSuccess(ReqResult<T> data, final ICallBack callback) {

        Flowable.just(data).compose(RxUtils.<ReqResult<T>>flyableTransformer()).subscribe(new Consumer<ReqResult<T>>() {
            @Override
            public void accept(ReqResult<T> tReqResult) throws Exception {
                if (callback != null) {
                    callback.onSuccess(tReqResult.getCode(), tReqResult.getData(), tReqResult.getMsg());
                }
            }
        });
    }


    public static void sendFail(Throwable e, final ICallBack callback) {
        Flowable.just(e).compose(RxUtils.<Throwable>flyableTransformer()).subscribe(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                if (callback != null && e instanceof HttpThrowable) {
                    HttpThrowable throwable = (HttpThrowable) e;
                    callback.onFailure(throwable.getCode(), throwable.getMessage());
                }
            }
        });
    }

    public static void sendStart(final ICallBack callback) {
        if (callback != null) {
            callback.onStart();
        }
    }

    public static void sendComplete(final ICallBack callback) {
        Flowable.just(callback).compose(RxUtils.<ICallBack>flyableTransformer()).subscribe(new Consumer<ICallBack>() {
            @Override
            public void accept(ICallBack iCallBack) throws Exception {
                if (callback != null) {
                    iCallBack.onComplete();
                }
            }
        });
    }

    public static void sendProgress(final float progress, final long downloaded, final long total, final ICallBack callback) {
        Flowable.just(progress).compose(RxUtils.<Float>flyableTransformer()).subscribe(new Consumer<Float>() {
            @Override
            public void accept(Float progress) throws Exception {
                if (callback != null) {
                    callback.onProgress(progress, downloaded, total);
                }
            }
        });
    }

}
