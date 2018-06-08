package com.jianbo.toolkit.http;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jianbo on 2018/4/10.
 */

public class ReqManager {

    private static final int MIN_REQUEST_DELAY_TIME = 5000;
    private Map<String, Set<String>> requests;
    private Set<String> getRequests;
    private long lastRequestTime = 0;

    private static class CreateRequestManager {
        private static final ReqManager manager = new ReqManager();
    }

    public static ReqManager getInstance() {
        return CreateRequestManager.manager;
    }

    private ReqManager() {
        requests = new HashMap<>();
        getRequests = new HashSet<>();
    }

    public boolean addRequest(String url) {
        return getRequests.add(url);
    }

    public boolean addRequest(String tag, String url, Map<String, String> params) {
        String requestName = ParamsUtils.urlJoint(tag + url, params);
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
        String requestName = ParamsUtils.urlJoint(tag + url, params);
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
