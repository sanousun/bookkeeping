package com.sz.bookkeeping.util;

import android.util.Log;

/**
 * Created with Android Studio.
 * User: dashu
 * Date: 2017/2/27
 * Time: 下午4:59
 * Desc:
 */

public class LogUtil {
    private static final String DEFAULT_TAG = "xyz";

    public static void e(String msg) {
        Log.e(DEFAULT_TAG, msg);
    }
}
