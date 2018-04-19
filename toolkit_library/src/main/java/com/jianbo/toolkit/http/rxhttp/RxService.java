package com.jianbo.toolkit.http.rxhttp;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Jianbo on 2018/4/10.
 */

public interface RxService {
    @Streaming
    @GET
    Observable<ResponseBody> getResponse(@Url String url);

    @GET
    Observable<ResponseBody> getResponse(@Url String url, @HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);

    @POST
    Observable<ResponseBody> postResponse(@Url String url, @HeaderMap Map<String, String> headers, @Body Map<String, String> parameter);

}
