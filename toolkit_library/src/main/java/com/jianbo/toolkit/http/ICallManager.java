package com.jianbo.toolkit.http;

import com.jianbo.toolkit.http.base.ICallBack;
import com.jianbo.toolkit.prompt.ApplicationUtils;
import com.jianbo.toolkit.rxjava.RxUtils;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class ICallManager {

    public static <T> void sendSuccess(HttpResult<T> data, final ICallBack callback) {

        Flowable.just(data).compose(RxUtils.<HttpResult<T>>flyableTransformer()).subscribe(new Consumer<HttpResult<T>>() {
            @Override
            public void accept(HttpResult<T> tHttpResult) throws Exception {
                if (ApplicationUtils.isNotNull(callback)) {
                    callback.onSuccess(tHttpResult.getCode(), tHttpResult.getData(), tHttpResult.getMsg());
                }
            }
        });
    }


    public static void sendFail(Exception e, final ICallBack callback) {
        Flowable.just(e).compose(RxUtils.<Throwable>flyableTransformer()).subscribe(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                if (ApplicationUtils.isNotNull(callback)) {
                    callback.onFailure(e);
                }
            }
        });
    }

    public static void sendStart(final ICallBack callback) {
        if (ApplicationUtils.isNotNull(callback)) {
            callback.onStart();
        }
    }

    public static void sendComplete(final ICallBack callback) {
        Flowable.just(callback).compose(RxUtils.<ICallBack>flyableTransformer()).subscribe(new Consumer<ICallBack>() {
            @Override
            public void accept(ICallBack iCallBack) throws Exception {
                if (ApplicationUtils.isNotNull(iCallBack)) {
                    iCallBack.onComplete();
                }
            }
        });
    }

    public static void sendProgress(final float progress, final long downloaded, final long total, final ICallBack callback) {
        Flowable.just(progress).compose(RxUtils.<Float>flyableTransformer()).subscribe(new Consumer<Float>() {
            @Override
            public void accept(Float progress) throws Exception {
                if (ApplicationUtils.isNotNull(callback)) {
                    callback.onProgress(progress, downloaded, total);
                }
            }
        });
    }


}
