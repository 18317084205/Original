package com.jianbo.original.base;

import com.jianbo.toolkit.base.BaseView;
import com.jianbo.toolkit.base.IPresenter;
import com.jianbo.toolkit.base.JBaseActivity;


public abstract class BaseActivityJ<D,P extends IPresenter> extends JBaseActivity implements BaseView {

    @Override
    public void onPermissionGranted(String[] permissions) {

    }

    @Override
    public void showDialog(String msg) {

    }

    @Override
    public void dismissDialog() {

    }

    @Override
    public void error(String msg) {

    }
}
