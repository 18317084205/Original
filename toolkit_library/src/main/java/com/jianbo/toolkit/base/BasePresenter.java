package com.jianbo.toolkit.base;

public abstract class BasePresenter<D, T extends BaseView> extends IPresenter {
    protected T view;

    protected boolean notNullViewImp() {
        return view != null;
    }

    public BasePresenter(T view) {
        this.view = view;
    }

    protected void showDialog(String msg) {
        if (notNullViewImp()) {
            view.showDialog(msg);
        }
    }

    protected void dismissDialog() {
        if (notNullViewImp()) {
            view.dismissDialog();
        }
    }

    protected void showDataFromPresenter(D date) {
        if (notNullViewImp()) {
            view.showDataFromPresenter(date);
        }
    }

    protected void error(String msg) {
        if (notNullViewImp()) {
            view.error(msg);
        }
    }

    @Override
    protected void unSubmersible() {
        if (notNullViewImp()) {
            view = null;
        }
    }
}
