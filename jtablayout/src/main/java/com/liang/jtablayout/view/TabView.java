package com.liang.jtablayout.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.FrameLayout;

public abstract class TabView extends FrameLayout {
    public static final int INVALID_POSITION = -1;
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private Object tag;
    private Drawable icon;
    private CharSequence title;
    private CharSequence contentDesc;
    private int position = INVALID_POSITION;
    private int mode = VERTICAL;

    public TabView(Context context) {
        super(context);
        setClickable(true);
    }

    public void reset() {
        setSelected(false);
    }

    public abstract void update();

}
