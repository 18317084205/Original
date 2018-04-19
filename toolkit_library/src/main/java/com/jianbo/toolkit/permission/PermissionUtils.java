package com.jianbo.toolkit.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.jianbo.toolkit.prompt.LogUtils;
import com.jianbo.toolkit.prompt.SPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jianbo on 2018/3/29.
 */

public class PermissionUtils {
    public static final String TAG = PermissionUtils.class.getSimpleName();
    private static final int REQUEST_CODE = 0x200;
    private static final int PERMISSION_UNTREATED = 1;
    private static final int PERMISSION_GRANTED = 2;
    private static final int PERMISSION_DENIED = 3;
    private static final int PERMISSION_BANNED = 4;
    private static PermissionsListener permissionsListener;

    public static void checkPermissions(Activity activity, String[] permissions, PermissionsListener listener) {
        permissionsListener = listener;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            callbackRequestResult(PERMISSION_GRANTED, permissions);
            return;
        }
        List<String> denied = new ArrayList<>();
        List<String> banned = new ArrayList<>();
        for (String permission : permissions) {
            LogUtils.d(TAG, "permission:" + permission + ";permissionCode:" + ContextCompat.checkSelfPermission(activity, permission));
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                LogUtils.d(TAG, "permission:" + permission + ";shouldShowRequest:" + ActivityCompat.shouldShowRequestPermissionRationale(activity, permission));
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) &&
                        SPUtils.getInstance(activity).getBoolean(TAG)) {
                    banned.add(permission);
                } else {
                    denied.add(permission);
                }
            }
        }

        if (!banned.isEmpty()) {
            callbackRequestResult(PERMISSION_BANNED, banned.toArray(new String[banned.size()]));
            return;
        }

        if (!denied.isEmpty()) {
            callbackRequestResult(PERMISSION_UNTREATED, denied.toArray(new String[denied.size()]));
            return;
        }

        callbackRequestResult(PERMISSION_GRANTED, permissions);

    }

    public static void requestPermissions(Activity activity, String[] permissions) {
        ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE);
    }

    public static void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        LogUtils.d(TAG, "requestCode:" + requestCode + ";permissions:" + permissions + ";grantResults:" + grantResults);

        if (requestCode != REQUEST_CODE) {
            return;
        }
        if (grantResults.length > 0) {
            List<String> denied = new ArrayList<>();
            List<String> banned = new ArrayList<>();
            for (int j = 0; j < grantResults.length; j++) {
                if (grantResults[j] != PackageManager.PERMISSION_GRANTED) {

                    LogUtils.d(TAG, "permission:" + permissions[j] + ";;shouldShowRequest:" + ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[j]));
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[j])) {
                        banned.add(permissions[j]);
                    } else {
                        denied.add(permissions[j]);
                    }
                }
            }

            SPUtils.getInstance(activity).putBoolean(TAG, true);

            if (!banned.isEmpty()) {
                callbackRequestResult(PERMISSION_BANNED, banned.toArray(new String[banned.size()]));
                return;
            }

            if (!denied.isEmpty()) {
                callbackRequestResult(PERMISSION_DENIED, denied.toArray(new String[denied.size()]));
                return;
            }

            callbackRequestResult(PERMISSION_GRANTED, permissions);
        }


    }

    private static void callbackRequestResult(int permissionType, String... permissions) {
        if (permissionsListener == null) {
            return;
        }

        switch (permissionType) {
            case PERMISSION_UNTREATED:
                permissionsListener.onPermissionUntreated(permissions);
                break;
            case PERMISSION_GRANTED:
                permissionsListener.onPermissionGranted(permissions);
                break;
            case PERMISSION_DENIED:
                permissionsListener.onPermissionDenied(permissions);
                break;
            case PERMISSION_BANNED:
                permissionsListener.onPermissionBanned(permissions);
                break;
        }
    }


}
