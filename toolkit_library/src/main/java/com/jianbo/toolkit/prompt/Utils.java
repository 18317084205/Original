package com.jianbo.toolkit.prompt;

import android.os.Looper;

import java.lang.reflect.Field;

/**
 * Created by Jianbo on 2018/4/10.
 */

public class Utils {
    public static <T> T checkNotNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }

    public static boolean checkMain() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    /**
     * 获取反射字段
     *
     * @param className 反射类
     * @param fieldName 字段名
     * @param obj       反射对象
     * @return 反射字段
     */
    public static Object getReflectField(String className, String fieldName, Object obj) {
        try {
            Class<?> clazz = Class.forName(className);
            Field field = clazz.getDeclaredField(fieldName);
            return field.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isNotNull(Object o) {
        return o != null;
    }
}