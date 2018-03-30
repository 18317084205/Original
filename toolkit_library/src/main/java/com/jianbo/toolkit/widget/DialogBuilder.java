package com.jianbo.toolkit.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jianbo.toolkit.R;

/**
 * Created by Jianbo on 2018/3/29.
 */

public class DialogBuilder {
    private Context context;
    private View mLayout;
    private boolean mCancelable = false;
    private DialogListener dialogListener;
    private Dialog dialog;

    public DialogBuilder(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayout = inflater.inflate(R.layout.base_dialog_layout, null);


    }

    //能否返回键取消
    public DialogBuilder setCancelable(Boolean boolean1) {
        this.mCancelable = boolean1;
        return this;
    }

    public DialogBuilder title(int title) {
        ((TextView) mLayout.findViewById(R.id.title)).setText(title);
        return this;
    }

    public DialogBuilder title(String title) {
        ((TextView) mLayout.findViewById(R.id.title)).setText(title);
        return this;
    }

    public DialogBuilder titleColor(int color) {
        ((TextView) mLayout.findViewById(R.id.title)).setTextColor(context.getResources().getColor(color));
        return this;
    }

    public DialogBuilder setListener(DialogListener listener) {
        this.dialogListener = listener;
        return this;
    }

    public DialogBuilder message(String message) {
        ((TextView) mLayout.findViewById(R.id.message)).setText(message);
        return this;
    }

    public DialogBuilder messageColor(int color) {
        ((TextView) mLayout.findViewById(R.id.message)).setTextColor(context.getResources().getColor(color));
        return this;
    }

    //确定按钮文本
    public DialogBuilder sureText(String str) {
        ((TextView) mLayout.findViewById(R.id.sure)).setText(str);
        return this;
    }

    public DialogBuilder sureTextColor(int color) {
        ((TextView) mLayout.findViewById(R.id.sure)).setTextColor(context.getResources().getColor(color));
        return this;
    }

    //取消按钮文本
    public DialogBuilder cancelText(String str) {
        ((TextView) mLayout.findViewById(R.id.cancel)).setText(str);
        return this;
    }

    public DialogBuilder cancelTextColor(int color) {
        ((TextView) mLayout.findViewById(R.id.cancel)).setTextColor(context.getResources().getColor(color));
        return this;
    }

    //切换内容view
    public DialogBuilder setView(View v) {
        ((LinearLayout) mLayout.findViewById(R.id.content)).removeAllViews();
        ((LinearLayout) mLayout.findViewById(R.id.content)).addView(v);
        return this;
    }

    public DialogBuilder setView(View v, LinearLayout.LayoutParams params) {
        ((LinearLayout) mLayout.findViewById(R.id.content)).removeAllViews();
        ((LinearLayout) mLayout.findViewById(R.id.content)).addView(v, params);
        return this;
    }

    public DialogBuilder showCancelButton(boolean isShow) {
        if (!isShow) {
            mLayout.findViewById(R.id.cancel).setVisibility(View.GONE);
        }
        return this;
    }

    public DialogBuilder noTitle() {
        mLayout.findViewById(R.id.title).setVisibility(View.GONE);
        return this;
    }

    public Dialog builder() {
        dialog = new Dialog(context, R.style.base_dialog);
        dialog.setCancelable(mCancelable);
        dialog.addContentView(mLayout, new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        mLayout.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (dialogListener != null) {
                    dialogListener.sure();
                }
            }
        });

        mLayout.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (dialogListener != null) {
                    dialogListener.cancel();
                }
            }
        });

        return dialog;
    }

    public interface DialogListener {
        void sure();

        void cancel();
    }
}
