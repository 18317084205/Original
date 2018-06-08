package com.jianbo.toolkit.http;

import com.jianbo.toolkit.http.base.IRequest;
import com.jianbo.toolkit.http.base.Request;
import com.jianbo.toolkit.http.okhttp.OkRequest;
import com.jianbo.toolkit.prompt.ToastUtils;

/**
 * Created by Jianbo on 2018/4/3.
 */

public class HttpRequest {
    private static Request request;

    public static void init(Request httpRequest) {
        request = httpRequest;
    }

    public static Request getInstance() {
        if (request == null) {
            request = new OkRequest() {
                @Override
                public String baseUrl() {
                    return "";
                }
            };
        }
        return request;
    }
}
