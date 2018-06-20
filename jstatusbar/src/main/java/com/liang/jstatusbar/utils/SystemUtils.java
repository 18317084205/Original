package com.liang.jstatusbar.utils;

import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

public class SystemUtils {
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    /**
     * 判断手机是否是小米
     */
    public static boolean isMIUI() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "create.prop")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
    }

    /**
     * 判断手机是否是魅族
     *
     * @return
     */
    public static boolean isFlyme() {
        try {
            // Invoke Build.hasSmartBar()
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null || android.text.TextUtils.equals("Meizu", Build.MANUFACTURER);
        } catch (final Exception e) {
            return android.text.TextUtils.equals("Meizu", Build.MANUFACTURER);
        }
    }
}
