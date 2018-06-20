package com.liang.jpermission.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jianbo on 2018/3/30.
 */

public abstract class SharedPreHelper {

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value).apply();
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }
}
