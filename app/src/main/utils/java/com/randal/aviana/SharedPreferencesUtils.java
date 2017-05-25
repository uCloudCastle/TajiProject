package com.randal.aviana;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferencesUtils {

    private SharedPreferencesUtils(){
        throw new UnsupportedOperationException("DO NOT INSTANTIATE THIS CLASS");
    }

    private static final String FILE_NAME = "sp_file_name";

    /**
     * sava data to file
     *
     * @param context
     * @param key
     * @param data
     */
    public static void saveData(Context context, String key, Object data) {
        String type = data.getClass().getSimpleName();
        SharedPreferences sharedPreferences = context
                .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) data);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) data);
        } else if ("String".equals(type)) {
            editor.putString(key, (String) data);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) data);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) data);
        }
        editor.apply();
    }

    /**
     * get data from file
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static Object getData(Context context, String key, Object defValue) {
        String type = defValue.getClass().getSimpleName();
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (FILE_NAME, Context.MODE_PRIVATE);

        if ("Integer".equals(type)) {
            return sharedPreferences.getInt(key, (Integer) defValue);
        } else if ("Boolean".equals(type)) {
            return sharedPreferences.getBoolean(key, (Boolean) defValue);
        } else if ("String".equals(type)) {
            return sharedPreferences.getString(key, (String) defValue);
        } else if ("Float".equals(type)) {
            return sharedPreferences.getFloat(key, (Float) defValue);
        } else if ("Long".equals(type)) {
            return sharedPreferences.getLong(key, (Long) defValue);
        }
        return null;
    }

    /**
     * SP中清除所有数据
     */
    public void clear(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();
    }
}

