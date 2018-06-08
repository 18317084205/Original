package com.jianbo.toolkit.permission.view;

public interface PermissionView {
    void onPermissionGranted(String[] permissions);
    void onPermissionDenied();
}
