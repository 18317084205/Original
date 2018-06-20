package com.liang.jhttp.callback;


import com.liang.jhttp.base.ICallBack;
import com.liang.jhttp.base.ReqResult;
import com.liang.jhttp.rxhttp.RxFactory;

import okhttp3.ResponseBody;

/**
 * Created by Jianbo on 2018/4/18.
 */

public abstract class FileCallback extends ICallBack<String> {

    public String fileDir() {
        return "";
    }

    public String fileName() {
        return "";
    }

    @Override
    public ReqResult<String> convertSuccess(ResponseBody responseBody) throws Exception {
        String path = RxFactory.transformFile(responseBody, fileDir(), fileName(), this);
        ReqResult<String> result = new ReqResult();
        result.setCode(0);
        result.setData(path);
        return result;
    }
}
