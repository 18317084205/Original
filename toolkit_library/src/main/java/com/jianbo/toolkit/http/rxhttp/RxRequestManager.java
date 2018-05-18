package com.jianbo.toolkit.http.rxhttp;

import com.jianbo.toolkit.prompt.ApplicationUtils;
import com.jianbo.toolkit.prompt.LogUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.reactivex.disposables.Disposable;

public class RxRequestManager {
    private static Map<String, ConcurrentLinkedQueue<Disposable>> disposables = new HashMap<>();

    public static void addDisposable(Disposable d, String tag) {
        ConcurrentLinkedQueue<Disposable> queue = disposables.get(tag);
        if (!ApplicationUtils.isNotNull(queue)) {
            queue = new ConcurrentLinkedQueue<>();
            disposables.put(tag, queue);
        }
        queue.add(d);
    }

    public static void rxCancel(String tag) {
        ConcurrentLinkedQueue<Disposable> queue = disposables.get(tag);
        if (ApplicationUtils.isNotNull(queue)) {
            for (Disposable disposable : queue) {
                if (ApplicationUtils.isNotNull(disposable) && !disposable.isDisposed()) {
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
