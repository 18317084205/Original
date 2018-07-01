package com.liang.jtablayout;

import android.graphics.Canvas;
import android.graphics.Color;

public abstract class Indicator {

    private boolean foreground;
    private float widthScale = 0.5f;
    private int width = -1;
    private int height = 20;

    private boolean transitionScroll;

    //    public abstract void draw(Canvas canvas, float left, float top, float right, float bottom);
    public abstract void draw(Canvas canvas, float left, float right, int tabHeight);

    public boolean isForeground() {
        return foreground;
    }

    public void setForeground(boolean foreground) {
        this.foreground = foreground;
    }

    public float getWidthScale() {
        return widthScale;
    }

    public Indicator setWidthScale(float widthScale) {
        this.widthScale = widthScale;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public Indicator setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public Indicator setHeight(int height) {
        this.height = height;
        return this;
    }

    public Indicator setTransitionScroll(boolean transitionScroll) {
        this.transitionScroll = transitionScroll;
        return this;
    }

    public boolean isTransitionScroll() {
        return transitionScroll;
    }
}
