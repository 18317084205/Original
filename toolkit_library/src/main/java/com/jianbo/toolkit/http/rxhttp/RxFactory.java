package com.jianbo.toolkit.http.rxhttp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.jianbo.toolkit.http.HttpUtils;
import com.jianbo.toolkit.http.callback.ICallBack;
import com.jianbo.toolkit.prompt.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by Jianbo on 2018/4/11.
 */

public class RxFactory {

    private static final String TAG = RxFactory.class.getSimpleName();

    public static Bitmap transformBitmap(ResponseBody response) {
        return BitmapFactory.decodeStream(response.byteStream());
    }

    public static File transformFile(ResponseBody response, String fileDir, String fileName, final ICallBack<File> callBack) throws Exception {
        String type = "." + response.contentType().subtype();
        if (TextUtils.isEmpty(fileName)) {
            fileName = System.currentTimeMillis() + type;
        }

        LogUtils.d("fileName", fileName);

        if (TextUtils.isEmpty(fileDir)) {
            fileDir = HttpUtils.getContext().getExternalFilesDir(null) + File.separator;
        }

        LogUtils.d("fileDir", fileDir);

        byte[] buf = new byte[4096];
        int len = 0;
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            is = response.byteStream();
            final long total = response.contentLength();
            long sum = 0;

            File dir = new File(fileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);
                final long finalSum = sum;
                Observable.just(finalSum).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(@NonNull Long aLong) throws Exception {
                                if (callBack != null) {
                                    callBack.onProgress(finalSum * 1.0f / total, finalSum, total);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {

                            }
                        });
            }
            fos.flush();
            return file;

        } catch (IOException e){
            Observable.just(e).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<IOException>() {
                        @Override
                        public void accept(@NonNull IOException e1) throws Exception {
                            if (callBack != null) {
                                callBack.onFailure(e1);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(@NonNull Throwable throwable) throws Exception {

                        }
                    });
            return null;
        }finally {
            if (is != null) try {
                is.close();
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static <T> ObservableTransformer<T, T> transformer() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static class HttpResponseFunc<T> implements Function<Throwable, Observable<T>> {
        @Override
        public Observable<T> apply(Throwable throwable) throws Exception {
            return Observable.error(RxException.handleException(throwable));
        }
    }
}
