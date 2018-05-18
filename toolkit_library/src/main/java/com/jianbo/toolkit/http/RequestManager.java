package com.jianbo.toolkit.http;

import com.jianbo.toolkit.prompt.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jianbo on 2018/4/10.
 */

public class RequestManager {

    private static final int MIN_REQUEST_DELAY_TIME = 5000;
    private Map<String, Set<String>> requests;
    private Set<String> getRequests;
    private long lastRequestTime = 0;


    private static class CreateRequestManager {
        private static final RequestManager manager = new RequestManager();
    }

    public static RequestManager getInstance() {
        return CreateRequestManager.manager;
    }

    private RequestManager() {
        requests = new HashMap<>();
        getRequests = new HashSet<>();
    }

    public boolean addRequest(String url) {
        return getRequests.add(url);
    }

    public boolean addRequest(String tag, String url, Map<String, String> params) {
        String requestName = RequestUtils.urlJoint(tag + url, params);
        return addRequest(tag, requestName);
    }

    public boolean addRequest(String tag, String requestName) {
        long curRequestTime = System.currentTimeMillis();

        Set<String> requestList = requests.get(tag);
        if (requestList == null) {
            requestList = new HashSet<>();
            requests.put(tag, requestList);
        }
        if (requestList.add(requestName)) {
            lastRequestTime = curRequestTime;
            return true;
        } else if ((curRequestTime - lastRequestTime) >= MIN_REQUEST_DELAY_TIME) {
            lastRequestTime = curRequestTime;
            return true;
        }
        return false;
    }

    public void removeRequest(String url) {
        LogUtils.d("removeRequest :" + url);
        if (getRequests.contains(url)) {
            getRequests.remove(url);
        }
    }

    public void removeRequest(String tag, String requestName) {
        removeRequest(tag);
        Set<String> requestList = requests.get(tag);
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
        removeRequest(tag);
        requests.remove(tag);
    }

    public void removeAll() {
        requests.clear();
        getRequests.clear();
    }
}
