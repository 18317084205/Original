package com.jianbo.toolkit.base;

public interface BaseView extends IView {
    void showDialog(String msg);
    void dismissDialog();
    <D> void showDataFromPresenter(D data);
}
