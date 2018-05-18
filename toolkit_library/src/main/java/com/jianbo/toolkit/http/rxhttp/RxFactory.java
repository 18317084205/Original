package com.jianbo.toolkit.http.rxhttp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.jianbo.toolkit.http.HttpResult;
import com.jianbo.toolkit.http.HttpUtils;
import com.jianbo.toolkit.http.ICallManager;
import com.jianbo.toolkit.http.base.ICallBack;
import com.jianbo.toolkit.prompt.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Created by Jianbo on 2018/4/11.
 */

public class RxFactory {

    private static final String TAG = RxFactory.class.getSimpleName();

    public static Bitmap transformBitmap(ResponseBody response) {
        return BitmapFactory.decodeStream(response.byteStream());
    }

    public static File transformFile(ResponseBody response, String fileDir, String fileName, final ICallBack<String> callBack) throws Exception {
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
                ICallManager.sendProgress(finalSum * 1.0f / total, finalSum, total, callBack);
            }
            fos.flush();
            HttpResult<String> result = new HttpResult<>();
            result.setCode(0);
            result.setData(file.getAbsolutePath());
            ICallManager.sendSuccess(result, callBack);
            return file;
        } catch (IOException e) {
            ICallManager.sendFail(e, callBack);
            return null;
        } finally {
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

    public static class HttpResponseFunc<T> implements Function<Throwable, Observable<T>> {
        @Override
        public Observable<T> apply(Throwable throwable) {
            return Observable.error(RxException.handleException(throwable));
        }
    }
}
