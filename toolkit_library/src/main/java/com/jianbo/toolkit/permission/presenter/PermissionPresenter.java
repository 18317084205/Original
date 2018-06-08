package com.jianbo.toolkit.permission.presenter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.jianbo.toolkit.R;
import com.jianbo.toolkit.permission.PermissionUtils;
import com.jianbo.toolkit.permission.PermissionsListener;
import com.jianbo.toolkit.permission.view.PermissionView;
import com.jianbo.toolkit.prompt.AppUtils;
import com.jianbo.toolkit.prompt.DialogUtils;
import com.jianbo.toolkit.widget.DialogBuilder;

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
        PermissionUtils.checkPermissions(context, this.permissions, this);
    }

    public void onResume() {
        if (isOpenedSettings) {
            PermissionUtils.checkPermissions(context, this.permissions, this);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.onRequestPermissionsResult((Activity) context, requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionUntreated(String[] permissions) {
        PermissionUtils.requestPermissions((Activity) context, permissions);
    }

    @Override
    public void onPermissionGranted(String[] permissions) {
        if (AppUtils.isNotNull(permissionView)) {
            permissionView.onPermissionGranted(permissions);
        }
    }

    @Override
    public void onPermissionDenied(final String[] permissions) {
        isOpenedSettings = false;
        if (permissions.length > 0) {
            DialogUtils.showDialog(context, context.getString(R.string.app_system_tip), context.getString(R.string.app_permission_denied),
                    context.getString(R.string.app_ok), context.getString(R.string.app_exit), new DialogBuilder.DialogListener() {
                        @Override
                        public void sure() {
                            PermissionUtils.requestPermissions((Activity) context, permissions);
                        }

                        @Override
                        public void cancel() {
                            if (AppUtils.isNotNull(permissionView)) {
                                permissionView.onPermissionDenied();
                            }
                        }
                    });
        }
    }

    @Override
    public void onPermissionBanned(String[] permissions) {
        if (permissions.length > 0) {
            DialogUtils.showDialog(context, context.getString(R.string.app_system_tip), context.getString(R.string.app_permission_banned),
                    context.getString(R.string.app_setting), context.getString(R.string.app_exit), new DialogBuilder.DialogListener() {
                        @Override
                        public void sure() {
                            isOpenedSettings = true;
                            AppUtils.openAppSettings(context);
                        }

                        @Override
                        public void cancel() {
                            if (AppUtils.isNotNull(permissionView)) {
                                permissionView.onPermissionDenied();
                            }
                        }
                    });
        }
    }

    public void unBindView() {
        permissionView = null;
    }
}
