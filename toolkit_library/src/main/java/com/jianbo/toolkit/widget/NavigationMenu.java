package com.jianbo.toolkit.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianbo.toolkit.R;
import com.jianbo.toolkit.prompt.DrawableUtils;
import com.jianbo.toolkit.prompt.ViewUtils;

public class NavigationMenu extends Menu {

    private ImageView menu_icon;
    private TextView menu_title;
    private TextView menu_badge;
    private int icon_unSelected = Color.GRAY;
    private int icon_selected = Color.BLACK;
    private int title_unSelected = Color.GRAY;
    private int title_selected = Color.BLACK;
    private boolean selected;
    private Drawable icon;
    private String title = "";
    private int mode = MODE_VERTICAL;

    public NavigationMenu(@NonNull Context context) {
        super(context);
        setGravity(Gravity.CENTER);
    }

    @Override
    public void build() {
        View view = LayoutInflater.from(getContext()).inflate(
                mode == MODE_VERTICAL ? R.layout.navigation_menu_vertical : R.layout.navigation_menu_horizontal, null);
        menu_icon = ViewUtils.findViewById(view, R.id.navigation_icon);
        menu_title = ViewUtils.findViewById(view, R.id.navigation_title);
        menu_badge = ViewUtils.findViewById(view, R.id.navigation_badge);
        LayoutParams itemParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        LayoutParams itemParams = new LayoutParams(mode == MODE_VERTICAL ?
//                ViewGroup.LayoutParams.WRAP_CONTENT : ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(view, itemParams);
        refreshMenu();
    }

    @Override
    public Menu setIcon(int resId) {
        icon = ContextCompat.getDrawable(getContext(), resId);
        return this;
    }

    @Override
    public Menu setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public Menu setTitle(int strId) {
        this.title = getContext().getString(strId);
        return this;
    }

    @Override
    public Menu setChecked(boolean checked) {
        selected = checked;
        return this;
    }

    @Override
    public void refreshMenu() {
        if (menu_title != null) {
            menu_title.setText(title);
            menu_title.setTextColor(selected ? title_selected : title_unSelected);
        }
        if (menu_icon != null && icon != null) {
            Drawable fIcon = DrawableUtils.tintDrawable(icon, selected ?
                    icon_selected :
                    icon_unSelected);
            menu_icon.setImageDrawable(fIcon);
        }
    }

    @Override
    public Menu setMode(int mode) {
        this.mode = mode;
        return this;
    }

    @Override
    public Menu setIconColor(int unSelectedColor, int selectedColor) {
        icon_unSelected = unSelectedColor;
        icon_selected = selectedColor;
        return this;
    }

    @Override
    public Menu setTitleColor(int unSelectedColor, int selectedColor) {
        title_unSelected = unSelectedColor;
        title_selected = selectedColor;
        return this;
    }

    @Override
    public void setBadge(int count) {
        if (menu_badge != null) {
            if (count > 0) {
                menu_badge.setText(count + "");
                menu_badge.setVisibility(VISIBLE);
            } else {
                menu_badge.setVisibility(GONE);
            }
        }
    }

}
