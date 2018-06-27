package com.liang.jtablayout;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pools;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.liang.jtablayout.listener.TabPageChangeListener;
import com.liang.jtablayout.view.JTabView;
import com.liang.jtablayout.view.SlidingTabStrip;
import com.liang.jtablayout.view.TabView;

import java.util.ArrayList;
import java.util.Iterator;

public class TabLayout extends HorizontalScrollView implements View.OnClickListener {

    private Pools.Pool<TabView> mTabViewPool = new Pools.SynchronizedPool<>(12);
    private ArrayList<TabView> mTabs = new ArrayList<>();
    private ArrayList<OnTabSelectedListener> mSelectedListeners = new ArrayList<>();
    public static final int MODE_SCROLLABLE = 0;
    public static final int MODE_FIXED = 1;

    private static final int ANIMATION_DURATION = 300;

    private OnTabSelectedListener mSelectedListener;
    private ValueAnimator mScrollAnimator;

    private SlidingTabStrip mTabStrip;

    private int mMode = MODE_SCROLLABLE;
    private TabView mSelectedTab;
    private ColorStateList mTabTextColors;
    private ColorStateList mTabIconColors;

    private int mTabPaddingStart = 10;
    private int mTabPaddingTop = 0;
    private int mTabPaddingEnd = 10;
    private int mTabPaddingBottom = 0;
    private ViewPager viewPager;
    private TabPageChangeListener tabPageChangeListener;

    public interface OnTabSelectedListener {

        public void onTabSelected(int tab);

        public void onTabUnselected(int tab);

        public void onTabReselected(int tab);
    }

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
        mTabStrip = new SlidingTabStrip(context);
        addView(mTabStrip, new HorizontalScrollView.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        updateTabViews(true);
    }

    public void setTabPadding(int mTabPaddingStart, int mTabPaddingTop, int mTabPaddingEnd, int mTabPaddingBottom) {
        this.mTabPaddingStart = mTabPaddingStart;
        this.mTabPaddingTop = mTabPaddingTop;
        this.mTabPaddingEnd = mTabPaddingEnd;
        this.mTabPaddingBottom = mTabPaddingBottom;
        updateTabViews(true);
    }

    public void updateTabViews(boolean requestLayout) {
        for (int i = 0; i < mTabStrip.getChildCount(); i++) {
            View child = mTabStrip.getChildAt(i);
            child.setPadding(mTabPaddingStart, mTabPaddingTop, mTabPaddingEnd, mTabPaddingBottom);
            updateTabViewLayoutParams((LinearLayout.LayoutParams) child.getLayoutParams());
            if (requestLayout) {
                child.requestLayout();
            }
        }
    }

    public void setIndicator(Indicator indicator) {
        mTabStrip.setIndicator(indicator);
    }

    private void updateTabViewLayoutParams(LinearLayout.LayoutParams lp) {
        if (mMode == MODE_FIXED) {
            lp.width = 0;
            lp.weight = 1;
        } else {
            lp.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            lp.weight = 0;
        }
    }

    public void addTab(@NonNull TabView tab) {
        addTab(tab, mTabs.isEmpty());
    }

    public void addTab(@NonNull TabView tab, int position) {
        addTab(tab, position, mTabs.isEmpty());
    }

    public void addTab(@NonNull TabView tab, boolean setSelected) {
        addTab(tab, mTabs.size(), setSelected);
    }

    public void addTab(@NonNull TabView tab, int position, boolean setSelected) {
        tab.setPadding(mTabPaddingStart, mTabPaddingTop, mTabPaddingEnd, mTabPaddingBottom);
        if (tab.getTitleColor() == null) {
            tab.setTitleColor(mTabTextColors);
        }
        if (tab.getIcons() == null) {
            tab.setIconColor(mTabIconColors);
        }
        configureTab(tab, position);
        addTabView(tab);
        if (setSelected) {
            selectTab(tab);
        }
        tab.setOnClickListener(this);
    }

    private void addTabView(TabView tab) {
        mTabStrip.addView(tab, tab.getPosition(), createLayoutParamsForTabs());
    }

    private void configureTab(TabView tab, int position) {
        tab.setPosition(position);
        mTabs.add(position, tab);

        int count = mTabs.size();
        for (int i = position + 1; i < count; i++) {
            mTabs.get(i).setPosition(i);
        }
    }

