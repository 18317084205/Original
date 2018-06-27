package com.liang.jtablayout.listener;

import android.support.v4.view.ViewPager;
import android.util.Log;

import com.liang.jtablayout.TabLayout;

import java.lang.ref.WeakReference;

import static android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING;
import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;
import static android.support.v4.view.ViewPager.SCROLL_STATE_SETTLING;

public class TabPageChangeListener implements ViewPager.OnPageChangeListener {
    private WeakReference<TabLayout> mTabLayoutRef;
    private int mPreviousScrollState;
    private int mScrollState;
    private int selectedPosition;

    public TabPageChangeListener(TabLayout tabLayout) {
        this.mTabLayoutRef = new WeakReference<>(tabLayout);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.e("TabPageChangeListener", "onPageScrolled: ..." + position);
        selectedPosition = position;
        TabLayout tabLayout = mTabLayoutRef.get();
        if (tabLayout != null) {
            // Only update the text selection if we're not settling, or we are settling after
            // being dragged
            final boolean updateText = mScrollState != SCROLL_STATE_SETTLING ||
                    mPreviousScrollState == SCROLL_STATE_DRAGGING;
            // Update the indicator if we're not settling after being idle. This is caused
            // from a setCurrentItem() call and will be handled by an animation from
            // onPageSelected() instead.
            final boolean updateIndicator = !(mScrollState == SCROLL_STATE_SETTLING
                    && mPreviousScrollState == SCROLL_STATE_IDLE);
            tabLayout.setScrollPosition(position, positionOffset, updateText, updateIndicator);
        }
    }

    @Override
    public void onPageSelected(int position) {
        Log.e("TabPageChangeListener", "onPageSelected: ..." + position);
        TabLayout tabLayout = mTabLayoutRef.get();
        if (mScrollState == SCROLL_STATE_IDLE && tabLayout != null && selectedPosition != position
                && position < tabLayout.getTabCount()) {
            tabLayout.dispatchTabUnselected(tabLayout.getTabAt(selectedPosition));
            tabLayout.dispatchTabSelected(tabLayout.getTabAt(position));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mPreviousScrollState = mScrollState;
        mScrollState = state;
    }
}
