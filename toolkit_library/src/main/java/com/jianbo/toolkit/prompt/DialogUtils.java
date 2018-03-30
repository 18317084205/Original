package com.jianbo.toolkit.prompt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;

import com.jianbo.toolkit.widget.DialogBuilder;

/**
 * Created by Jianbo on 2018/3/29.
 */

public class DialogUtils {

    private static Dialog dialog;

    private static void showDialog(Context context, String title, int titleColor, String message, int messageColor,
                                   String sureText, int sureColor, String cancelText, int cancelColor,
                                   DialogBuilder.DialogListener dialogListener) {

        dismiss();
        DialogBuilder dialogBuilder = new DialogBuilder(context);

        if (!TextUtils.isEmpty(title)) {
            dialogBuilder.title(title);
        } else {
            dialogBuilder.noTitle();
        }

        if (titleColor > 0) {
            dialogBuilder.titleColor(titleColor);
        }

        if (!TextUtils.isEmpty(message)) {
            dialogBuilder.message(message);
        }

        if (messageColor > 0) {
            dialogBuilder.messageColor(messageColor);
        }


        if (!TextUtils.isEmpty(sureText)) {
            dialogBuilder.sureText(sureText);
        }

        if (sureColor > 0) {
            dialogBuilder.sureTextColor(sureColor);
        }


        if (!TextUtils.isEmpty(cancelText)) {
            dialogBuilder.cancelText(cancelText);
        } else {
            dialogBuilder.showCancelButton(false);
        }

        if (cancelColor > 0) {
            dialogBuilder.cancelTextColor(cancelColor);
        }

        dialogBuilder.setCancelable(false);

        dialogBuilder.setListener(dialogListener);

        dialog = dialogBuilder.builder();

        dialog.show();

    }

    public static void showDialog(Context context, String title, String message,
                                  String sureText, String cancelText,
                                  DialogBuilder.DialogListener dialogListener) {
        showDialog(context, title, 0, message, 0, sureText, 0, cancelText, 0, dialogListener);

    }

    public static void showDialog(Context context, String message, int messageColor,
                                  String sureText, int sureColor,
                                  DialogBuilder.DialogListener dialogListener) {
        showDialog(context, null, 0, message, messageColor, sureText, sureColor, null, 0, dialogListener);
    }

    public static void showDialog(Context context, String message, int messageColor,
                                  String sureText, int sureColor, String cancelText, int cancelColor,
                                  DialogBuilder.DialogListener dialogListener) {
        showDialog(context, null, 0, message, messageColor, sureText, sureColor, cancelText, cancelColor, dialogListener);
    }

    public static void showDialog(Context context, View view, DialogBuilder.DialogListener dialogListener) {
        DialogBuilder dialogBuilder = new DialogBuilder(context);
        if (view != null) {
            dialogBuilder.setView(view);
        }

        dialogBuilder.setCancelable(false);

        dialogBuilder.setListener(dialogListener);

        dialog = dialogBuilder.builder();

        dialog.show();
    }


    /**
     * 关闭提示框
     */
    public static void dismiss() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
        }
    }
}
