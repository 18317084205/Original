package com.jianbo.toolkit.http.rxhttp;

import com.jianbo.toolkit.prompt.AppUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.reactivex.disposables.Disposable;

public class RxReqManager {
    private static Map<String, ConcurrentLinkedQueue<Disposable>> disposables = new HashMap<>();

    public static void addDisposable(Disposable d, String tag) {
        ConcurrentLinkedQueue<Disposable> queue = disposables.get(tag);
        if (!AppUtils.isNotNull(queue)) {
            queue = new ConcurrentLinkedQueue<>();
            disposables.put(tag, queue);
        }
        queue.add(d);
    }

    public static void rxCancel(String tag) {
        ConcurrentLinkedQueue<Disposable> queue = disposables.get(tag);
        if (AppUtils.isNotNull(queue)) {
            for (Disposable disposable : queue) {
                if (AppUtils.isNotNull(disposable) && !disposable.isDisposed()) {
                    disposable.dispose();
                    queue.remove(disposable);
                }
            }
            disposables.remove(tag);
        }
    }

    public static void rxCancelAll() {
        for (String tag : disposables.keySet()) {
            rxCancel(tag);
        }
    }
}
