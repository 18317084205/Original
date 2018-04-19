package com.jianbo.toolkit.http;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jianbo on 2018/4/9.
 */

public class RequestUtils {
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

}
