package com.liang.jtablayout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pools;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.liang.jtablayout.listener.OnTabSelectedListener;
import com.liang.jtablayout.view.JTabView;
import com.liang.jtablayout.view.SlidingTabStrip;
import com.liang.jtablayout.view.TabView;

import java.util.ArrayList;
import java.util.Iterator;

import static android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING;
import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;
import static android.support.v4.view.ViewPager.SCROLL_STATE_SETTLING;

public class TabLayout extends HorizontalScrollView implements View.OnClickListener, ViewPager.OnPageChangeListener, ViewPager.OnAdapterChangeListener {

    private Pools.Pool<TabView> tabViewPool = new Pools.SynchronizedPool<>(12);
    private ArrayList<TabView> tabViews = new ArrayList<>();
    private ArrayList<OnTabSelectedListener> tabSelectedListeners = new ArrayList<>();
    public static final int MODE_SCROLLABLE = 0;
    public static final int MODE_FIXED = 1;

    private static final int ANIMATION_DURATION = 300;

    private ValueAnimator scrollAnimator;

    private SlidingTabStrip slidingTabStrip;

    private int mode = MODE_FIXED;
    private TabView selectedTab;
    private ColorStateList tabTextColors;
    private ColorStateList tabIconColors;

    private int tabPaddingStart = 10;
    private int tabPaddingTop = 0;
    private int tabPaddingEnd = 10;
    private int tabPaddingBottom = 0;
    private ViewPager viewPager;
    private PagerAdapter mPagerAdapter;
    private ViewPager.OnAdapterChangeListener mAdapterChangeListener;

    private int previousScrollState;
    private int scrollState;
    private String TAG = "TabLayout";

    private int dividerWidth;
    private int dividerHeight;
    private int dividerColor;
    private Paint dividerPaint;

    public TabLayout(Context context) {
        this(context, null);
    }

    public TabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFillViewport(true);
        setHorizontalScrollBarEnabled(false);
        dividerPaint = new Paint();
        slidingTabStrip = new SlidingTabStrip(context);
        addView(slidingTabStrip, 0, new HorizontalScrollView.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JTabLayout,
                defStyleAttr, 0);
        if (typedArray.hasValue(android.support.design.R.styleable.TabLayout_tabTextColor)) {
            // If we have an explicit text color set, use it instead
            tabTextColors = typedArray.getColorStateList(android.support.design.R.styleable.TabLayout_tabTextColor);
        }
        dividerWidth = typedArray.getDimensionPixelSize(R.styleable.JTabLayout_dividerWidth, 0);
        dividerHeight = typedArray.getDimensionPixelSize(R.styleable.JTabLayout_dividerHeight, 50);
        dividerColor = typedArray.getColor(R.styleable.JTabLayout_dividerColor, Color.BLACK);
        typedArray.recycle();

