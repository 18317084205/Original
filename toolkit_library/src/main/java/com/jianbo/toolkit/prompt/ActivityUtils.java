package com.jianbo.toolkit.prompt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class ActivityUtils {


    public static class SkipFactory {
        private Context context;
        private Intent intent;

        public SkipFactory(Context context, Class clazz) {
            this.context = context;
            this.intent = new Intent(context, clazz);
        }

        public SkipFactory putParams(String key, String value) {
            intent.putExtra(key, value);
            return this;
        }

        public SkipFactory putParams(String key, int value) {
            intent.putExtra(key, value);
            return this;
        }

        public SkipFactory putParams(String key, boolean value) {
            intent.putExtra(key, value);
            return this;
        }

        public SkipFactory putParams(String key, Parcelable value) {
            intent.putExtra(key, value);
            return this;
        }

        public SkipFactory putParams(String key, int[] value) {
            intent.putExtra(key, value);
            return this;
        }

        public SkipFactory putParams(String key, String[] value) {
            intent.putExtra(key, value);
            return this;
        }

        public SkipFactory putParams(String key, ArrayList<Integer> value) {
            intent.putIntegerArrayListExtra(key, value);
            return this;
        }

        public SkipFactory putParams(String key, Serializable value) {
            intent.putExtra(key, value);
            return this;
        }

        public void start() {
            context.startActivity(intent);
        }

        public void start(int requestCode) {
            ((Activity) context).startActivityForResult(intent, requestCode);
        }
    }
}
