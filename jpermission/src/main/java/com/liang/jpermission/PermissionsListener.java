package com.liang.jpermission;

/**
 * Created by Jianbo on 2018/3/29.
 */

public interface PermissionsListener {

    void onPermissionUntreated(String[] permissions);

    void onPermissionGranted(String[] permissions);

    void onPermissionDenied(String[] permissions);

    void onPermissionBanned(String[] permissions);
}

