package com.jianbo.toolkit.prompt;

import android.app.Activity;
import android.os.Build;
import android.view.View;

/**
 * Created by Jianbo on 2018/5/3.
 */

public class WindowUtils {
    /**
     * 隐藏虚拟按键，并且全屏
     */
    public static void hideBottomUIMenu(Activity activity) {
        //隐藏虚拟按键，并且全屏
        View decorView = activity.getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= 19) {
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        } else {
            decorView.setSystemUiVisibility(View.GONE);
        }
    }
}
