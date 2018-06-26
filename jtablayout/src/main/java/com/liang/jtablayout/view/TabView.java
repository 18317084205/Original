package com.liang.jtablayout.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.widget.FrameLayout;

import com.liang.jtablayout.utils.DrawableUtils;

public abstract class TabView extends FrameLayout {
    public static final int INVALID_POSITION = -1;
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private Drawable icon;
    private CharSequence title;
    private CharSequence contentDesc;
    private int position = INVALID_POSITION;
    private int mode = VERTICAL;
    private ColorStateList titleColor;

    private Drawable[] icons;


    public TabView(Context context) {
        super(context);
        setClickable(true);
    }

    public void setTabPadding(int mTabPaddingStart,int mTabPaddingTop,int mTabPaddingEnd,int mTabPaddingBottom) {
        ViewCompat.setPaddingRelative(this, mTabPaddingStart, mTabPaddingTop,
                mTabPaddingEnd, mTabPaddingBottom);
    }

    public void reset() {
        setSelected(false);
        icon = null;
        title = null;
        contentDesc = null;
        position = INVALID_POSITION;
        mode = VERTICAL;
    }
    public Drawable[] getIcons() {
        return icons;
    }
    
    public Drawable getDefaultIcon() {
        return icon;
    }

    public TabView setIcon(Drawable defaultIcon) {
        this.icon = defaultIcon;
        return this;
    }

    public TabView setIcon(Drawable defaultIcon, Drawable selectedIcon) {
        if (defaultIcon != null && selectedIcon != null) {
            icons = new Drawable[2];
            icons[0] = defaultIcon;
            icons[1] = selectedIcon;
        }
        return this;
    }


    public TabView setIconColor(ColorStateList iconColor) {
        if (icon != null && iconColor != null) {
            icons = new Drawable[2];
            icons[0] = DrawableUtils.tintDrawable(icon, titleColor.getColorForState(EMPTY_STATE_SET, Color.GRAY));
            icons[1] = DrawableUtils.tintDrawable(icon, titleColor.getColorForState(SELECTED_STATE_SET, Color.BLACK));
        }
        return this;
    }

    public CharSequence getTitle() {
        return title;
    }

    public TabView setTitle(CharSequence title) {
        this.title = title;
        return this;
    }

    public CharSequence getContentDesc() {
        return contentDesc;
    }

    public TabView setContentDesc(CharSequence contentDesc) {
        this.contentDesc = contentDesc;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public TabView setPosition(int position) {
        this.position = position;
        return this;
    }

    public int getMode() {
        return mode;
    }

    public TabView setMode(int mode) {
        this.mode = mode;
        return this;
    }

    public TabView setTitleColor(ColorStateList titleColor) {
        this.titleColor = titleColor;
        return this;
    }

    public ColorStateList getTitleColor() {
        return titleColor;
    }

}
