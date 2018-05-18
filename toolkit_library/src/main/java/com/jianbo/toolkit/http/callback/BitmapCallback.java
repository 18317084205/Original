package com.jianbo.toolkit.http.callback;

import android.graphics.Bitmap;

import com.jianbo.toolkit.http.HttpResult;
import com.jianbo.toolkit.http.base.ICallBack;
import com.jianbo.toolkit.http.rxhttp.RxFactory;

import java.io.File;
import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * Created by Jianbo on 2018/4/18.
 */

public abstract class BitmapCallback extends ICallBack<Bitmap> {

    @Override
    public HttpResult<Bitmap> convertSuccess(ResponseBody responseBody) throws IOException {
        HttpResult<Bitmap> result = new HttpResult<>();
        result.setCode(0);
        result.setData(RxFactory.transformBitmap(responseBody));
        return result;
    }
}
