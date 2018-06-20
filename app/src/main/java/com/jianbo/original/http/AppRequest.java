package com.jianbo.original.http;


import com.liang.jhttp.okhttp.OkRequest;

/**
 * Created by Jianbo on 2018/4/12.
 */

public class AppRequest extends OkRequest {
    @Override
    public String baseUrl() {
        return "https://www.baidu.com";
    }
}
