package com.jianbo.original;

import android.content.Context;
import android.content.Intent;

import com.jianbo.original.splash.GuideActivity;
import com.jianbo.toolkit.prompt.LogUtils;
import com.tamic.novate.Novate;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jianbo on 2018/4/9.
 */

public class MovieLoaderj {

    String baseUrl = "http://ip.taobao.com/";
    private Novate novate;
    private Map<String, Object> parameters = new HashMap<String, Object>();
    private Map<String, String> headers = new HashMap<>();
    private Context context;
    public MovieLoaderj(){
        parameters.put("ip", "21.22.11.33");
        headers.put("Accept", "application/json");

        novate = new Novate.Builder(context)
                .connectTimeout(20)
                .writeTimeout(15)
                .baseUrl(baseUrl)
                .addHeader(headers)
                .addLog(true)
                .build();


        novate.rxGet("service/getIpInfo.php", parameters, new RxStringCallback() {

            @Override
            public void onNext(Object tag, String response) {
                LogUtils.d(tag+"", response);
            }

            @Override
            public void onError(Object tag, Throwable e) {
            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }

        });


        float f = 123.22f;
        String.valueOf(f);


    }
}


