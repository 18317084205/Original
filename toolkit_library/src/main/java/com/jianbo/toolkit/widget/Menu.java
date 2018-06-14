package com.jianbo.toolkit.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class Menu extends RelativeLayout {

    public static final int MODE_VERTICAL = 0;
    public static final int MODE_HORIZONTAL = 1;

    public Menu(@NonNull Context context) {
        super(context);
    }

    public abstract Menu setIcon(int resId);

    public abstract Menu setTitle(String title);

    public abstract Menu setTitle(int strId);

    public abstract Menu setChecked(boolean checked);

    public abstract void refreshMenu();

    public abstract Menu setMode(int mode);

    public abstract Menu setIconColor(int unSelected, int selected);

    public abstract Menu setTitleColor(int unSelected, int selected);

    public abstract void build();

    public void setBadge(int count) {

    }


}
