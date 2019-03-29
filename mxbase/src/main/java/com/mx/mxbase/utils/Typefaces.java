package com.mx.mxbase.utils;

import android.graphics.Typeface;

import com.mx.mxbase.constant.APPLog;

import java.util.Hashtable;

/**
 * Created by Administrator on 2016/10/21.
 */
public class Typefaces {
    private static final String TAG = "Typefaces";

    private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

    public static Typeface get(String assetPath) {
        synchronized (cache) {
            if (!cache.containsKey(assetPath)) {
                try {
                    Typeface t = Typeface.createFromFile(assetPath);
                    cache.put(assetPath, t);
                } catch (Exception e) {
                    APPLog.e(TAG, "Could not get typeface '" + assetPath
                            + "' because " + e.getMessage());
                    return null;
                }
            }
            return cache.get(assetPath);
        }
    }
}
