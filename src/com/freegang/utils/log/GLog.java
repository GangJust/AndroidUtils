package com.freegang.utils.log;

import android.util.Log;

import java.util.Arrays;

public class GLog {

    private GLog() {}

    public static final String TAG = "GLog";

    public static void d(Object[] o) {
        d("", Arrays.deepToString(o));
    }

    public static void d(Object o) {
        d("", o);
    }

    public static void d(String msg, Object[] o) {
        Log.d(TAG, msg + ": " + Arrays.deepToString(o));
    }

    public static void d(String msg, Object o) {
        Log.d(TAG, msg + ": " + o.toString());
    }
}