        updateTabViews(true);
    }


    public void setMode(int mode) {
        this.mode = mode;
        updateTabViews(true);
    }

    public void setTabPadding(int mTabPaddingStart, int mTabPaddingTop, int mTabPaddingEnd, int mTabPaddingBottom) {
        this.tabPaddingStart = mTabPaddingStart;
        this.tabPaddingTop = mTabPaddingTop;
        this.tabPaddingEnd = mTabPaddingEnd;
        this.tabPaddingBottom = mTabPaddingBottom;
        updateTabViews(true);
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

    public void updateTabViews(boolean requestLayout) {
        switch (mode) {
            case MODE_FIXED:
                slidingTabStrip.setGravity(Gravity.CENTER);
                break;
            case MODE_SCROLLABLE:
                slidingTabStrip.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                break;
        }
        for (int i = 0; i < slidingTabStrip.getChildCount(); i++) {
            View child = slidingTabStrip.getChildAt(i);
            child.setPadding(tabPaddingStart, tabPaddingTop, tabPaddingEnd, tabPaddingBottom);
            updateTabViewLayoutParams((LinearLayout.LayoutParams) child.getLayoutParams(), i == 0 ? 0 : dividerWidth);
            if (requestLayout) {
                child.requestLayout();
            }
        }
    }

    public void setIndicator(Indicator indicator) {
        slidingTabStrip.setIndicator(indicator);
    }

    private void updateTabViewLayoutParams(LinearLayout.LayoutParams lp, int dividerWidth) {
        lp.leftMargin = dividerWidth;
        if (mode == MODE_FIXED) {
            lp.width = 0;
            lp.weight = 1.0f;
        } else {
            lp.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            lp.weight = 0;
        }
    }

    public void addTab(@NonNull TabView tab) {
        addTab(tab, tabViews.isEmpty());
    }

    public void addTab(@NonNull TabView tab, int position) {
        addTab(tab, position, tabViews.isEmpty());
    }

    public void addTab(@NonNull TabView tab, boolean setSelected) {
        addTab(tab, tabViews.size(), setSelected);
    }

    public void addTab(@NonNull TabView tab, int position, boolean setSelected) {
        tab.setPadding(tabPaddingStart, tabPaddingTop, tabPaddingEnd, tabPaddingBottom);
        if (tab.getTitleColor() == null) {
            tab.setTitleColor(tabTextColors);
        }
        if (tab.getIcons() == null) {
            tab.setIconColor(tabIconColors);
        }
        configureTab(tab, position);
        addTabView(tab);
        if (setSelected) {
            selectTab(tab);
        }
        tab.setOnClickListener(this);
    }

    private void addTabView(TabView tab) {
        slidingTabStrip.addView(tab, tab.getPosition(), createLayoutParamsForTabs(tab.getPosition()));
    }

    private void configureTab(TabView tab, int position) {
        tab.setPosition(position);
        tabViews.add(position, tab);

        int count = tabViews.size();
        for (int i = position + 1; i < count; i++) {
            tabViews.get(i).setPosition(i);
        }
    }

    private LinearLayout.LayoutParams createLayoutParamsForTabs(int position) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        updateTabViewLayoutParams(lp, position == 0 ? 0 : dividerWidth);
        return lp;
    }


    public void addOnTabSelectedListener(@NonNull OnTabSelectedListener listener) {
        if (!tabSelectedListeners.contains(listener)) {
            tabSelectedListeners.add(listener);
        }
    }

    public void removeOnTabSelectedListener(@NonNull OnTabSelectedListener listener) {
        tabSelectedListeners.remove(listener);
    }

    public void clearOnTabSelectedListeners() {
        tabSelectedListeners.clear();
    }

    @NonNull
    public TabView newTab() {
        JTabView tab = (JTabView) tabViewPool.acquire();
        if (tab == null) {
            tab = new JTabView(getContext());
        }
        return tab;
    }

    public int getTabCount() {
        return tabViews.size();
    }

    @Nullable
    public TabView getTabAt(int index) {
        return (index < 0 || index >= getTabCount()) ? null : tabViews.get(index);
    }

    public void removeTab(TabView tab) {
        removeTabAt(tab.getPosition());
    }

    public void removeTabAt(int position) {
        int selectedTabPosition = selectedTab != null ? selectedTab.getPosition() : 0;
        removeTabViewAt(position);

        TabView removedTab = tabViews.remove(position);
        if (removedTab != null) {
            removedTab.reset();
            tabViewPool.release(removedTab);
        }

        int newTabCount = tabViews.size();
        for (int i = position; i < newTabCount; i++) {
            tabViews.get(i).setPosition(i);
        }

        if (selectedTabPosition == position) {
            selectTab(tabViews.isEmpty() ? null : tabViews.get(Math.max(0, position - 1)));
        }
    }

    private void removeTabViewAt(int position) {
        final TabView view = (TabView) slidingTabStrip.getChildAt(position);
        slidingTabStrip.removeViewAt(position);
        if (view != null) {
            view.reset();
            tabViewPool.release(view);
        }
        requestLayout();
    }

    public void removeAllTabs() {
        // Remove all the views
        for (int i = slidingTabStrip.getChildCount() - 1; i >= 0; i--) {
            removeTabViewAt(i);
        }

        for (Iterator<TabView> i = tabViews.iterator(); i.hasNext(); ) {
            TabView tab = i.next();
            i.remove();
            tab.reset();
            tabViewPool.release(tab);
        }
        selectedTab = null;
    }

    public void selectTab(TabView tab) {
        selectTab(tab, true);
    }

    private void selectTab(TabView tab, boolean updateIndicator) {
        final TabView currentTab = selectedTab;

        if (currentTab == tab) {
            if (currentTab != null) {
                dispatchTabReselected(tab);
                animateToTab(tab.getPosition());
            }
        } else {
            final int newPosition = tab != null ? tab.getPosition() : TabView.INVALID_POSITION;
            if (updateIndicator) {
                if ((currentTab == null || currentTab.getPosition() == TabView.INVALID_POSITION)
                        && newPosition != TabView.INVALID_POSITION) {
                    // If we don't currently have a tab, just draw the interpolator
                    setScrollPosition(newPosition, 0f, true);
                } else {
                    animateToTab(newPosition);
                }
                if (newPosition != TabView.INVALID_POSITION) {
                    setSelectedTabView(newPosition);
                }
            }
            if (viewPager != null) {
                viewPager.setCurrentItem(tab.getPosition(), false);
            } else {
                selectedTab = tab;
                dispatchTabUnselected(currentTab);
                dispatchTabSelected(tab);
            }
        }
    }

    private void setSelectedTabView(int position) {
        final int tabCount = slidingTabStrip.getChildCount();
        if (position < tabCount) {
            for (int i = 0; i < tabCount; i++) {
                final View child = slidingTabStrip.getChildAt(i);
                child.setSelected(i == position);
            }
        }
    }

    private void setScrollPosition(int position, float positionOffset, boolean updateSelectedText) {
        setScrollPosition(position, positionOffset, updateSelectedText, true);
    }

    public void setScrollPosition(int position, float positionOffset, boolean updateSelectedText,
                                  boolean updateIndicatorPosition) {
        final int roundedPosition = Math.round(position + positionOffset);
        if (roundedPosition < 0 || roundedPosition >= slidingTabStrip.getChildCount()) {
            return;
        }

        // Set the indicator position, if enabled
        if (updateIndicatorPosition) {
            slidingTabStrip.setIndicatorPositionFromTabPosition(position, positionOffset);
        }

        // Now update the scroll position, canceling any running animation
        if (scrollAnimator != null && scrollAnimator.isRunning()) {
            scrollAnimator.cancel();
        }
        scrollTo(calculateScrollXForTab(position, positionOffset), 0);

        // Update the 'selected state' view as we scroll, if enabled
        if (updateSelectedText) {
            setSelectedTabView(roundedPosition);
        }
    }

    public int getSelectedTabPosition() {
        return selectedTab != null ? selectedTab.getPosition() : -1;
    }

    private void animateToTab(int newPosition) {
        if (newPosition == TabView.INVALID_POSITION) {
            return;
        }

        if (getWindowToken() == null || !ViewCompat.isLaidOut(this)
                || slidingTabStrip.childrenNeedLayout()) {
            // If we don't have a window token, or we haven't been laid out yet just draw the new
            // position now
            setScrollPosition(newPosition, 0f, true);
            return;
        }

        final int startScrollX = getScrollX();
        final int targetScrollX = calculateScrollXForTab(newPosition, 0);

        if (startScrollX != targetScrollX) {
            ensureScrollAnimator();
            scrollAnimator.setIntValues(startScrollX, targetScrollX);
            scrollAnimator.start();
        }

        // Now animate the interpolator
        slidingTabStrip.animateIndicatorToPosition(newPosition, ANIMATION_DURATION);
    }

    private void ensureScrollAnimator() {
        if (scrollAnimator == null) {
            scrollAnimator = new ValueAnimator();
            scrollAnimator.setInterpolator(new FastOutSlowInInterpolator());
            scrollAnimator.setDuration(ANIMATION_DURATION);
            scrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    scrollTo((int) animator.getAnimatedValue(), 0);
                }
            });
        }
    }

    public void dispatchTabSelected(@NonNull TabView tab) {
        if (tab == null) {
            return;
        }
        for (int i = tabSelectedListeners.size() - 1; i >= 0; i--) {
            tabSelectedListeners.get(i).onTabSelected(tab.getPosition());
        }
    }

    public void dispatchTabUnselected(@NonNull TabView tab) {
        if (tab == null) {
            return;
        }
        for (int i = tabSelectedListeners.size() - 1; i >= 0; i--) {
            tabSelectedListeners.get(i).onTabUnselected(tab.getPosition());
        }
    }

    private void dispatchTabReselected(@NonNull TabView tab) {
        if (tab == null) {
            return;
        }
        for (int i = tabSelectedListeners.size() - 1; i >= 0; i--) {
            tabSelectedListeners.get(i).onTabReselected(tab.getPosition());
        }
    }


    private int calculateScrollXForTab(int position, float positionOffset) {
        if (mode == MODE_SCROLLABLE) {
            final View selectedChild = slidingTabStrip.getChildAt(position);
            final View nextChild = position + 1 < slidingTabStrip.getChildCount()
                    ? slidingTabStrip.getChildAt(position + 1)
                    : null;
            final int selectedWidth = selectedChild != null ? selectedChild.getWidth() : 0;
            final int nextWidth = nextChild != null ? nextChild.getWidth() : 0;

            // base scroll amount: places center of tab in center of parent
            int scrollBase = selectedChild.getLeft() + (selectedWidth / 2) - (getWidth() / 2);
            // offset amount: fraction of the distance between centers of tabs
            int scrollOffset = (int) ((selectedWidth + nextWidth + dividerWidth * 2) * 0.5f * positionOffset);

            return (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_LTR)
                    ? scrollBase + scrollOffset
                    : scrollBase - scrollOffset;
        }
        return 0;
    }

    public void setTabTextColors(int normalColor, int selectedColor) {
        setTabTextColors(createColorStateList(normalColor, selectedColor));
    }


    public void setTabTextColors(@Nullable ColorStateList textColor) {
        if (tabTextColors != textColor) {
            tabTextColors = textColor;
            updateAllTabs();
        }
    }

    public void setTabIconColors(int normalColor, int selectedColor) {
        setTabIconColors(createColorStateList(normalColor, selectedColor));
    }

    public void setTabIconColors(@Nullable ColorStateList iconColor) {
        if (tabIconColors != iconColor) {
            tabIconColors = iconColor;
            updateAllTabs();
        }
    }

    private ColorStateList createColorStateList(int defaultColor, int selectedColor) {
        final int[][] states = new int[2][];
        final int[] colors = new int[2];
        int i = 0;
        states[i] = SELECTED_STATE_SET;
        colors[i] = selectedColor;
        i++;

        // Default enabled state
        states[i] = EMPTY_STATE_SET;
        colors[i] = defaultColor;
        i++;
        return new ColorStateList(states, colors);
    }

    private void updateAllTabs() {
        for (int i = 0, z = tabViews.size(); i < z; i++) {
            TabView tabView = tabViews.get(i);
            if (tabView.getTitleColor() == null) {
                tabView.setTitleColor(tabTextColors);
            }
            if (tabView.getIcons() == null) {
                tabView.setIconColor(tabIconColors);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v instanceof TabView) {
            selectTab((TabView) v);
        }
    }

    public void setupWithViewPager(@Nullable ViewPager viewPager) {
        if (this.viewPager != null) {
            // If we've already been setup with a ViewPager, remove us from it
            this.viewPager.removeOnPageChangeListener(this);
//            if (mAdapterChangeListener != null) {
//                mViewPager.removeOnAdapterChangeListener(mAdapterChangeListener);
//            }
        }
        this.viewPager = viewPager;
        if (this.viewPager != null) {
            mPagerAdapter = viewPager.getAdapter();
            this.viewPager.addOnPageChangeListener(this);
            this.viewPager.addOnAdapterChangeListener(this);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.e(TAG, "onPageScrolled: ..." + positionOffset + ", ..." + scrollState);
        if (scrollState == SCROLL_STATE_IDLE) {
            return;
        }
        setScrollPosition(position, positionOffset, true, true);
    }

    @Override
    public void onPageSelected(int position) {
        Log.e(TAG, "onPageSelected: ..." + position);
        if (getSelectedTabPosition() != position && position < getTabCount()) {
            dispatchTabUnselected(selectedTab);
            selectedTab = getTabAt(position);
            dispatchTabSelected(selectedTab);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.e(TAG, "onPageScrollStateChanged: ..." + state);
        previousScrollState = scrollState;
        scrollState = state;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() == 1) {
            final View child = getChildAt(0);
            boolean remeasure = false;
            switch (mode) {
                case MODE_SCROLLABLE:
                    remeasure = child.getMeasuredWidth() < getMeasuredWidth();
                    break;
                case MODE_FIXED:
                    remeasure = child.getMeasuredWidth() != getMeasuredWidth();
                    break;
            }

            if (remeasure) {
                int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                        getMeasuredWidth(), MeasureSpec.EXACTLY);
                child.measure(childWidthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isInEditMode() || slidingTabStrip.getChildCount() <= 0) {
            return;
        }

        if (dividerWidth > 0) {
            dividerPaint.setStrokeWidth(dividerWidth);
            dividerPaint.setColor(dividerColor);
            Log.e(TAG, "draw dividerWidth: ..." + dividerWidth);
            for (int i = 0; i < slidingTabStrip.getChildCount() - 1; i++) {
                View tab = slidingTabStrip.getChildAt(i);
                canvas.drawLine(tab.getRight() + dividerWidth / 2, (getHeight() - dividerHeight) / 2, tab.getRight() + dividerWidth / 2, (getHeight() - dividerHeight) / 2 + dividerHeight, dividerPaint);
            }
        }

        super.onDraw(canvas);
    }

    @Override
    public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {
        Log.e(TAG, "onAdapterChanged: ..." + viewPager.getCurrentItem());
    }
}
