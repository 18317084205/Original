package com.jianbo.toolkit.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.OverScroller;
import android.widget.Scroller;

import com.jianbo.toolkit.prompt.DensityUtils;

public class NavigationView extends ViewGroup {

    private OverScroller scroller;//弹性滑动对象，用于实现View的弹性滑动
    private VelocityTracker velocityTracker;//速度追踪，
    private int childWidth;//子View的宽度
    private int childIndex;//子View索引
    private int childCount;//子View数量
    private int totalWidth = 0;
    private boolean isScroller;

    public void setScroller(boolean isScroller) {
        this.isScroller = isScroller;
        scroller = new OverScroller(getContext());
    }

    public void setScroller(boolean isScroller,Interpolator interpolator) {
        this.isScroller = isScroller;
        scroller = new OverScroller(getContext(), interpolator);
    }
    public NavigationView(Context context) {
        this(context, null);
    }

    public NavigationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int count = getChildCount();
        int tWidth = 0;
        if (isScroller) {
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() == GONE) {
                    continue;
                }
                child.measure(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
                childWidth = child.getMeasuredWidth();
                tWidth += child.getMeasuredWidth();
            }
        } else {
            int maxAvailable = width / (count == 0 ? 1 : count);
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() == GONE) {
                    continue;
                }
                child.measure(MeasureSpec.makeMeasureSpec(maxAvailable, MeasureSpec.EXACTLY),
                        LayoutParams.WRAP_CONTENT);
                ViewGroup.LayoutParams params = child.getLayoutParams();
                params.width = child.getMeasuredWidth();
                tWidth += child.getMeasuredWidth();
            }
        }
        totalWidth = tWidth;
        setMeasuredDimension(
                View.resolveSizeAndState(totalWidth,
                        MeasureSpec.makeMeasureSpec(totalWidth, MeasureSpec.EXACTLY), 0),
                heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        childCount = getChildCount();
        final int width = right - left;
        final int height = bottom - top;
        int used = 0;
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                child.layout(width - used - child.getMeasuredWidth(), 0, width - used, height);
            } else {
                child.layout(used, 0, child.getMeasuredWidth() + used, height);
            }
            used += child.getMeasuredWidth();
        }
    }

    public void addMenu(Menu menu) {
        menu.build();
        addView(menu);
    }

    private void initOrResetVelocityTracker() {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        } else {
            velocityTracker.clear();
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    private boolean inChild(int x) {
        if (childCount > 0) {
            int scrollX = getScrollX();
            if (x < mLastX) {
                return scrollX > totalWidth - DensityUtils.getWidthPixels(getContext());
            } else {
                return scrollX < 0;
            }
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }

    private int mLastX = 0;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        initVelocityTrackerIfNotExists();
        velocityTracker.addMovement(ev);
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (inChild(x)) {
                    initOrResetVelocityTracker();
                    break;
                }
                int deltaX = x - mLastX;
                scrollBy(-deltaX, 0);
                break;
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                velocityTracker.computeCurrentVelocity(1000);
                float xVelocity = velocityTracker.getXVelocity();//获取X方向手指滑动的速度，之前必须调用computeCurrentVelocity（）方法
                if (Math.abs(xVelocity) > 200) {//当滑动速度>200Px/S时
                    childIndex = xVelocity > 0 ? childIndex - 1 : childIndex + 1;
                } else {
                    childIndex = (scrollX + childWidth / 2) / childWidth;
                }
                childIndex = Math.max(0, Math.min(childIndex, childCount - 1));//限定childIndex在0到childCount之间
                int sd = (int) Math.ceil(DensityUtils.getWidthPixels(getContext()) / Float.valueOf(childWidth));
                int ft = sd * childWidth - DensityUtils.getWidthPixels(getContext());
                int dx = childIndex * childWidth + -scrollX;
                if (childIndex > 0) {
                    dx += ft;
                }
                scroller.startScroll(scrollX, 0, dx, 0, 500);//up 时自动滚动到
                recycleVelocityTracker();
                invalidate();
                break;
        }
        mLastX = x;
        return isScroller;
    }

}
