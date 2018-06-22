package com.liang.jtablayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class JTabLayout extends HorizontalScrollView implements Menu.OnClickListener ,ViewPager.OnPageChangeListener{

    private LinearLayout tabsContainer;

    private int icon_unSelected = 0;
    private int icon_selected = 0;
    private int title_unSelected = 0;
    private int title_selected = 0;

    private int position = 0;

    private int selectedPosition;
    private int newSelectedPosition = 0;

    private boolean isScroller;

    private boolean isChanged = true;

    private ValueAnimator mIndicatorAnimator;
    private Indicator indicator;
    private OnTabListener changeListener;
    private String TAG = "JTabLayout";
    private int lastScrollX = 0;
    private int left = 0;

    public interface OnTabListener {
        void onChanged(int position);

        void onClick(int position);
    }

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
        typedArray.recycle();
    }

    public void addTab(Menu menu) {
        if (title_unSelected != 0 && title_selected != 0) {
            menu.setTitleColor(title_unSelected, title_selected);
        }
        if (icon_unSelected != 0 && icon_selected != 0) {
            menu.setIconColor(icon_unSelected, icon_selected);
        }
        if (menu.isChecked) {
            selectedPosition = position;
        }
        menu.position = position;
        position++;
        menu.isChecked = false;
        menu.create();
        menu.setClickListener(this);
        LinearLayout.LayoutParams params = isScroller ? new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT) :
                new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
        tabsContainer.addView(menu, params);
    }

    public void setCurrentTab(int currentTab) {
        if (isChanged) {
            selectedPosition = currentTab;
            return;
        }
        checkMenu(currentTab);
    }

    private void checkMenu(int selectedPosition) {
        Log.e(TAG, "checkMenu: " + selectedPosition);
        if (tabsContainer.getChildCount() <= 0 || selectedPosition < 0 || selectedPosition >= tabsContainer.getChildCount()) {
            return;
        }
        Menu child = (Menu) tabsContainer.getChildAt(selectedPosition);
        if (child.isChecked) {
            return;
        }
        refreshMenu(child);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.e(TAG, "onLayout: " + changed);
        isChanged = changed;
        if (changed) {
            checkMenu(selectedPosition);
        }
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

    private void animateIndicatorToPosition() {
        if (indicator == null) {
            selectedPosition = newSelectedPosition;
            if (changeListener != null) {
                changeListener.onChanged(selectedPosition);
            }
            return;
        }

        if (mIndicatorAnimator != null && mIndicatorAnimator.isRunning()) {
            mIndicatorAnimator.cancel();
        }

        View tab = tabsContainer.getChildAt(newSelectedPosition);
        if (tab != null) {
            if (indicator.getWidth() <= 0) {
                indicator.setWidth(tab.getWidth());
            }
        }

        mIndicatorAnimator = new ValueAnimator();
        mIndicatorAnimator.setInterpolator(new FastOutSlowInInterpolator());
        mIndicatorAnimator.setDuration(200);
        mIndicatorAnimator.setFloatValues(0, 1);
        mIndicatorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                float xOffset = animator.getAnimatedFraction();
                Log.e(TAG, "addUpdateListener xOffset: " + xOffset);
                reIndicator(xOffset);
            }
        });

        mIndicatorAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                selectedPosition = newSelectedPosition;
                if (changeListener != null) {
                    changeListener.onChanged(selectedPosition);
                }
            }
        });
        mIndicatorAnimator.start();
    }

    private void reIndicator(float xOffset) {

        if (indicator == null) {
            return;
        }
        View tab = tabsContainer.getChildAt(newSelectedPosition);
        if (tab != null) {
            int indicatorWidth = indicator.getWidth();
            Log.e(TAG, "draw indicatorWidth: " + indicatorWidth);
            left = tab.getWidth() / 2 - indicatorWidth / 2;
        }

        for (int i = 0; i < selectedPosition; i++) {
            left += tabsContainer.getChildAt(i).getWidth();
        }

        int offsetX = 0;
        if (newSelectedPosition > selectedPosition) {
            for (int i = selectedPosition; i < newSelectedPosition; i++) {
                offsetX += tabsContainer.getChildAt(i).getWidth();
            }
            left += (offsetX * xOffset);
        } else {
            for (int i = newSelectedPosition; i < selectedPosition; i++) {
                offsetX += tabsContainer.getChildAt(i).getWidth();
            }
            left -= (offsetX * xOffset);
        }
        Log.e(TAG, "draw left: " + left);
        updateIndicatorPosition();
    }

    

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        scrollTab(position, 1);
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void updateIndicatorPosition() {
        ViewCompat.postInvalidateOnAnimation(this);
    }

    private void refreshMenu(Menu menu) {
        if (tabsContainer.getChildCount() > 0) {
            for (int i = 0; i < tabsContainer.getChildCount(); i++) {
                Menu child = (Menu) tabsContainer.getChildAt(i);
                if (child.getVisibility() == GONE) {
                    continue;
                }
                if (menu == child) {
                    child.isChecked = true;
                    newSelectedPosition = child.position;
                } else {
                    if (!child.isChecked) {
                        continue;
                    } else {
                        child.isChecked = false;
                    }
                }
                child.refreshMenu();
            }
            scrollTab(menu.position, 1);
            animateIndicatorToPosition();
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
        if (indicator == null || isInEditMode() || tabsContainer.getChildCount() <= 0) {
            return;
        }
        indicator.draw(canvas, left, getHeight());
        Log.e(TAG, "draw: ...");
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

    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
    }

    public void setChangeListener(OnTabListener listener) {
        changeListener = listener;
    }
}
