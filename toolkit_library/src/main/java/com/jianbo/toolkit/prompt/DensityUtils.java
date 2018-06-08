package com.jianbo.toolkit.prompt;

import android.content.Context;

/**
 * Created by Jianbo on 2018/5/3.
 */

public class DensityUtils {
    /**
     * 描述：根据手机分辨率把dip转换成px像素
     */
    public static int dip2px(Context context , float dp){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * 描述：根据手机分辨率把px像素转换成dip
     */
    public static int px2dip(Context context , float px){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}
