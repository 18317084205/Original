package com.liang.jtablayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class JTabLayout extends HorizontalScrollView implements Menu.OnClickListener {

    private LinearLayout tabsContainer;
    private Paint indicatorPaint;

    private int icon_unSelected = 0;
    private int icon_selected = 0;
    private int title_unSelected = 0;
    private int title_selected = 0;

    private int position = 0;

    private int selectedPosition;
    private int checkPosition;

    private boolean isScroller;
    private boolean showIndicator;

    private float indicatorHeight;
    private int indicatorColor;

    private ValueAnimator mIndicatorAnimator;

    private int tabWidth;

    private MenuView.OnTabChangeListener changeListener;
    private String TAG = "JTabLayout";
    private int lastScrollX = 0;
    private float xOffset;

    public JTabLayout(Context context) {
        this(context, null);
    }

    public JTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFillViewport(true);
        setClipChildren(false);

        tabsContainer = new LinearLayout(context);
        indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(tabsContainer, params);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JTabLayout,
                defStyleAttr, 0);
        icon_unSelected = typedArray.getColor(R.styleable.JTabLayout_icon_unSelected, 0);
        icon_selected = typedArray.getColor(R.styleable.JTabLayout_icon_selected, 0);
        title_unSelected = typedArray.getColor(R.styleable.JTabLayout_title_unSelected, 0);
        title_selected = typedArray.getColor(R.styleable.JTabLayout_title_selected, 0);

        selectedPosition = typedArray.getInt(R.styleable.JTabLayout_selectedPosition, 0);
        isScroller = typedArray.getBoolean(R.styleable.JTabLayout_isScroller, false);
        showIndicator = typedArray.getBoolean(R.styleable.JTabLayout_showIndicator, false);
        indicatorColor = typedArray.getColor(R.styleable.JTabLayout_indicatorColor, Color.GRAY);
        indicatorHeight = typedArray.getDimension(R.styleable.JTabLayout_indicatorHeight, 10);
        typedArray.recycle();

        indicatorPaint.setColor(indicatorColor);
        indicatorPaint.setStrokeWidth(indicatorHeight);

        checkMenu(selectedPosition);
    }

    private void checkMenu(int selectedPosition) {
        if (selectedPosition < 0 || selectedPosition >= tabsContainer.getChildCount()) {
            return;
        }
        Menu child = (Menu) tabsContainer.getChildAt(selectedPosition);

        if (child.isChecked) {
            return;
        }

        refreshMenu(child);
    }


    public void addTab(Menu menu) {
        if (title_unSelected != 0 && title_selected != 0) {
            menu.setTitleColor(title_unSelected, title_selected);
        }
        if (icon_unSelected != 0 && icon_selected != 0) {
            menu.setIconColor(icon_unSelected, icon_selected);
        }
        menu.position = position;
        position++;
        menu.create();
        menu.setClickListener(this);
        LinearLayout.LayoutParams params = isScroller ? new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT) :
                new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
        tabsContainer.addView(menu, params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private void animateIndicatorToPosition() {

        if (mIndicatorAnimator != null && mIndicatorAnimator.isRunning()) {
            mIndicatorAnimator.cancel();
        }
        mIndicatorAnimator = new ValueAnimator();
        mIndicatorAnimator.setInterpolator(new FastOutSlowInInterpolator());
        mIndicatorAnimator.setDuration(200);
        mIndicatorAnimator.setFloatValues(0, 1);
        mIndicatorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
//                final float fraction = animator.getAnimatedFraction();
                xOffset = animator.getAnimatedFraction();
                Log.e(TAG, "addUpdateListener xOffset: " + xOffset);
                updateIndicatorPosition();
            }
        });

        mIndicatorAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                selectedPosition = checkPosition;
                xOffset = 0f;
            }
        });
        mIndicatorAnimator.start();
    }

    private void updateIndicatorPosition() {
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void setScroller(boolean isScroller) {
        this.isScroller = isScroller;
    }

    public void setIconColor(int unSelectedColor, int selectedColor) {
        icon_unSelected = unSelectedColor;
        icon_selected = selectedColor;
    }

    public void setTitleColor(int unSelectedColor, int selectedColor) {
        title_unSelected = unSelectedColor;
        title_selected = selectedColor;
    }

    public void setSelectedIndicatorColor(int color) {
        indicatorPaint.setColor(color);
    }

    public void showIndicator(boolean isShow) {
        this.showIndicator = isShow;
    }

    public void setIndicatorHeight(int height) {
        indicatorHeight = height;
        indicatorPaint.setStrokeWidth(height);
    }

    public void setChangeListener(MenuView.OnTabChangeListener listener) {
        changeListener = listener;
    }


    @Override
    public void onClick(Menu menu) {
        if (menu.isChecked) {
            if (changeListener != null) {
                changeListener.onClick(menu.position);
            }
            return;
        }

        refreshMenu(menu);
    }

    private void refreshMenu(Menu menu) {
        if (tabsContainer.getChildCount() > 0) {
            for (int i = 0; i < tabsContainer.getChildCount(); i++) {
                Menu child = (Menu) tabsContainer.getChildAt(i);
                if (child.getVisibility() == GONE) {
                    continue;
                }
                if (menu == child) {
                    child.setChecked(true);
                    checkPosition = child.position;
                } else {
                    if (!child.isChecked) {
                        continue;
                    } else {
                        child.setChecked(false);
                    }
                }
                child.refreshMenu();
            }
            scrollTab(menu.position, 1);
            animateIndicatorToPosition();
        }

        if (changeListener != null) {
            changeListener.onChanged(menu.position);
        }
    }

    private void scrollTab(int position, int offset) {
        if (isScroller && tabsContainer.getChildCount() > 0) {
            int newScrollX;
            View childView = tabsContainer.getChildAt(position);
            newScrollX = childView.getLeft() + offset;
            if (position > 0 || offset > 0) {
                newScrollX -= getWidth() / 2 - childView.getWidth() / 2;
            }
            if (newScrollX != lastScrollX) {
                lastScrollX = newScrollX;
                scrollTo(newScrollX, 0);
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        Log.e(TAG, "draw: ...");
        if (!showIndicator || isInEditMode() || tabsContainer.getChildCount() <= 0) {
            return;
        }

        int height = getHeight();
        View tab = tabsContainer.getChildAt(checkPosition);
        for (int i = 0; i < tabsContainer.getChildCount(); i++) {
            Menu child = (Menu) tabsContainer.getChildAt(i);
            if (child.isChecked) {
                tab = child;
            }
        }
//        if (mIndicatorLeft >= 0 && mIndicatorRight > mIndicatorLeft) {
////            canvas.drawRect(tab.getLeft(), height - indicatorHeight, tab.getRight(), height, indicatorPaint);
//            Log.e(TAG, "mIndicatorLeft: " + mIndicatorLeft);
//            Log.e(TAG, "mIndicatorRight: " + mIndicatorRight);
//            canvas.drawRect(mIndicatorLeft, height - indicatorHeight, mIndicatorLeft, height, indicatorPaint);
//        }
        if (tab != null) {
            tabWidth = tab.getWidth();
            Log.e(TAG, "draw tabWidth: " + tabWidth);
            Log.e(TAG, "draw tabWidth z: " + tab.getMeasuredWidth());
            int indicatorWidth = tabWidth / 2;
            Log.e(TAG, "draw indicatorWidth: " + indicatorWidth);
            int left = 0;
            if (checkPosition > selectedPosition) {
                left = (int) (tabWidth / 4 + selectedPosition * tabWidth + xOffset * tabWidth * (checkPosition - selectedPosition));
            } else {
                left = (int) (tabWidth / 4 + selectedPosition * tabWidth - xOffset * tabWidth * (selectedPosition - checkPosition));
            }

            Log.e(TAG, "draw left: " + left);
            canvas.drawRect(left, height - indicatorHeight, left + indicatorWidth, height, indicatorPaint);
        }

    }

    private class SlidingTabStrip extends LinearLayout {
        private int mSelectedIndicatorHeight;
        private final Paint mSelectedIndicatorPaint;

        int mSelectedPosition = -1;
        float mSelectionOffset;

        private int mLayoutDirection = -1;

        private int mIndicatorLeft = -1;
        private int mIndicatorRight = -1;

        private ValueAnimator mIndicatorAnimator;

        SlidingTabStrip(Context context) {
            super(context);
            setWillNotDraw(false);
            mSelectedIndicatorPaint = new Paint();
        }

        void setSelectedIndicatorColor(int color) {
            if (mSelectedIndicatorPaint.getColor() != color) {
                mSelectedIndicatorPaint.setColor(color);
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        void setSelectedIndicatorHeight(int height) {
            if (mSelectedIndicatorHeight != height) {
                mSelectedIndicatorHeight = height;
                ViewCompat.postInvalidateOnAnimation(this);
            }
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

        void setIndicatorPositionFromTabPosition(int position, float positionOffset) {
            if (mIndicatorAnimator != null && mIndicatorAnimator.isRunning()) {
                mIndicatorAnimator.cancel();
            }

            mSelectedPosition = position;
            mSelectionOffset = positionOffset;
            updateIndicatorPosition();
        }

        float getIndicatorPosition() {
            return mSelectedPosition + mSelectionOffset;
        }

        @Override
        public void onRtlPropertiesChanged(int layoutDirection) {
            super.onRtlPropertiesChanged(layoutDirection);

            // Workaround for a bug before Android M where LinearLayout did not relayout itself when
            // layout direction changed.
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                //noinspection WrongConstant
                if (mLayoutDirection != layoutDirection) {
                    requestLayout();
                    mLayoutDirection = layoutDirection;
                }
            }
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

        void setIndicatorPosition(int left, int right) {
            if (left != mIndicatorLeft || right != mIndicatorRight) {
                // If the indicator's left/right has changed, invalidate
                mIndicatorLeft = left;
                mIndicatorRight = right;
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        int dpToPx(int dps) {
            return Math.round(getResources().getDisplayMetrics().density * dps);
        }

        void animateIndicatorToPosition(final int position, int duration) {
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
                final int offset = dpToPx(24);
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
                animator.setInterpolator(new FastOutSlowInInterpolator());
                animator.setDuration(duration);
                animator.setFloatValues(0, 1);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        final float fraction = animator.getAnimatedFraction();
                        setIndicatorPosition(startLeft + Math.round(fraction * (targetLeft - startLeft)),
                                startLeft + Math.round(fraction * (targetLeft - startLeft)));
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
            if (mIndicatorLeft >= 0 && mIndicatorRight > mIndicatorLeft) {
                canvas.drawRect(mIndicatorLeft, getHeight() - mSelectedIndicatorHeight,
                        mIndicatorRight, getHeight(), mSelectedIndicatorPaint);
            }
        }
    }

}
