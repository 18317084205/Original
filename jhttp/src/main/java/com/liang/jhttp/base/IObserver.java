package com.liang.jhttp.base;

import java.lang.reflect.ParameterizedType;

/**
 * Created by Jianbo on 2018/4/13.
 */

public abstract class IObserver<T> {
    public Class<T> getTClass() {
        return (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
