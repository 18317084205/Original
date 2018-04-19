package com.jianbo.toolkit.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jianbo on 2018/4/10.
 */

public class RequestManager {

    private Map<String, List<String>> requests;

    private static class CreateRequestManager {
        private static final RequestManager manager = new RequestManager();
    }

    public static RequestManager getInstance() {
        return CreateRequestManager.manager;
    }

    private RequestManager() {
        requests = new HashMap<>();
    }

    public boolean addRequest(String tag, String url, Map<String, String> params) {
        String requestName = RequestUtils.urlJoint(tag + url, params);
        return addRequest(tag, requestName);
    }

    public boolean addRequest(String tag, String requestName) {
        List<String> requestList = requests.get(tag);
        if (requestList == null) {
            requestList = new ArrayList<>();
            requestList.add(requestName);
            requests.put(tag, requestList);
            return true;
        }

        if (requestList.contains(requestName)) {
            return false;
        }

        requestList.add(requestName);
        return true;
    }

    public void removeRequest(String tag, String requestName) {
        List<String> requestList = requests.get(tag);
        if (requestList == null) {
            return;
        }

        if (requestList.contains(requestName)) {
            requestList.remove(requestName);
        }
    }

    public void removeReq(String tag, String url, Map<String, String> params) {
        String requestName = RequestUtils.urlJoint(tag + url, params);
        removeRequest(tag, requestName);
    }

    public void removeReq(String tag) {
        requests.remove(tag);
    }

    public void removeAll() {
        requests.clear();
    }
}
