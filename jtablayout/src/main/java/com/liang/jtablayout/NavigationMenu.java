package com.liang.jtablayout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.ViewUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liang.jtablayout.utils.DrawableUtils;

public class NavigationMenu extends Menu implements View.OnClickListener {

    private ImageView menu_icon;
    private TextView menu_title;
    private TextView menu_badge;
    private Drawable icon_unSelected;
    private Drawable icon_selected;
    private int title_unSelected = Color.GRAY;
    private int title_selected = Color.BLACK;
    private int mode = MODE_VERTICAL;

    @Override
    public void setClickListener(OnClickListener listener) {
        clickListener = listener;
    }

    public static NavigationMenu newInstance(Context context, String title, int imgId) {
        return new NavigationMenu(context, title, imgId);
    }

    public static NavigationMenu newInstance(Context context, int strId, int imgId) {
        return new NavigationMenu(context, context.getString(strId), imgId);
    }

    private NavigationMenu(@NonNull Context context, String title, int imgId) {
        super(context, title, imgId);
        setGravity(Gravity.CENTER);
    }

    @Override
    public void create() {
        if (icon != null && icon_unSelected == null && icon_selected == null) {
            icon_unSelected = DrawableUtils.tintDrawable(icon, Color.GRAY);
            icon_selected = DrawableUtils.tintDrawable(icon, Color.BLACK);
        }
        View view = LayoutInflater.from(getContext()).inflate(
                mode == MODE_VERTICAL ? R.layout.navigation_menu_vertical : R.layout.navigation_menu_horizontal, null);
        menu_icon = view.findViewById(R.id.navigation_icon);
        menu_title = view.findViewById(R.id.navigation_title);
        menu_badge = view.findViewById(R.id.navigation_badge);
        RelativeLayout.LayoutParams itemParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(view, itemParams);
        view.setOnClickListener(this);
        refreshMenu();
    }

    @Override
    public Menu setChecked(boolean checked) {
        isChecked = checked;
        return this;
    }

    @Override
    public void refreshMenu() {
        if (menu_title != null) {
            menu_title.setText(title);
            menu_title.setTextColor(isChecked ? title_selected : title_unSelected);
        }
        if (menu_icon != null && icon != null) {
            menu_icon.setImageDrawable(isChecked ? icon_selected : icon_unSelected);
        }
    }

    @Override
    public Menu setMode(int mode) {
        this.mode = mode;
        return this;
    }

    @Override
    public Menu setIconColor(int unSelectedColor, int selectedColor) {
        if (icon != null) {
            icon_unSelected = DrawableUtils.tintDrawable(icon, unSelectedColor);
            icon_selected = DrawableUtils.tintDrawable(icon, selectedColor);
        }
        return this;
    }

    @Override
    public Menu setIcon(int unSelectedImgId, int selectedImgId) {
        icon_unSelected = ContextCompat.getDrawable(getContext(), unSelectedImgId);
        icon_selected = ContextCompat.getDrawable(getContext(), selectedImgId);
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

    @Override
    public void onClick(View view) {
        if (clickListener != null) {
            clickListener.onClick(this);
        }
    }
}
