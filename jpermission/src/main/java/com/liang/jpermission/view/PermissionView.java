package com.liang.jpermission.view;

public interface PermissionView {
    void onPermissionGranted(String[] permissions);
    void onPermissionDenied();
}
