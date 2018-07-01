package com.liang.jtablayout.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.liang.jtablayout.Indicator;

public class SlidingTabStrip extends LinearLayout {

    private ValueAnimator mIndicatorAnimator;
    private int mSelectedPosition;
    private float mIndicatorLeft;
    private float mIndicatorRight;
    private TimeInterpolator interpolator = new FastOutSlowInInterpolator();

    private Indicator indicator;
    private float mSelectionOffset;

    public SlidingTabStrip(Context context) {
        super(context);
        setWillNotDraw(false);
        setClipChildren(false);
        setClipToPadding(false);
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

    public void setIndicatorPositionFromTabPosition(int position, float positionOffset) {
        if (indicator == null) {
            return;
        }
        if (mIndicatorAnimator != null && mIndicatorAnimator.isRunning()) {
            mIndicatorAnimator.cancel();
        }

        mSelectedPosition = position;
        mSelectionOffset = positionOffset;
        updateIndicatorPosition(-1);
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
            updateIndicatorPosition(-1);
        }
    }

    private void updateIndicatorPosition(int position) {

        if (position < 0) {
            position = mSelectedPosition + 1;
        }

        float left = -1, right = -1;
        View currentTab = getChildAt(mSelectedPosition);
        if (currentTab != null && currentTab.getWidth() > 0) {
            left = currentTab.getLeft();
            right = currentTab.getRight();
            int indicatorWidth = getIndicatorWidth(currentTab);
            left += (currentTab.getWidth() - indicatorWidth) / 2;
            right = left + indicatorWidth;
        }
        if (position < getChildCount()) {
            View nextTabView = getChildAt(position);
            int indicatorWidth = getIndicatorWidth(nextTabView);
            float nextLeft = nextTabView.getLeft();
            float nextRight;
            nextLeft += (nextTabView.getWidth() - indicatorWidth) / 2;
            nextRight = nextLeft + indicatorWidth;

            if (indicator.isTransitionScroll()) {
                float offR = mSelectionOffset * 2 - 1;
                float offL = mSelectionOffset * 2;

                if (position < mSelectedPosition && mSelectionOffset > 0) {

                    if (offR < 0) {
                        offR = 0;
                    }
                    if (1 - offL < 0) {
                        offL = 1;
                    }
                } else {
                    offL = mSelectionOffset * 2 - 1;
                    offR = mSelectionOffset * 2;
                    if (offL < 0) {
                        offL = 0;
                    }
                    if (1 - offR < 0) {
                        offR = 1;
                    }
                }
                left += ((nextLeft - left) * offL);
                right += ((nextRight - right) * offR);
            }else {
                left += ((nextLeft - left) * mSelectionOffset);
                right += ((nextRight - right) * mSelectionOffset);
            }

        }

        setIndicatorPosition(left, right);
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
                    mSelectionOffset = animator.getAnimatedFraction();
                    updateIndicatorPosition(position);
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    mSelectedPosition = position;
                    mSelectionOffset = 0f;
                }
            });
            animator.start();
        }
    }

    private void setIndicatorPosition(float left, float right) {
        if (left != mIndicatorLeft || right != mIndicatorRight) {
            // If the indicator's left/right has changed, invalidate
            mIndicatorLeft = left;
            mIndicatorRight = right;
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (isInEditMode() || getChildCount() <= 0) {
            super.draw(canvas);
            return;
        }
        // Thick colored underline below the current selection
        if (indicator != null && mIndicatorLeft >= 0 && mIndicatorRight > mIndicatorLeft) {
            if (indicator.isForeground()) {
                super.draw(canvas);
                indicator.draw(canvas, mIndicatorLeft, mIndicatorRight, getHeight());
            } else {
                indicator.draw(canvas, mIndicatorLeft, mIndicatorRight, getHeight());
                super.draw(canvas);
            }

        }

    }
}
