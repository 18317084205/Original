package com.liang.jtablayout;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;

public class JIndicator extends Indicator {
    private Rect mIndicatorRect = new Rect();
    private GradientDrawable mIndicatorDrawable = new GradientDrawable();
    private Paint indicatorPaint;
    private Paint mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTrianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path trianglePath;
    private int gravity = Gravity.BOTTOM;

    public JIndicator() {
        indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicatorPaint.setColor(color);
        indicatorPaint.setStrokeWidth(height);
    }

    @Override
    public void setType(int type) {
        this.type = type;
        reInitIndicator();
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
        reInitIndicator();
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
        reInitIndicator();
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
        reInitIndicator();
    }

    @Override
    public void setRadius(int radius) {
        this.radius = radius;
        reInitIndicator();
    }

    @Override
    public void setColor(int color) {
        this.color = color;
        indicatorPaint.setColor(color);
    }

    private void reInitIndicator() {
        if (type == TYPE_LINE) {
            indicatorPaint.setStrokeWidth(height);
        }

        if (type == TYPE_RECT) {

        }

        if (type == TYPE_TRIANGLE) {
            trianglePath = new Path();
            trianglePath.moveTo(0, 0);
            trianglePath.lineTo(width, 0);
            if (gravity == Gravity.TOP) {
                trianglePath.lineTo(width / 2, height);
            } else {
                trianglePath.lineTo(width / 2, -height);
            }
            trianglePath.close();
        }
    }

    @Override
    public void draw(Canvas canvas, float left, int tabHeight) {
        if (type == TYPE_LINE) {
            if (gravity == Gravity.TOP) {
                canvas.drawLine(left, 0, left + width, 0, indicatorPaint);
            }else {
                canvas.drawLine(left, tabHeight, left + width, tabHeight, indicatorPaint);
            }
        }

        if (type == TYPE_TRIANGLE) {
            if (gravity == Gravity.TOP) {
                canvas.translate(left, 0);
            }else {
                canvas.translate(left, tabHeight);
            }
            canvas.drawPath(trianglePath, indicatorPaint);
        }
    }


}
