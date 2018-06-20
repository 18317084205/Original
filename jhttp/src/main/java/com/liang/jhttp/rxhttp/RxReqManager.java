package com.liang.jhttp.rxhttp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.reactivex.disposables.Disposable;

public class RxReqManager {
    private static Map<String, ConcurrentLinkedQueue<Disposable>> disposables = new HashMap<>();

    public static void addDisposable(Disposable d, String tag) {
        ConcurrentLinkedQueue<Disposable> queue = disposables.get(tag);
        if (queue == null) {
            queue = new ConcurrentLinkedQueue<>();
            disposables.put(tag, queue);
        }
        queue.add(d);
    }

    public static void rxCancel(String tag) {
        ConcurrentLinkedQueue<Disposable> queue = disposables.get(tag);
        if (queue != null) {
            for (Disposable disposable : queue) {
                if (queue != null && !disposable.isDisposed()) {
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
