package com.liang.jtablayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;

import com.liang.jtablayout.utils.DensityUtils;

public class MenuView extends ViewGroup implements Menu.OnClickListener {

    private int childCount;//子View数量
    private boolean isScroller;
    private OnTabChangeListener changeListener;
    private int icon_unSelected = 0;
    private int icon_selected = 0;
    private int title_unSelected = 0;
    private int title_selected = 0;
    private int selectedIndicatorHeight = 320;
    private Paint mSelectedIndicatorPaint;

    public static MenuView newInstance(Context context) {
        return new MenuView(context);
    }

    public void setChangeListener(OnTabChangeListener changeListener) {
        this.changeListener = changeListener;
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

    private MenuView(Context context) {
        super(context);
        setWillNotDraw(false);
        setClipChildren(false);
        mSelectedIndicatorPaint = new Paint();
        mSelectedIndicatorPaint.setColor(Color.BLUE);
        mSelectedIndicatorPaint.setStrokeWidth(selectedIndicatorHeight);
        View view = new View(context);
        addView(view);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int count = getChildCount() - 1;
        int tWidth = 0;
        if (isScroller) {
            for (int i = 1; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child.getVisibility() == GONE) {
                    continue;
                }
                child.measure(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
                tWidth += child.getMeasuredWidth();
            }
        } else {
            int maxAvailable = width / (count == 0 ? 1 : count);
            for (int i = 1; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child.getVisibility() == GONE) {
                    continue;
                }
                child.measure(MeasureSpec.makeMeasureSpec(maxAvailable, MeasureSpec.EXACTLY),
                        LayoutParams.WRAP_CONTENT);
                tWidth += child.getMeasuredWidth();
            }
        }
        setMeasuredDimension(
                View.resolveSizeAndState(tWidth,
                        MeasureSpec.makeMeasureSpec(tWidth, MeasureSpec.EXACTLY), 0),
                heightMeasureSpec);
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        childCount = getChildCount();
        final int width = right - left;
        final int height = bottom - top;
        int used = 0;
        int id = 0;
        for (int i = 1; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            child.setId(id);
            id++;
            if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                child.layout(width - used - child.getMeasuredWidth(), 0, width - used, height);
            } else {
                child.layout(used, 0, child.getMeasuredWidth() + used, height);
            }
            used += child.getMeasuredWidth();
            if (((Menu) child).isChecked && getChildAt(0).getVisibility() == VISIBLE) {
                getChildAt(0).layout(child.getLeft(), height - selectedIndicatorHeight, child.getRight(), height);
            }
        }

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawLine(getChildAt(1).getX(), getBottom(),
                getChildAt(1).getRight(), getBottom(), mSelectedIndicatorPaint);
    }

    public void addMenu(Menu menu) {
        if (title_unSelected != 0 && title_selected != 0) {
            menu.setTitleColor(title_unSelected, title_selected);
        }
        if (icon_unSelected != 0 && icon_selected != 0) {
            menu.setIconColor(icon_unSelected, icon_selected);
        }
        menu.create();
        menu.setClickListener(this);
        addView(menu);
    }

    @Override
    public void onClick(Menu menu) {
        if (menu.isChecked) {
            if (changeListener != null) {
                changeListener.onClick(menu.getId());
            }
            return;
        }

        if (getChildCount() > 1) {
            for (int i = 1; i < childCount; i++) {
                Menu child = (Menu) getChildAt(i);
                if (child.getVisibility() == GONE) {
                    continue;
                }
                if (menu.getId() == child.getId()) {
                    child.setChecked(true);
                } else {
                    if (!child.isChecked) {
                        continue;
                    } else {
                        child.setChecked(false);
                    }
                }
                child.refreshMenu();
            }
        }
        if (changeListener != null) {
            changeListener.onChanged(menu.getId());
        }
    }

    public void setSelectedIndicatorHeight(int selectedIndicatorHeight) {
        this.selectedIndicatorHeight = DensityUtils.dip2px(getContext(), selectedIndicatorHeight);
    }

    public void setSelectedIndicatorColor(int selectedIndicatorColor) {
        getChildAt(0).setBackgroundColor(selectedIndicatorColor);
    }

    public void isIndicator(boolean isShow) {
        getChildAt(0).setVisibility(isShow ? VISIBLE : GONE);
    }

    interface OnTabChangeListener {
        void onChanged(int position);

        void onClick(int position);
    }
}
