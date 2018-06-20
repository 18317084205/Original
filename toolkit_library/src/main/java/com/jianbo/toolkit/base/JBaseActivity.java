package com.jianbo.toolkit.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.jianbo.toolkit.prompt.ActivityUtils;
import com.jianbo.toolkit.prompt.AppUtils;
import com.jianbo.toolkit.prompt.ToastUtils;
import com.jianbo.toolkit.prompt.WindowUtils;
import com.liang.jpermission.presenter.PermissionPresenter;
import com.liang.jpermission.view.PermissionView;
import com.liang.jstatusbar.JStatusBar;

public abstract class JBaseActivity<P extends BasePresenter> extends AppCompatActivity implements PermissionView {

    private PermissionPresenter permissionPresenter;
    protected P iPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        JStatusBar.with(this).init();
        iPresenter = getPresenter();
        initViewOrData(savedInstanceState);
        //930086807
    }


    protected void checkPermissions(String... permissions) {
        permissionPresenter = new PermissionPresenter(this, this);
        permissionPresenter.checkPermissions(permissions);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppUtils.isNotNull(permissionPresenter)) {
            permissionPresenter.onResume();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (AppUtils.isNotNull(permissionPresenter)) {
            permissionPresenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (AppUtils.isNotNull(permissionPresenter)) {
            permissionPresenter.unBindView();
        }

        if (AppUtils.isNotNull(iPresenter)) {
            iPresenter.unSubmersible();
        }
    }
    protected <T extends View> T initViewById(int id) {
        return (T) super.findViewById(id);
    }

    protected abstract P getPresenter();

    protected abstract int getLayoutId();

    protected abstract void initViewOrData(Bundle savedInstanceState);

    protected ActivityUtils.SkipFactory createActivity(Class clazz) {
        return new ActivityUtils.SkipFactory(this, clazz);
    }

    @Override
    public void onPermissionDenied() {
        finish();
    }
    public void showToast(String msg) {
        ToastUtils.showToast(this, msg);
    }

    public void showToast(int resId) {
        ToastUtils.showToast(this, getString(resId));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //获取当前获得当前焦点所在View
            View view = getCurrentFocus();
            if (WindowUtils.isClickEditText(view, event)) {
                WindowUtils.hideSoftKeyboard(view);
            }
            return super.dispatchTouchEvent(event);
        }
        if (getWindow().superDispatchTouchEvent(event)) {
            return true;
        }
        return onTouchEvent(event);
    }
}
