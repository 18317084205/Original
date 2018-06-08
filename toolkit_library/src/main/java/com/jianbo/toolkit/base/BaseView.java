package com.jianbo.toolkit.base;

public interface BaseView<D> extends IView {
    void showDialog(String msg);
    void dismissDialog();
    void showDataFromPresenter(D data);
}
