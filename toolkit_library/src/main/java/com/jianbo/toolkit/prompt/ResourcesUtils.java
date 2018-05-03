package com.jianbo.toolkit.prompt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

/**
 * Created by Jianbo on 2018/5/3.
 */

public class ResourcesUtils {

    private ResourcesUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }


    /**
     * 获取配置的string
     * @param context   上下文
     * @param id        资源id
     */
    public static String getString(Context context, @StringRes int id) {
        return context.getResources().getString(id);
    }

    /**
     * 获取配置的color
     * @param context   上下文
     * @param id        资源id
     */
    public static int getColor(Context context, @ColorRes int id) {
        return ContextCompat.getColor(context, id);
    }

    /**
     * 将资源图片转化为Bitmap
     * @param context   上下文
     * @param id        资源id
     */
    public static Bitmap getBitmap(Context context, @DrawableRes int id) {
        return ((BitmapDrawable) ContextCompat.getDrawable(context, id)).getBitmap();
    }

}
