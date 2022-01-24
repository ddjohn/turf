package com.avelon.turf;

import android.util.Log;

public class Logger {
    private String tag;

    public Logger(Class<?> clazz) {
        tag = clazz.getCanonicalName();
    }

    public void method(String s, Object... objects) {
        Log.d(tag, "<-met-> " + s);
        for(Object o : objects) {
            Log.d(tag, "<-arg-> " + o);
        }
    }
    public void error(String s) {
        Log.e(tag, "<-err-> " + s);
    }
    public void  info(String s) {
        Log.i(tag, "<-inf-> " + s);
    }
    public void debug(String s) {
        Log.v(tag, "<-dbg-> " + s);
    }
}
