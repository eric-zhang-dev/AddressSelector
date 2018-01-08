package com.example.appforsql.utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.appforsql.common.Constant;

public class SharedUtils {
    public static String getString(Context context, String key, String defValue) {
        SharedPreferences mpref = context.getSharedPreferences(Constant.SHARED_KEY,
                Context.MODE_PRIVATE);
        String value = mpref.getString(key, defValue);
        return value;
    }

    public static void putString(Context context, String key, String value) {

        SharedPreferences mpref = context.getSharedPreferences(Constant.SHARED_KEY,
                Context.MODE_PRIVATE);
        mpref.edit().putString(key, value).commit();
    }

    public static void putLong(Context context, String key, long value) {
        SharedPreferences mpref = context.getSharedPreferences(Constant.SHARED_KEY,
                Context.MODE_PRIVATE);
        mpref.edit().putLong(key, value).commit();
    }

    public static long getLong(Context context, String key, long defValue) {
        SharedPreferences mpref = context.getSharedPreferences(Constant.SHARED_KEY, Context.MODE_PRIVATE);
        long value = mpref.getLong(key, defValue);
        return value;
    }

    public static void putInt(Context context, String key, int value) {
        SharedPreferences mpref = context.getSharedPreferences(Constant.SHARED_KEY,
                Context.MODE_PRIVATE);
        mpref.edit().putInt(key, value).commit();
    }

    public static int getInt(Context context, String key, int defValue) {
        SharedPreferences mpref = context.getSharedPreferences(Constant.SHARED_KEY, Context.MODE_PRIVATE);
        int value = mpref.getInt(key, defValue);
        return value;
    }

    public static boolean getBoolean(Context context, String key,
                                     boolean defValue) {
        SharedPreferences mpref = context.getSharedPreferences(Constant.SHARED_KEY,
                Context.MODE_PRIVATE);
        Boolean value = mpref.getBoolean(key, defValue);
        return value;
    }

    public static void putBoolean(Context context, String key, boolean value) {

        SharedPreferences mpref = context.getSharedPreferences(Constant.SHARED_KEY,
                Context.MODE_PRIVATE);
        mpref.edit().putBoolean(key, value).commit();
    }

    public static void removeAll(Context context) {
        SharedPreferences mpref = context.getSharedPreferences(Constant.SHARED_KEY,
                Context.MODE_PRIVATE);
        mpref.edit().clear().commit();
    }
    public static void putFloat(Context context, String key, float value) {
        SharedPreferences mpref = context.getSharedPreferences(Constant.SHARED_KEY,
                Context.MODE_PRIVATE);
        mpref.edit().putFloat(key, value).commit();
    }

    public static float getFloat(Context context, String key, float defValue) {
        SharedPreferences mpref = context.getSharedPreferences(Constant.SHARED_KEY, Context.MODE_PRIVATE);
        float value = mpref.getFloat(key, defValue);
        return value;
    }
}
