package com.liang.jhttp;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.FormBody;

/**
 * Created by Jianbo on 2018/4/9.
 */

public class ParamsUtils {
    /**
     * 拼接url和请求参数
     *
     * @param url    url
     * @param params key value
     * @return String url
     */
    public static String urlJoint(String url, Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        StringBuilder endUrl = new StringBuilder(url);
        boolean isFirst = true;
        Set<Map.Entry<String, String>> entrySet = params.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            if (isFirst && !url.contains("?")) {
                isFirst = false;
                endUrl.append("?");
            } else {
                endUrl.append("&");
            }
            endUrl.append(entry.getKey());
            endUrl.append("=");
            endUrl.append(entry.getValue());
        }
        return endUrl.toString();
    }

    public static String upMap(Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        return new Gson().toJson(params);
    }

    public static String upJson(JSONObject jsonObject) {
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        return jsonObject.toString();
    }

    public static FormBody upFormBody(Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }
}
