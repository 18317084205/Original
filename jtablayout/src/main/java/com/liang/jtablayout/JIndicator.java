package com.liang.jtablayout;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;

public class JIndicator extends Indicator {
    private Paint indicatorPaint;
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

    @Override
    public void setWidthScale(float scale) {
        this.widthScale = scale;
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
            if (width <= 0) {
                return;
            }
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
    public void draw(Canvas canvas, float left, float right, int tabHeight) {
        if (type == TYPE_LINE) {
            if (gravity == Gravity.TOP) {
                canvas.drawLine(left, 0, right, 0, indicatorPaint);
            } else {
                canvas.drawLine(left, tabHeight, right, tabHeight, indicatorPaint);
            }
        }

        if (type == TYPE_TRIANGLE) {
            if (width <= 0) {
                if (trianglePath == null) {
                    trianglePath = new Path();
                }
                float triangleWidth = right - left;
                trianglePath.reset();
                trianglePath.moveTo(0, 0);
                trianglePath.lineTo(triangleWidth, 0);
                if (gravity == Gravity.TOP) {
                    trianglePath.lineTo(triangleWidth / 2, height);
                    trianglePath.close();
                    canvas.translate(left, 0);
                } else {
                    trianglePath.lineTo(triangleWidth / 2, -height);
                    trianglePath.close();
                    canvas.translate(left, tabHeight);
                }
            } else {
                if (gravity == Gravity.TOP) {
                    canvas.translate(left, 0);
                } else {
                    canvas.translate(left, tabHeight);
                }
            }
            canvas.drawPath(trianglePath, indicatorPaint);
        }
    }
}
