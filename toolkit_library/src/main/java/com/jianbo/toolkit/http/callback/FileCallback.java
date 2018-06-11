package com.jianbo.toolkit.http.callback;


import com.jianbo.toolkit.http.base.ReqResult;
import com.jianbo.toolkit.http.base.ICallBack;
import com.jianbo.toolkit.http.rxhttp.RxFactory;
import com.jianbo.toolkit.prompt.LogUtils;

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
        LogUtils.e("FileCallback", responseBody.contentType() + "");
        String path = RxFactory.transformFile(responseBody, fileDir(), fileName(), this);
        ReqResult<String> result = new ReqResult();
        result.setCode(0);
        result.setData(path);
        return result;
    }
}
