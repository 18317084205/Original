package com.jianbo.toolkit.http.callback;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jianbo.toolkit.http.HttpResult;
import com.jianbo.toolkit.prompt.LogUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * Created by Jianbo on 2018/4/13.
 */

public abstract class HttpCallback<T> extends ICallBack<T> {

    private T data = null;
    private int code = -1;
    private String msg = "";
    private String dataStr = "";

    @Override
    public T convertSuccess(ResponseBody responseBody) throws IOException {
        Class<T> entityClass = getTClass();
        if (entityClass == String.class) {
            LogUtils.e("onResponse", entityClass.toString());
            return (T) new String(responseBody.bytes());
        }
        String jString = new String(responseBody.bytes());
        return transform(jString, entityClass);
    }

    public T transform(String response, Class classOfT) {
        if (classOfT == HttpResult.class) {
            return (T) new Gson().fromJson(response, classOfT);
        }

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            code = jsonObject.optInt("code");
            msg = jsonObject.optString("msg");
            if (TextUtils.isEmpty(msg)) {
                msg = jsonObject.optString("error");
            }

            if (TextUtils.isEmpty(msg)) {
                msg = jsonObject.optString("message");
            }

            dataStr = jsonObject.opt("data").toString();
            if (TextUtils.isEmpty(dataStr)) {
                dataStr = jsonObject.opt("result").toString();
            }

            if (dataStr.charAt(0) == '{') {
                data = (T) new Gson().fromJson(dataStr, classOfT);
            } else if (dataStr.charAt(0) == '[') {
                dataStr = jsonObject.optJSONArray("data").toString();
                if (TextUtils.isEmpty(dataStr)) {
                    dataStr = jsonObject.optJSONArray("result").toString();
                }
                Type collectionType = new TypeToken<List<T>>() {
                }.getType();
                data = (T) new Gson().fromJson(dataStr, collectionType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
