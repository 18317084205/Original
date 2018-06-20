package com.liang.jstatusbar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.liang.jstatusbar.utils.SystemUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Jianbo on 2018/5/3.
 */

public class JStatusBar {

    /**
     * 利用反射获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(Activity activity) {
        int result = 0;
        //获取状态栏高度的资源id
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获得 ActionBar 的高度
     *
     * @param context
     * @return
     */
    public static int getActionBarHeight(Context context) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            TypedValue tv = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
            result = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        return result;
    }



    private Activity mActivity;
    //状态栏颜色
    private int themeColor = Color.TRANSPARENT;
    private boolean withoutUseStatusBarTextColor;


    public JStatusBar useDarkTextColor(boolean useDarkColor) {
        this.withoutUseStatusBarTextColor = useDarkColor;
        return this;
    }

    public JStatusBar setThemeColor(int color) {
        this.themeColor = color;
        return this;
    }

    private JStatusBar(Activity activity) {
        mActivity = activity;
    }

    public static JStatusBar with(Activity activity) {
        return new JStatusBar(activity);
    }

    public void init() {
        fullScreen(mActivity,themeColor);
        setStatusTextColor(withoutUseStatusBarTextColor, mActivity);
    }

    /**
     * 通过设置全屏，设置状态栏透明
     *
     * @param activity
     */
    private void fullScreen(Activity activity, int themeColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window .setStatusBarColor(themeColor);
                //导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
//                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }

    /**
     * 设置状态栏文字色值为深色调
     *
     * @param useDart  是否使用深色调
     * @param activity activity
     */
    private void setStatusTextColor(boolean useDart, Activity activity) {
        if (SystemUtils.isFlyme()) {
            processFlyMe(activity, useDart);
        } else if (SystemUtils.isMIUI()) {
            processMIUI(activity, useDart);
        } else {
            if (useDart) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity.getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    activity.getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                }
            }
            activity.getWindow().getDecorView().findViewById(android.R.id.content).setPadding(0, 0, 0, 0);
        }
    }

    /**
     * 改变魅族的状态栏字体为黑色，要求FlyMe4以上
     */
    private void processFlyMe(Activity activity, boolean darkMode) {
        Window window = activity.getWindow();
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meiZuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meiZuFlags.setAccessible(true);
                int bit = darkFlag.getInt((Object) null);
                int value = meiZuFlags.getInt(lp);
                if (darkMode) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }

                meiZuFlags.setInt(lp, value);
                window.setAttributes(lp);
            } catch (Exception var8) {
                Log.w("JStatusBar", "setStatusBarDarkIcon: failed");
            }
        }
    }

    /**
     * 改变小米的状态栏字体颜色为黑色, 要求MIUI6以上  lightStatusBar为真时表示黑色字体
     */
    private void processMIUI(Activity activity, boolean darkMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // 即基于 Android 6.0 ，开发版 7.7.13 及以后版本
            compatHighMIUI(activity, darkMode);
        } else {
            compatLowMIUI(activity, darkMode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void compatHighMIUI(Activity activity, boolean darkmode) {
        View decorView = activity.getWindow().getDecorView();
        if (darkmode) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            int flag = decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decorView.setSystemUiVisibility(flag);
        }
    }

    /**
     * 兼容低版本miui
     *
     * @param activity activity
     * @param darkMode 是否夜间模式
     */
    private void compatLowMIUI(Activity activity, boolean darkMode) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkMode ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}

