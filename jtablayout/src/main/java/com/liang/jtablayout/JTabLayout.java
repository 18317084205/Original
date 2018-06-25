package com.liang.jtablayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class JTabLayout extends HorizontalScrollView implements Menu.OnClickListener, ViewPager.OnPageChangeListener {

    private LinearLayout tabsContainer;

    private int icon_unSelected;
    private int icon_selected;
    private int title_unSelected;
    private int title_selected;

    private float dividerWidth;
    private float dividerHeight;
    private int dividerColor;

    private int position = 0;

    private int selectedPosition;
    private int newSelectedPosition = 0;

    private boolean isScroller;

    private boolean isChanged = true;

    private ViewPager viewPager;

    private ValueAnimator mIndicatorAnimator;
    private Indicator indicator;
    private OnTabListener changeListener;
    private String TAG = "JTabLayout";
    private int lastScrollX = 0;

    private float tabLeft = 0f;
    private float tabRight = 0f;
    private float indicatorLeft = 0f;
    private float indicatorRight = 0f;
    private Paint dividerPaint;

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
        super.setFillViewport(true);
        super.setWillNotDraw(false);
        super.setClipChildren(false);
        tabsContainer = new LinearLayout(context);
        tabsContainer.setClipChildren(false);
        super.addView(tabsContainer, 0, new HorizontalScrollView.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JTabLayout,
                defStyleAttr, 0);
        icon_unSelected = typedArray.getColor(R.styleable.JTabLayout_icon_unSelected, 0);
        icon_selected = typedArray.getColor(R.styleable.JTabLayout_icon_selected, 0);
        title_unSelected = typedArray.getColor(R.styleable.JTabLayout_title_unSelected, 0);
        title_selected = typedArray.getColor(R.styleable.JTabLayout_title_selected, 0);
        selectedPosition = typedArray.getInt(R.styleable.JTabLayout_selectedPosition, 0);
        dividerWidth = typedArray.getDimension(R.styleable.JTabLayout_dividerWidth, 0f);
        dividerHeight = typedArray.getDimension(R.styleable.JTabLayout_dividerHeight, 0f);
        dividerColor = typedArray.getColor(R.styleable.JTabLayout_dividerColor, Color.BLACK);
        isScroller = typedArray.getBoolean(R.styleable.JTabLayout_isScroller, false);
        typedArray.recycle();

        dividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
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
        menu.isChecked = false;
        menu.create();
        menu.setClickListener(this);
        LinearLayout.LayoutParams params = isScroller ? new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT) :
                new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
        params.leftMargin = position > 0 ? (int) dividerWidth : 0;
        tabsContainer.addView(menu, params);
        position++;
    }

    public void setCurrentTab(int currentTab) {
        if (isChanged) {
            selectedPosition = currentTab;
            return;
        }
        checkMenu(currentTab);
    }

    private void checkMenu(int selectedPosition) {
        if (tabsContainer.getChildCount() <= 0 || selectedPosition < 0 || selectedPosition >= tabsContainer.getChildCount()) {
            return;
        }
        Menu child = (Menu) tabsContainer.getChildAt(selectedPosition);
        if (child.isChecked) {
            return;
        }
        refreshMenu(child);
        refreshIndicator(selectedPosition);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            checkMenu(selectedPosition);
            reIndicator(selectedPosition, -1, 0);
        }
        isChanged = changed;
    }

    private void initViewPager() {
        if (viewPager != null) {
            viewPager.addOnPageChangeListener(this);
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
        refreshIndicator(menu.position);
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

        mIndicatorAnimator = new ValueAnimator();
        mIndicatorAnimator.setInterpolator(new FastOutSlowInInterpolator());
        mIndicatorAnimator.setDuration(200);
        mIndicatorAnimator.setFloatValues(0, 1);
        mIndicatorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                float xOffset = animator.getAnimatedFraction();
                reIndicator(selectedPosition, newSelectedPosition, xOffset);
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

    private int getIndicatorWidth(View tabItem) {
        if (indicator == null) {
            return 0;
        }
        int indicatorWidth = indicator.width;
        if (indicatorWidth <= 0) {
            indicatorWidth = (int) (tabItem.getWidth() * indicator.widthScale);
        }
        return indicatorWidth;
    }

    private void reIndicator(int position, int newPosition, float positionOffset) {
        if (indicator == null) {
            return;
        }
        if (newPosition < 0) {
            newPosition = position + 1;
        }
        View currentTab = tabsContainer.getChildAt(position);
        if (currentTab != null && currentTab.getWidth() > 0) {
            indicatorLeft = currentTab.getLeft();
            indicatorRight = currentTab.getRight();
            int indicatorWidth = getIndicatorWidth(currentTab);
            indicatorLeft += (currentTab.getWidth() - indicatorWidth) / 2;
            indicatorRight = indicatorLeft + indicatorWidth;
        }
        if (newPosition < tabsContainer.getChildCount()) {
            View nextTabView = tabsContainer.getChildAt(newPosition);
            int indicatorWidth = getIndicatorWidth(nextTabView);
            float nextLeft = nextTabView.getLeft();
            float nextRight;
            nextLeft += (nextTabView.getWidth() - indicatorWidth) / 2;
            nextRight = nextLeft + indicatorWidth;

            indicatorLeft += ((nextLeft - indicatorLeft) * positionOffset);
            indicatorRight += ((nextRight - indicatorRight) * positionOffset);

            tabLeft = currentTab.getLeft() + (nextTabView.getLeft() - currentTab.getLeft()) * positionOffset;
            tabRight = currentTab.getRight() + (nextTabView.getRight() - currentTab.getRight()) * positionOffset;
        }
        updateIndicatorPosition();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        scrollTab(position, positionOffset);
        reIndicator(position, -1, positionOffset);
    }

    @Override
    public void onPageSelected(int position) {
        selectedPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            checkMenu(selectedPosition);
        }
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
        }
    }

    private void refreshIndicator(int position) {
        if (viewPager != null) {
            if (viewPager.getCurrentItem() != position) {
                viewPager.setCurrentItem(position);
            } else {
                if (changeListener != null) {
                    changeListener.onChanged(selectedPosition);
                }
            }
        } else {
            scrollTab(position, 0);
            animateIndicatorToPosition();
        }
    }

    private void scrollTab(int position, float positionOffset) {
        if (isScroller && tabsContainer.getChildCount() > 0) {
            View childView = tabsContainer.getChildAt(position);
            int offset = (int) (positionOffset * (childView.getWidth() + dividerWidth));
            int newScrollX = childView.getLeft() + offset;
            if (position > 0 || offset > 0) {
                newScrollX -= getWidth() / 2;
                newScrollX += (tabRight - tabLeft) / 2;
//                newScrollX += dividerWidth / 2;
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
        if (isInEditMode() || tabsContainer.getChildCount() <= 0) {
            return;
        }

        if (dividerWidth > 0) {
            dividerPaint.setStrokeWidth(dividerWidth);
            dividerPaint.setColor(dividerColor);
            Log.e(TAG, "draw dividerWidth: ..." + dividerWidth);
            for (int i = 0; i < tabsContainer.getChildCount() - 1; i++) {
                View tab = tabsContainer.getChildAt(i);
                canvas.drawLine(tab.getRight() + dividerWidth / 2, (getHeight() - dividerHeight) / 2, tab.getRight() + dividerWidth / 2, (getHeight() - dividerHeight) / 2 + dividerHeight, dividerPaint);
            }
        }

        if (indicator != null) {
            indicator.draw(canvas, indicatorLeft, indicatorRight, getHeight());
        }

        Log.e(TAG, "draw: ...");
    }

    public void setScroller(boolean isScroller) {
        this.isScroller = isScroller;
    }

    public void setIconColor(int unSelectedColor, int selectedColor) {
        icon_unSelected = unSelectedColor;
        icon_selected = selectedColor;
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        initViewPager();
    }

    public void setTitleColor(int unSelectedColor, int selectedColor) {
        title_unSelected = unSelectedColor;
        title_selected = selectedColor;
    }

    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
    }

    public void setDividerWidth(int dividerWidth) {
        this.dividerWidth = dividerWidth;
    }

    public void setDividerHeight(int dividerHeight) {
        this.dividerHeight = dividerHeight;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
    }

    public void setChangeListener(OnTabListener listener) {
        changeListener = listener;
    }
}
