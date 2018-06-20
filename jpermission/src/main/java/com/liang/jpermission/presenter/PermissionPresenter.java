package com.liang.jpermission.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.liang.jpermission.JPermission;
import com.liang.jpermission.PermissionsListener;
import com.liang.jpermission.R;
import com.liang.jpermission.view.PermissionView;

public class PermissionPresenter implements PermissionsListener {
    private Context context;
    private boolean isOpenedSettings;
    private String[] permissions;
    private PermissionView permissionView;

    public PermissionPresenter(Context context, PermissionView permissionView) {
        this.context = context;
        this.permissionView = permissionView;
    }

    public void checkPermissions(String... permissions) {
        this.permissions = permissions;
        JPermission.checkPermissions(context, this.permissions, this);
    }

    public void onResume() {
        if (isOpenedSettings) {
            JPermission.checkPermissions(context, this.permissions, this);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        JPermission.onRequestPermissionsResult((Activity) context, requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionUntreated(String[] permissions) {
        JPermission.requestPermissions((Activity) context, permissions);
    }

    @Override
    public void onPermissionGranted(String[] permissions) {
        if (permissionView != null) {
            permissionView.onPermissionGranted(permissions);
        }
    }

    @Override
    public void onPermissionDenied(final String[] permissions) {
        isOpenedSettings = false;
        if (permissions.length > 0) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            try {
                dialog.setIcon(context.getPackageManager().getApplicationIcon(context.getPackageName()));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            dialog.setTitle(R.string.app_system_tip);
            dialog.setMessage(R.string.app_permission_denied);
            dialog.setCancelable(false);
            dialog.setPositiveButton(R.string.app_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    JPermission.requestPermissions((Activity) context, permissions);
                }
            });

            dialog.setNegativeButton(R.string.app_exit, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (null != permissionView) {
                        permissionView.onPermissionDenied();
                    }
                }
            });
            dialog.show();
        }
    }

    @Override
    public void onPermissionBanned(String[] permissions) {
        if (permissions.length > 0) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            try {
                dialog.setIcon(context.getPackageManager().getApplicationIcon(context.getPackageName()));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            dialog.setTitle(R.string.app_system_tip);
            dialog.setMessage(R.string.app_permission_banned);
            dialog.setCancelable(false);
            dialog.setPositiveButton(R.string.app_setting, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isOpenedSettings = true;
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                }
            });

            dialog.setNegativeButton(R.string.app_exit, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (null != permissionView) {
                        permissionView.onPermissionDenied();
                    }
                }
            });
            dialog.show();
        }
    }

    public void unBindView() {
        permissionView = null;
    }
}
