package com.liang.jtablayout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pools;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.liang.jtablayout.view.SlidingTabStrip;
import com.liang.jtablayout.view.TabView;

import java.util.ArrayList;
import java.util.Iterator;

public class TabLayout extends HorizontalScrollView {

    private Pools.Pool<TabView> mTabViewPool = new Pools.SynchronizedPool<>(12);
    private ArrayList<TabView> mTabs = new ArrayList<>();
    private ArrayList<OnTabSelectedListener> mSelectedListeners = new ArrayList<>();
    public static final int MODE_SCROLLABLE = 0;
    public static final int MODE_FIXED = 1;

    private static final int ANIMATION_DURATION = 300;

    private OnTabSelectedListener mSelectedListener;
    private ValueAnimator mScrollAnimator;

    private ViewPager mViewPager;

    private SlidingTabStrip mTabStrip;

    private int mMode = MODE_FIXED;

    private TabView mSelectedTab;

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
        setHorizontalScrollBarEnabled(false);
        mTabStrip = new SlidingTabStrip(context);
        super.addView(mTabStrip, 0, new HorizontalScrollView.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        updateTabViews(true);
    }

    public void updateTabViews(boolean requestLayout) {
        for (int i = 0; i < mTabStrip.getChildCount(); i++) {
            View child = mTabStrip.getChildAt(i);
            updateTabViewLayoutParams((LinearLayout.LayoutParams) child.getLayoutParams());
            if (requestLayout) {
                child.requestLayout();
            }
        }
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

    public void setScrollPosition(int position, float positionOffset, boolean updateSelectedText) {
        setScrollPosition(position, positionOffset, updateSelectedText, true);
    }

    public void setScrollPosition(int position, float positionOffset, boolean updateSelectedText,
                                  boolean updateIndicatorPosition) {
        final int roundedPosition = Math.round(position + positionOffset);
        if (roundedPosition < 0 || roundedPosition >= mTabStrip.getChildCount()) {
            return;
        }

        // Set the indicator position, if enabled
        if (updateIndicatorPosition) {
            mTabStrip.setIndicatorPositionFromTabPosition(position, positionOffset);
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

    private float getScrollPosition() {
        return mTabStrip.getIndicatorPosition();
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
        configureTab(tab, position);
        addTabView(tab);

        if (setSelected) {
            tab.select();
        }
    }

    private void configureTab(TabView tab, int position) {
        tab.setPosition(position);
        mTabs.add(position, tab);

        int count = mTabs.size();
        for (int i = position + 1; i < count; i++) {
            mTabs.get(i).setPosition(i);
        }
    }

    private void addTabView(TabView tab) {
        TabView tabView = tab.tabView;
        mTabStrip.addView(tabView, tab.getPosition(), createLayoutParamsForTabs());
    }

    private LinearLayout.LayoutParams createLayoutParamsForTabs() {
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
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
    public Tab newTab() {
        Tab tab = sTabPool.acquire();
        if (tab == null) {
            tab = new Tab();
        }
        tab.tabLayout = this;
        tab.tabView = createTabView(tab);
        return tab;
    }

    public int getTabCount() {
        return mTabs.size();
    }

    @Nullable
    public Tab getTabAt(int index) {
        return (index < 0 || index >= getTabCount()) ? null : mTabs.get(index);
    }

    public int getSelectedTabPosition() {
        return mSelectedTab != null ? mSelectedTab.getPosition() : -1;
    }

    private TabView createTabView(@NonNull Tab tab) {
        TabView tabView = mTabViewPool != null ? mTabViewPool.acquire() : null;
        if (tabView == null) {
            tabView = new TabView(getContext());
        }
        tabView.setTab(tab);
        tabView.setFocusable(true);
        return tabView;
    }

    public void removeTab(Tab tab) {
        if (tab.tabLayout != this) {
            throw new IllegalArgumentException("Tab does not belong to this TabLayout.");
        }

        removeTabAt(tab.getPosition());
    }

    public void removeTabAt(int position) {
        int selectedTabPosition = mSelectedTab != null ? mSelectedTab.getPosition() : 0;
        removeTabViewAt(position);

        final Tab removedTab = mTabs.remove(position);
        if (removedTab != null) {
            removedTab.reset();
            sTabPool.release(removedTab);
        }

        final int newTabCount = mTabs.size();
        for (int i = position; i < newTabCount; i++) {
            mTabs.get(i).setPosition(i);
        }

        if (selectedTabPosition == position) {
            selectTab(mTabs.isEmpty() ? null : mTabs.get(Math.max(0, position - 1)));
        }
    }

    void selectTab(Tab tab) {
        selectTab(tab, true);
    }

    void selectTab(final Tab tab, boolean updateIndicator) {
        final Tab currentTab = mSelectedTab;

        if (currentTab == tab) {
            if (currentTab != null) {
                dispatchTabReselected(tab);
                animateToTab(tab.getPosition());
            }
        } else {
            final int newPosition = tab != null ? tab.getPosition() : Tab.INVALID_POSITION;
            if (updateIndicator) {
                if ((currentTab == null || currentTab.getPosition() == Tab.INVALID_POSITION)
                        && newPosition != Tab.INVALID_POSITION) {
                    // If we don't currently have a tab, just draw the indicator
                    setScrollPosition(newPosition, 0f, true);
                } else {
                    animateToTab(newPosition);
                }
                if (newPosition != Tab.INVALID_POSITION) {
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

        for (final Iterator<Tab> i = mTabs.iterator(); i.hasNext();) {
            final Tab tab = i.next();
            i.remove();
            tab.reset();
            sTabPool.release(tab);
        }

        mSelectedTab = null;
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
}
