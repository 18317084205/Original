package com.jianbo.toolkit.prompt;


public class TextUtils {
    public static boolean isNotEmpty(CharSequence str) {
        return str != null && !android.text.TextUtils.isEmpty(str);
    }

    public static boolean isSpace(String input) {
        if (input == null || input.trim().length() == 0)
            return true;
        else
            return false;
    }

}
