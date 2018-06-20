package com.liang.jhttp.base;


import com.liang.jhttp.ReqManager;
import com.liang.jhttp.callback.BitmapCallback;
import com.liang.jhttp.callback.FileCallback;

import java.util.Map;

public abstract class Request extends IRequest{

    public abstract void download(String url, FileCallback callback);

    public abstract void bitmap(String url, BitmapCallback callback);

    @Override
    public boolean isWay(String url) {
        return !ReqManager.getInstance().addRequest(url);
    }

    @Override
    public boolean isWay(String tag, String url, Map<String, String> params) {
        return !ReqManager.getInstance().addRequest(tag, url, params);
    }

    @Override
    public void cancel(String tag) {
        ReqManager.getInstance().removeReq(tag);
    }

    @Override
    public void cancelAll() {
        ReqManager.getInstance().removeAll();
    }
}
