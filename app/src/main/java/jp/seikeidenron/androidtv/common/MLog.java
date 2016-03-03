package jp.seikeidenron.androidtv.common;

import android.util.Log;

import jp.seikeidenron.androidtv.BuildConfig;

public class MLog {

    private static final String TAG = MLog.class.getSimpleName();

    // Loggable flags
    private static final boolean isVerboseLoggable = BuildConfig.DEBUG;
    private static final boolean isDebugLoggable   = true;
    private static final boolean isInfoLoggable    = true;
    private static final boolean isWarningLoggable = true;
    private static final boolean isErrorLoggable   = true;

    private MLog(){
        // no class instance for utility class
    }

    public static void v(String tag, String msg) {
        if(isVerboseLoggable == true) Log.v(tag, msg);
    }

    public static void d(String tag, String msg) {
        if(isDebugLoggable == true) Log.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        if(isInfoLoggable == true) Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        if(isWarningLoggable == true) Log.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        if(isErrorLoggable == true) Log.e(tag, msg);
    }
}
