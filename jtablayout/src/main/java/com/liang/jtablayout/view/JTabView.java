package com.liang.jtablayout.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class JTabView extends TabView {
    private TextView titleView;
    private ImageView iconView;
    private LinearLayout tab;

    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

    public JTabView(Context context) {
        super(context);
        tab = new LinearLayout(context);
        tab.setGravity(Gravity.CENTER);
        titleView = new TextView(context);
        iconView = new ImageView(context);
        tab.addView(iconView);
        tab.addView(titleView);
        addView(tab, params);
    }

    @Override
    public TabView setMode(int mode) {
        super.setMode(mode);
        tab.removeAllViews();
        tab.setOrientation(mode);
        tab.addView(iconView);
        tab.addView(titleView);
        return this;
    }

    @Override
    public TabView setTitle(CharSequence title) {
        super.setTitle(title);
        if (titleView != null) {
            titleView.setText(title);
        }
        return this;
    }

    @Override
    public TabView setTitleColor(ColorStateList titleColor) {
        super.setTitleColor(titleColor);
        if (titleColor != null && titleView != null) {
            titleView.setTextColor(titleColor);
        }
        return this;
    }

    @Override
    public TabView setIcon(Drawable defaultIcon) {
        super.setIcon(defaultIcon);
        if (getDefaultIcon() != null && iconView != null) {
            iconView.setImageDrawable(getDefaultIcon());
        }
        return this;
    }

    @Override
    public TabView setIconColor(ColorStateList iconColor) {
        super.setIconColor(iconColor);
        if (iconColor != null && iconView != null) {
            iconView.setImageDrawable(isSelected() ? getIcons()[1]
                    : getIcons()[0]);
        }
        return this;
    }

    @Override
    public TabView setIcon(Drawable defaultIcon, Drawable selectedIcon) {
        super.setIcon(defaultIcon, selectedIcon);
        if (getIcons() != null && iconView != null) {
            iconView.setImageDrawable(isSelected() ? getIcons()[1]
                    : getIcons()[0]);
        }
        return this;
    }

    @Override
    public void setSelected(final boolean selected) {
        final boolean changed = isSelected() != selected;
        super.setSelected(selected);
        if (changed && selected && Build.VERSION.SDK_INT < 16) {
            // Pre-JB we need to manually send the TYPE_VIEW_SELECTED event
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
        }
        // Always dispatch this to the child views, regardless of whether the value has
        // changed
        if (titleView != null) {
            titleView.setSelected(selected);
        }

        if (getIcons() != null && iconView != null) {
            iconView.setImageDrawable(isSelected() ? getIcons()[1]
                    : getIcons()[0]);
        }
    }
}
