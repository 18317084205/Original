package com.jianbo.toolkit.base;

public abstract class IModel {
    public abstract void cancel(String tag);

    public interface Callback<T> {
        void onSuccess(int code, T result);

        void onFailure(int code, String msg);
    }
}
