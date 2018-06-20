package com.liang.jhttp.callback;

import android.graphics.Bitmap;

import com.liang.jhttp.base.ICallBack;
import com.liang.jhttp.base.ReqResult;
import com.liang.jhttp.rxhttp.RxFactory;

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * Created by Jianbo on 2018/4/18.
 */

public abstract class BitmapCallback extends ICallBack<Bitmap> {

    @Override
    public ReqResult<Bitmap> convertSuccess(ResponseBody responseBody) throws IOException {
        ReqResult<Bitmap> result = new ReqResult<>();
        result.setCode(0);
        result.setData(RxFactory.transformBitmap(responseBody));
        return result;
    }
}
