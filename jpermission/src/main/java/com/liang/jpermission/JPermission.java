package com.liang.jpermission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.liang.jpermission.utils.SharedPreHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jianbo on 2018/3/29.
 */
public class JPermission {
    public static final String TAG = JPermission.class.getSimpleName();
    private static final int REQUEST_CODE = 0x200;
    private static final int PERMISSION_UNTREATED = 1;
    private static final int PERMISSION_GRANTED = 2;
    private static final int PERMISSION_DENIED = 3;
    private static final int PERMISSION_BANNED = 4;
    private static PermissionsListener permissionsListener;

    public static void checkPermissions(Context activity, String[] permissions, PermissionsListener listener) {
        permissionsListener = listener;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            callbackRequestResult(PERMISSION_GRANTED, permissions);
            return;
        }
        List<String> denied = new ArrayList<>();
        List<String> banned = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) activity, permission) &&
                        SharedPreHelper.getBoolean(activity, TAG)) {
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
        if (requestCode != REQUEST_CODE) {
            return;
        }
        if (grantResults.length > 0) {
            List<String> denied = new ArrayList<>();
            List<String> banned = new ArrayList<>();
            for (int j = 0; j < grantResults.length; j++) {
                if (grantResults[j] != PackageManager.PERMISSION_GRANTED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[j])) {
                        banned.add(permissions[j]);
                    } else {
                        denied.add(permissions[j]);
                    }
                }
            }

            SharedPreHelper.putBoolean(activity, TAG, true);

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
