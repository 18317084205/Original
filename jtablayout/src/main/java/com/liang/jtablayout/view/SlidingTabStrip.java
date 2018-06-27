package com.liang.jtablayout.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.widget.LinearLayout;

import com.liang.jtablayout.Indicator;

public class SlidingTabStrip extends LinearLayout {

    private ValueAnimator mIndicatorAnimator;
    private int mSelectedPosition;
    private int mIndicatorLeft;
    private int mIndicatorRight;
    private TimeInterpolator interpolator = new FastOutSlowInInterpolator();

    private Indicator indicator;

    public SlidingTabStrip(Context context) {
        super(context);
        setWillNotDraw(false);
    }


    public boolean childrenNeedLayout() {
        for (int i = 0, z = getChildCount(); i < z; i++) {
            final View child = getChildAt(i);
            if (child.getWidth() <= 0) {
                return true;
            }
        }
        return false;
    }

    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
    }

    public void setIndicatorPositionFromTabPosition(int position, int newPosition, float positionOffset) {
        if (indicator == null) {
            return;
        }
        if (mIndicatorAnimator != null && mIndicatorAnimator.isRunning()) {
            mIndicatorAnimator.cancel();
        }
        mSelectedPosition = position;
        updateIndicatorPosition(position, newPosition, positionOffset);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (indicator == null || !changed) {
            return;
        }
        if (mIndicatorAnimator != null && mIndicatorAnimator.isRunning()) {
            // If we're currently running an animation, lets cancel it and start a
            // new animation with the remaining duration
            mIndicatorAnimator.cancel();
            final long duration = mIndicatorAnimator.getDuration();
            animateIndicatorToPosition(mSelectedPosition,
                    Math.round((1f - mIndicatorAnimator.getAnimatedFraction()) * duration));
        } else {
            // If we've been layed out, update the indicator position
            updateIndicatorPosition(mSelectedPosition, -1, 0f);
        }
    }

    private void updateIndicatorPosition(int position, int newPosition, float positionOffset) {
        if (newPosition < 0) {
            newPosition = position + 1;
        }

        View currentTab = getChildAt(position);
        mIndicatorLeft = currentTab.getLeft();
        mIndicatorRight = currentTab.getRight();

        if (currentTab != null && currentTab.getWidth() > 0) {
            int indicatorWidth = getIndicatorWidth(currentTab);
            mIndicatorLeft += (currentTab.getWidth() - indicatorWidth) / 2;
            mIndicatorRight = mIndicatorLeft + indicatorWidth;
        }

        if (newPosition < getChildCount() && newPosition != position) {
            View nextTabView = getChildAt(newPosition);
            int indicatorWidth = getIndicatorWidth(nextTabView);
            int nextLeft = nextTabView.getLeft();
            int nextRight;
            nextLeft += (nextTabView.getWidth() - indicatorWidth) / 2;
            nextRight = nextLeft + indicatorWidth;

            mIndicatorLeft += ((nextLeft - mIndicatorLeft) * positionOffset);
            mIndicatorRight += ((nextRight - mIndicatorRight) * positionOffset);
        }

        setIndicatorPosition();
    }

    private int getIndicatorWidth(View currentTab) {
        if (indicator == null) {
            return 0;
        }
        int indicatorWidth = indicator.getWidth();
        if (indicatorWidth <= 0) {
            indicatorWidth = (int) (currentTab.getWidth() * indicator.getWidthScale());
        }
        return indicatorWidth;
    }

    public void setIndicatorPosition() {
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void animateIndicatorToPosition(final int position, int duration) {
        if (indicator == null) {
            return;
        }
        if (position != mSelectedPosition) {
            if (mIndicatorAnimator != null && mIndicatorAnimator.isRunning()) {
                mIndicatorAnimator.cancel();
            }
            ValueAnimator animator = mIndicatorAnimator = new ValueAnimator();
            animator.setInterpolator(interpolator);
            animator.setDuration(duration);
            animator.setFloatValues(0, 1);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    float fraction = animator.getAnimatedFraction();
                    updateIndicatorPosition(mSelectedPosition, position, fraction);
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    mSelectedPosition = position;
                }
            });
            animator.start();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (isInEditMode() || getChildCount() <= 0) {
            return;
        }
        // Thick colored underline below the current selection
        if (indicator != null && mIndicatorLeft >= 0 && mIndicatorRight > mIndicatorLeft) {
            indicator.draw(canvas, mIndicatorLeft, mIndicatorRight, getHeight());
        }
    }
}
