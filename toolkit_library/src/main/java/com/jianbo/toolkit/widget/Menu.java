package com.jianbo.toolkit.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class Menu extends RelativeLayout {

    public static final int MODE_VERTICAL = 0;
    public static final int MODE_HORIZONTAL = 1;

    protected OnClickListener clickListener;

    public boolean isChecked;
    public Drawable icon;
    public String title = "";

    public int position = -1;

    public abstract void setClickListener(OnClickListener clickListener);

    public Menu(@NonNull Context context, String title, int imgId) {
        super(context);
        this.title = title;
        this.icon = ContextCompat.getDrawable(context, imgId);
    }

    public abstract Menu setChecked(boolean checked);

    public abstract void refreshMenu();

    public abstract Menu setMode(int mode);

    public abstract Menu setIconColor(int unSelected, int selected);

    public abstract Menu setIcon(int unSelectedImgId, int selectedImgId);

    public abstract Menu setTitleColor(int unSelected, int selected);

    public abstract void create();

    public void setBadge(int count) {
    }

    interface OnClickListener {
        void onClick(Menu menu);
    }

}
