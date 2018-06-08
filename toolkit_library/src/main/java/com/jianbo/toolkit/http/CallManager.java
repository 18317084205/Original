package com.jianbo.toolkit.http;

import com.jianbo.toolkit.http.base.ReqResult;
import com.jianbo.toolkit.http.base.ICallBack;
import com.jianbo.toolkit.http.execption.HttpThrowable;
import com.jianbo.toolkit.prompt.AppUtils;
import com.jianbo.toolkit.prompt.rxjava.RxUtils;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

public class CallManager {

    public static <T> void sendSuccess(ReqResult<T> data, final ICallBack callback) {

        Flowable.just(data).compose(RxUtils.<ReqResult<T>>flyableTransformer()).subscribe(new Consumer<ReqResult<T>>() {
            @Override
            public void accept(ReqResult<T> tReqResult) throws Exception {
                if (AppUtils.isNotNull(callback)) {
                    callback.onSuccess(tReqResult.getCode(), tReqResult.getData(), tReqResult.getMsg());
                }
            }
        });
    }


    public static void sendFail(Throwable e, final ICallBack callback) {
        Flowable.just(e).compose(RxUtils.<Throwable>flyableTransformer()).subscribe(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                if (AppUtils.isNotNull(callback) && e instanceof HttpThrowable) {
                    HttpThrowable throwable = (HttpThrowable) e;
                    callback.onFailure(throwable.getCode(), throwable.getMessage());
                }
            }
        });
    }

    public static void sendStart(final ICallBack callback) {
        if (AppUtils.isNotNull(callback)) {
            callback.onStart();
        }
    }

    public static void sendComplete(final ICallBack callback) {
        Flowable.just(callback).compose(RxUtils.<ICallBack>flyableTransformer()).subscribe(new Consumer<ICallBack>() {
            @Override
            public void accept(ICallBack iCallBack) throws Exception {
                if (AppUtils.isNotNull(iCallBack)) {
                    iCallBack.onComplete();
                }
            }
        });
    }

    public static void sendProgress(final float progress, final long downloaded, final long total, final ICallBack callback) {
        Flowable.just(progress).compose(RxUtils.<Float>flyableTransformer()).subscribe(new Consumer<Float>() {
            @Override
            public void accept(Float progress) throws Exception {
                if (AppUtils.isNotNull(callback)) {
                    callback.onProgress(progress, downloaded, total);
                }
            }
        });
    }

}
