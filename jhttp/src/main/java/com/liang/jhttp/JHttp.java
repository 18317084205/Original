package com.liang.jhttp;


import com.liang.jhttp.base.Request;
import com.liang.jhttp.okhttp.OkRequest;

/**
 * Created by Jianbo on 2018/4/3.
 */

public class JHttp {
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
