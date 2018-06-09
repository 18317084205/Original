package com.jianbo.toolkit.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.jianbo.toolkit.permission.presenter.PermissionPresenter;
import com.jianbo.toolkit.permission.view.PermissionView;
import com.jianbo.toolkit.prompt.ActivityUtils;
import com.jianbo.toolkit.prompt.AppUtils;
import com.jianbo.toolkit.prompt.StatusBarUtils;
import com.jianbo.toolkit.prompt.ToastUtils;
import com.jianbo.toolkit.prompt.WindowUtils;
import com.jianbo.toolkit.prompt.rxjava.RxUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public abstract class JBaseActivity<P extends BasePresenter> extends AppCompatActivity implements PermissionView {

    private PermissionPresenter permissionPresenter;
    protected P iPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        StatusBarUtils.with(this).init();
        iPresenter = getPresenter();
        initViewOrData(savedInstanceState);
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

    public void showKeyboard() {
        Disposable disposable = Observable.timer(300, TimeUnit.MILLISECONDS)
                .compose(RxUtils.<Long>observableTransformer())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        WindowUtils.showSoftKeyboard(JBaseActivity.this);
                    }
                });
        RxUtils.addDisposable(this, disposable);
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
