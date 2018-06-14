package com.jianbo.toolkit.prompt;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;

public class ViewUtils {
    public static  <T extends View> T findViewById(Context context,int id) {
        return (T) ((Activity)context).findViewById(id);
    }

    public static  <T extends View> T findViewById(View view,int id) {
        return (T) view.findViewById(id);
    }
}