    private LinearLayout.LayoutParams createLayoutParamsForTabs() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        updateTabViewLayoutParams(lp);
        return lp;
    }


    public void addOnTabSelectedListener(@NonNull OnTabSelectedListener listener) {
        if (!mSelectedListeners.contains(listener)) {
            mSelectedListeners.add(listener);
        }
    }

    public void removeOnTabSelectedListener(@NonNull OnTabSelectedListener listener) {
        mSelectedListeners.remove(listener);
    }

    public void clearOnTabSelectedListeners() {
        mSelectedListeners.clear();
    }

    @NonNull
    public TabView newTab() {
        JTabView tab = (JTabView) mTabViewPool.acquire();
        if (tab == null) {
            tab = new JTabView(getContext());
        }
        return tab;
    }

    public int getTabCount() {
        return mTabs.size();
    }

    @Nullable
    public TabView getTabAt(int index) {
        return (index < 0 || index >= getTabCount()) ? null : mTabs.get(index);
    }

    public void removeTab(TabView tab) {
        removeTabAt(tab.getPosition());
    }

    public void removeTabAt(int position) {
        int selectedTabPosition = mSelectedTab != null ? mSelectedTab.getPosition() : 0;
        removeTabViewAt(position);

        TabView removedTab = mTabs.remove(position);
        if (removedTab != null) {
            removedTab.reset();
            mTabViewPool.release(removedTab);
        }

        int newTabCount = mTabs.size();
        for (int i = position; i < newTabCount; i++) {
            mTabs.get(i).setPosition(i);
        }

        if (selectedTabPosition == position) {
            selectTab(mTabs.isEmpty() ? null : mTabs.get(Math.max(0, position - 1)));
        }
    }

    private void removeTabViewAt(int position) {
        final TabView view = (TabView) mTabStrip.getChildAt(position);
        mTabStrip.removeViewAt(position);
        if (view != null) {
            view.reset();
            mTabViewPool.release(view);
        }
        requestLayout();
    }

    public void removeAllTabs() {
        // Remove all the views
        for (int i = mTabStrip.getChildCount() - 1; i >= 0; i--) {
            removeTabViewAt(i);
        }

        for (Iterator<TabView> i = mTabs.iterator(); i.hasNext(); ) {
            TabView tab = i.next();
            i.remove();
            tab.reset();
            mTabViewPool.release(tab);
        }
        mSelectedTab = null;
    }

    public void selectTab(TabView tab) {
        selectTab(tab, true);
    }

    private void selectTab(TabView tab, boolean updateIndicator) {
        final TabView currentTab = mSelectedTab;

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
                    setScrollPosition(newPosition, newPosition, 0f, true);
                } else {
                    animateToTab(newPosition);
                }
                if (newPosition != TabView.INVALID_POSITION) {
                    setSelectedTabView(newPosition);
                }
            }
            if (currentTab != null) {
                dispatchTabUnselected(currentTab);
            }
            mSelectedTab = tab;
            if (tab != null) {
                dispatchTabSelected(tab);
            }
        }
    }

    private void setSelectedTabView(int position) {
        final int tabCount = mTabStrip.getChildCount();
        if (position < tabCount) {
            for (int i = 0; i < tabCount; i++) {
                final View child = mTabStrip.getChildAt(i);
                child.setSelected(i == position);
            }
        }
    }

    public void setScrollPosition(int position, int newPosition, float positionOffset, boolean updateSelectedText) {
        setScrollPosition(position, newPosition, positionOffset, updateSelectedText, true);
    }

    public void setScrollPosition(int position, int newPosition, float positionOffset, boolean updateSelectedText,
                                  boolean updateIndicatorPosition) {
        final int roundedPosition = Math.round(position + positionOffset);
        if (roundedPosition < 0 || roundedPosition >= mTabStrip.getChildCount()) {
            return;
        }


        // Set the interpolator position, if enabled
        if (updateIndicatorPosition) {
            mTabStrip.setIndicatorPositionFromTabPosition(position, newPosition, positionOffset);
        }

        // Now update the scroll position, canceling any running animation
        if (mScrollAnimator != null && mScrollAnimator.isRunning()) {
            mScrollAnimator.cancel();
        }
        scrollTo(calculateScrollXForTab(position, positionOffset), 0);

        // Update the 'selected state' view as we scroll, if enabled
        if (updateSelectedText) {
            setSelectedTabView(roundedPosition);
        }
    }

    public int getSelectedTabPosition() {
        return mSelectedTab != null ? mSelectedTab.getPosition() : -1;
    }

    private void animateToTab(int newPosition) {
        if (newPosition == TabView.INVALID_POSITION) {
            return;
        }

        if (getWindowToken() == null || !ViewCompat.isLaidOut(this)
                || mTabStrip.childrenNeedLayout()) {
            // If we don't have a window token, or we haven't been laid out yet just draw the new
            // position now
            setScrollPosition(newPosition, newPosition, 0f, true);
            return;
        }

        final int startScrollX = getScrollX();
        final int targetScrollX = calculateScrollXForTab(newPosition, 0);

        if (startScrollX != targetScrollX) {
            ensureScrollAnimator();

            mScrollAnimator.setIntValues(startScrollX, targetScrollX);
            mScrollAnimator.start();
        }

        // Now animate the interpolator
        mTabStrip.animateIndicatorToPosition(newPosition, ANIMATION_DURATION);
    }

    private void ensureScrollAnimator() {
        if (mScrollAnimator == null) {
            mScrollAnimator = new ValueAnimator();
            mScrollAnimator.setInterpolator(new FastOutSlowInInterpolator());
            mScrollAnimator.setDuration(ANIMATION_DURATION);
            mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    scrollTo((int) animator.getAnimatedValue(), 0);
                }
            });
        }
    }

    void setScrollAnimatorListener(Animator.AnimatorListener listener) {
        ensureScrollAnimator();
        mScrollAnimator.addListener(listener);
    }

    private void dispatchTabSelected(@NonNull TabView tab) {
        for (int i = mSelectedListeners.size() - 1; i >= 0; i--) {
            mSelectedListeners.get(i).onTabSelected(tab.getPosition());
        }
    }

    private void dispatchTabUnselected(@NonNull TabView tab) {
        for (int i = mSelectedListeners.size() - 1; i >= 0; i--) {
            mSelectedListeners.get(i).onTabUnselected(tab.getPosition());
        }
    }

    private void dispatchTabReselected(@NonNull TabView tab) {
        for (int i = mSelectedListeners.size() - 1; i >= 0; i--) {
            mSelectedListeners.get(i).onTabReselected(tab.getPosition());
        }
    }


    private int calculateScrollXForTab(int position, float positionOffset) {
        if (mMode == MODE_SCROLLABLE) {
            final View selectedChild = mTabStrip.getChildAt(position);
            final View nextChild = position + 1 < mTabStrip.getChildCount()
                    ? mTabStrip.getChildAt(position + 1)
                    : null;
            final int selectedWidth = selectedChild != null ? selectedChild.getWidth() : 0;
            final int nextWidth = nextChild != null ? nextChild.getWidth() : 0;

            // base scroll amount: places center of tab in center of parent
            int scrollBase = selectedChild.getLeft() + (selectedWidth / 2) - (getWidth() / 2);
            // offset amount: fraction of the distance between centers of tabs
            int scrollOffset = (int) ((selectedWidth + nextWidth) * 0.5f * positionOffset);

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
        if (mTabTextColors != textColor) {
            mTabTextColors = textColor;
            updateAllTabs();
        }
    }

    public void setTabIconColors(int normalColor, int selectedColor) {
        setTabIconColors(createColorStateList(normalColor, selectedColor));
    }

    public void setTabIconColors(@Nullable ColorStateList iconColor) {
        if (mTabIconColors != iconColor) {
            mTabIconColors = iconColor;
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
        for (int i = 0, z = mTabs.size(); i < z; i++) {
            TabView tabView = mTabs.get(i);
            if (tabView.getTitleColor() == null) {
                tabView.setTitleColor(mTabTextColors);
            }
            if (tabView.getIcons() == null) {
                tabView.setIconColor(mTabIconColors);
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
            if (tabPageChangeListener != null) {
                this.viewPager.removeOnPageChangeListener(tabPageChangeListener);
            }
//            if (mAdapterChangeListener != null) {
//                mViewPager.removeOnAdapterChangeListener(mAdapterChangeListener);
//            }
        }
        this.viewPager = viewPager;
        if (this.viewPager != null) {
            tabPageChangeListener = new TabPageChangeListener(this);
            this.viewPager.addOnPageChangeListener(tabPageChangeListener);
        }
    }
}
