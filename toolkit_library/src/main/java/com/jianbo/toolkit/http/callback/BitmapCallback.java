package com.jianbo.toolkit.http.callback;

import android.graphics.Bitmap;

import com.jianbo.toolkit.http.rxhttp.RxFactory;

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * Created by Jianbo on 2018/4/18.
 */

public abstract class BitmapCallback extends ICallBack<Bitmap> {

    @Override
    public Bitmap convertSuccess(ResponseBody responseBody) throws IOException {
        return RxFactory.transformBitmap(responseBody);
    }
}
