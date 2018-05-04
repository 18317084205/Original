package com.jianbo.toolkit.http.rxhttp;

import com.jianbo.toolkit.http.RequestManager;
import com.jianbo.toolkit.http.callback.ICallBack;
import com.jianbo.toolkit.prompt.LogUtils;
import com.jianbo.toolkit.rxjava.RxUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Created by Jianbo on 2018/4/11.
 */

public abstract class IRxHttp<T> {
    protected RxService rxService;
    private Map<String, ConcurrentLinkedQueue<Disposable>> disposables;

    protected IRxHttp(RxService rxService) {
        this.rxService = rxService;
        this.disposables = new HashMap<>();
    }

    protected void rxGet(final String tag, final String url, Map<String, String> headers, final Map<String, String> params, final ICallBack<T> callBack) {
        LogUtils.e("observer", callBack.getClass().toString());
        sendStart(callBack);
        execute(tag, url, params, rxService.getResponse(url, headers, params), callBack);
    }

    protected void rxGet(final String tag, final String url, final ICallBack<T> callBack) {
        sendStart(callBack);
        LogUtils.e("rxGetBitmap", url);
        execute(tag, url, new HashMap<String, String>(), rxService.getResponse(url), callBack);
    }

    protected void rxPost(final String tag, final String url, Map<String, String> headers, final Map<String, String> params, final ICallBack<T> callBack) {
        sendStart(callBack);
        execute(tag, url, params, rxService.postResponse(url, headers, params), callBack);
    }

    private void execute(final String tag, final String url, final Map<String, String> params, Observable<ResponseBody> observable, final ICallBack<T> callBack) {
        observable
                .map(new Function<ResponseBody, T>() {
                    @Override
                    public T apply(ResponseBody responseBody) throws Exception {
                        return callBack.convertSuccess(responseBody);
                    }
                })
                .onErrorResumeNext(new RxFactory.HttpResponseFunc<T>())
                .compose(RxUtils.<T>observableTransformer())
                .subscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d, tag);
                    }

                    @Override
                    public void onNext(T t) {
                        sendSuccess(t, callBack);
                    }

                    @Override
                    public void onError(Throwable e) {
                        sendError(e, callBack);
                    }

                    @Override
                    public void onComplete() {
                        RequestManager.getInstance().removeReq(tag, url, params);
                        sendComplete(callBack);
                    }
                });
    }

    private void addDisposable(Disposable d, String tag) {
        ConcurrentLinkedQueue<Disposable> queue = disposables.get(tag);
        if (!isNotNull(queue)) {
            queue = new ConcurrentLinkedQueue<>();
            disposables.put(tag, queue);
        }
        queue.add(d);
        LogUtils.e("disposable", queue.size() + "");
    }

    protected void rxCancel(String tag) {
        ConcurrentLinkedQueue<Disposable> queue = disposables.get(tag);
        if (isNotNull(queue)) {
            for (Disposable disposable : queue) {
                if (isNotNull(disposable) && !disposable.isDisposed()) {
                    LogUtils.e("dispose", tag);
                    disposable.dispose();
                    queue.remove(disposable);
                    LogUtils.e("queue", queue.size() + "");
                }
            }
            disposables.remove(tag);
        }
        LogUtils.e("disposable", disposables.size() + "");
    }

    protected void rxCancelAll() {

    }

    private void sendStart(ICallBack<T> callBack) {
        if (isNotNull(callBack)) {
            callBack.onStart();
        }
    }

    private void sendSuccess(T t, ICallBack<T> callBack) {
        if (isNotNull(callBack) && isNotNull(t)) {
            callBack.onSuccess(t);
        }
    }


    private void sendError(Throwable e, ICallBack<T> callBack) {
        if (isNotNull(callBack)) {
            callBack.onFailure(e);
        }
    }

    private void sendComplete(ICallBack<T> callBack) {
        if (isNotNull(callBack)) {
            callBack.onComplete();
        }
    }

    private boolean isNotNull(Object o) {
        return o != null;
    }

}
