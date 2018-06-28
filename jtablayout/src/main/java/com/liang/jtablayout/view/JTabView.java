package com.liang.jtablayout.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liang.jtablayout.R;

public class JTabView extends TabView {
    private TextView titleView;
    private ImageView iconView;
    private TextView badgeView;

    private FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    public JTabView(Context context) {
        super(context);
        updateLayout();
    }

    private void updateLayout() {
        View view = LayoutInflater.from(getContext()).inflate(
                getMode() == VERTICAL ? R.layout.navigation_menu_vertical : R.layout.navigation_menu_horizontal, null);
        iconView = view.findViewById(R.id.navigation_icon);
        titleView = view.findViewById(R.id.navigation_title);
        badgeView = view.findViewById(R.id.navigation_badge);
        titleView.setSingleLine(true);
        titleView.setEllipsize(TextUtils.TruncateAt.END);
        badgeView.setSingleLine(true);
        badgeView.setEllipsize(TextUtils.TruncateAt.END);
        params.gravity = Gravity.CENTER;
        addView(view, 0, params);
        updateView();
    }

    private void updateView() {
        if (titleView != null) {
            if (!TextUtils.isEmpty(getTitle())) {
                titleView.setText(getTitle());
                titleView.setVisibility(VISIBLE);
            } else {
                titleView.setVisibility(GONE);
            }
        }
        if (getTitleColor() != null && titleView != null) {
            titleView.setTextColor(getTitleColor());
        }

        if (iconView != null) {
            if (getIcons() != null) {
                iconView.setImageDrawable(isSelected() ? getIcons()[1] : getIcons()[0]);
                iconView.setVisibility(VISIBLE);
            } else {
                iconView.setVisibility(GONE);
            }
        }

    }

    @Override
    public TabView setMode(int mode) {
        super.setMode(mode);
        updateLayout();
        return this;
    }

    @Override
    public TabView setTitle(CharSequence title) {
        super.setTitle(title);
        updateView();
        return this;
    }

    @Override
    public TabView setTitleColor(ColorStateList titleColor) {
        super.setTitleColor(titleColor);
        updateView();
        return this;
    }

    @Override
    public TabView setIcon(Drawable defaultIcon) {
        super.setIcon(defaultIcon);
        updateView();
        return this;
    }

    @Override
    public TabView setIconColor(ColorStateList iconColor) {
        super.setIconColor(iconColor);
        updateView();
        return this;
    }

    @Override
    public TabView setIcon(Drawable defaultIcon, Drawable selectedIcon) {
        super.setIcon(defaultIcon, selectedIcon);
        updateView();
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
        if (titleView != null && titleView.getVisibility() != GONE) {
            titleView.setSelected(selected);
        }

        if (getIcons() != null && iconView != null && iconView.getVisibility() != GONE) {
            iconView.setImageDrawable(isSelected() ? getIcons()[1]
                    : getIcons()[0]);
        }
    }
}
