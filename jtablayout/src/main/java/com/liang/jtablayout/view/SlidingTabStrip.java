package com.liang.jtablayout.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.liang.jtablayout.Indicator;

public class SlidingTabStrip extends LinearLayout {

    private ValueAnimator mIndicatorAnimator;
    private int mSelectedPosition;
    private float mSelectionOffset;
    private int mIndicatorLeft;
    private int mIndicatorRight;
    private Interpolator interpolator = new FastOutSlowInInterpolator();
    private Indicator indicator;

    public SlidingTabStrip(Context context) {
        super(context);
        setWillNotDraw(false);
    }


    boolean childrenNeedLayout() {
        for (int i = 0, z = getChildCount(); i < z; i++) {
            final View child = getChildAt(i);
            if (child.getWidth() <= 0) {
                return true;
            }
        }
        return false;
    }

    public void setIndicatorPositionFromTabPosition(int position, float positionOffset) {
        if (mIndicatorAnimator != null && mIndicatorAnimator.isRunning()) {
            mIndicatorAnimator.cancel();
        }

        mSelectedPosition = position;
        mSelectionOffset = positionOffset;
        updateIndicatorPosition();
    }

    public float getIndicatorPosition() {
        return mSelectedPosition + mSelectionOffset;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (mIndicatorAnimator != null && mIndicatorAnimator.isRunning()) {
            // If we're currently running an animation, lets cancel it and start a
            // new animation with the remaining duration
            mIndicatorAnimator.cancel();
            final long duration = mIndicatorAnimator.getDuration();
            animateIndicatorToPosition(mSelectedPosition,
                    Math.round((1f - mIndicatorAnimator.getAnimatedFraction()) * duration));
        } else {
            // If we've been layed out, update the indicator position
            updateIndicatorPosition();
        }
    }

    private void updateIndicatorPosition() {
        final View selectedTitle = getChildAt(mSelectedPosition);
        int left, right;

        if (selectedTitle != null && selectedTitle.getWidth() > 0) {
            left = selectedTitle.getLeft();
            right = selectedTitle.getRight();

            if (mSelectionOffset > 0f && mSelectedPosition < getChildCount() - 1) {
                // Draw the selection partway between the tabs
                View nextTitle = getChildAt(mSelectedPosition + 1);
                left = (int) (mSelectionOffset * nextTitle.getLeft() +
                        (1.0f - mSelectionOffset) * left);
                right = (int) (mSelectionOffset * nextTitle.getRight() +
                        (1.0f - mSelectionOffset) * right);
            }
        } else {
            left = right = -1;
        }

        setIndicatorPosition(left, right);
    }

    public void setIndicatorPosition(int left, int right) {
        if (left != mIndicatorLeft || right != mIndicatorRight) {
            // If the indicator's left/right has changed, invalidate
            mIndicatorLeft = left;
            mIndicatorRight = right;
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void animateIndicatorToPosition(final int position, int duration) {
        if (mIndicatorAnimator != null && mIndicatorAnimator.isRunning()) {
            mIndicatorAnimator.cancel();
        }

        final boolean isRtl = ViewCompat.getLayoutDirection(this)
                == ViewCompat.LAYOUT_DIRECTION_RTL;

        final View targetView = getChildAt(position);
        if (targetView == null) {
            // If we don't have a view, just update the position now and return
            updateIndicatorPosition();
            return;
        }

        final int targetLeft = targetView.getLeft();
        final int targetRight = targetView.getRight();
        final int startLeft;
        final int startRight;

        if (Math.abs(position - mSelectedPosition) <= 1) {
            // If the views are adjacent, we'll animate from edge-to-edge
            startLeft = mIndicatorLeft;
            startRight = mIndicatorRight;
        } else {
            // Else, we'll just grow from the nearest edge
            final int offset = Math.round(getResources().getDisplayMetrics().density * 24);
            if (position < mSelectedPosition) {
                // We're going end-to-start
                if (isRtl) {
                    startLeft = startRight = targetLeft - offset;
                } else {
                    startLeft = startRight = targetRight + offset;
                }
            } else {
                // We're going start-to-end
                if (isRtl) {
                    startLeft = startRight = targetRight + offset;
                } else {
                    startLeft = startRight = targetLeft - offset;
                }
            }
        }

        if (startLeft != targetLeft || startRight != targetRight) {
            ValueAnimator animator = mIndicatorAnimator = new ValueAnimator();
            animator.setInterpolator(interpolator);
            animator.setDuration(duration);
            animator.setFloatValues(0, 1);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    final float fraction = animator.getAnimatedFraction();
                    setIndicatorPosition(
                            leap(startLeft, targetLeft, fraction),
                            leap(startRight, targetRight, fraction));
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

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        // Thick colored underline below the current selection
        if (indicator != null && mIndicatorLeft >= 0 && mIndicatorRight > mIndicatorLeft) {
            indicator.draw(canvas, mIndicatorLeft, mIndicatorRight, getHeight());
        }
    }

    public int leap(int startValue, int endValue, float fraction) {
        return startValue + Math.round(fraction * (endValue - startValue));
    }
}
