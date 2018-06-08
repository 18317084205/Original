package com.jianbo.toolkit.prompt;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.jianbo.toolkit.base.BaseSharedPre;

public class DefSharedPreImp extends BaseSharedPre {
    private static DefSharedPreImp sharedPreImp;

    public DefSharedPreImp(@NonNull SharedPreferences sharedPreferences) {
        super(sharedPreferences);
    }

    public static DefSharedPreImp getInstance(Context context) {
        if (sharedPreImp == null) {
            synchronized (BaseSharedPre.class) {
                if (sharedPreImp == null) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
                    sharedPreImp = new DefSharedPreImp(sharedPreferences);
                }
            }
        }
        return sharedPreImp;
    }
}
