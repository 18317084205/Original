package com.jianbo.toolkit.http.base;

import com.jianbo.toolkit.http.ReqManager;
import com.jianbo.toolkit.http.callback.BitmapCallback;
import com.jianbo.toolkit.http.callback.FileCallback;

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
