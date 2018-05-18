package com.jianbo.toolkit.prompt;

import android.os.Looper;

import java.lang.reflect.Field;

/**
 * Created by Jianbo on 2018/4/10.
 */

public class Utils {


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

}
