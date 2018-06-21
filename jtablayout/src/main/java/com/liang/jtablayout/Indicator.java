package com.liang.jtablayout;

import android.graphics.Canvas;
import android.graphics.Color;

public abstract class Indicator {
    public static final int TYPE_LINE = 0;
    public static final int TYPE_RECT = 1;
    public static final int TYPE_TRIANGLE = 2;
    protected int type = TYPE_LINE;
    protected int width = 0;
    protected int height = 10;
    protected int radius = 0;
    protected int color = Color.GRAY;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public abstract void setType(int type);

    public abstract void setWidth(int width);

    public abstract void setHeight(int height);

    public abstract void setRadius(int radius);

    public abstract void setColor(int color);

    //    public abstract void draw(Canvas canvas, float left, float top, float right, float bottom);
    public abstract void draw(Canvas canvas, float left, int tabHeight);
}
