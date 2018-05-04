package com.jianbo.toolkit.http.callback;


import com.jianbo.toolkit.http.rxhttp.RxFactory;

import java.io.File;

import okhttp3.ResponseBody;

/**
 * Created by Jianbo on 2018/4/18.
 */

public abstract class FileCallback extends ICallBack<File> {

    public String fileDir() {
        return "";
    }

    public String fileName() {
        return "";
    }

    @Override
    public File convertSuccess(ResponseBody responseBody) throws Exception {
        return RxFactory.transformFile(responseBody, fileDir(), fileName(), this);
    }
}
