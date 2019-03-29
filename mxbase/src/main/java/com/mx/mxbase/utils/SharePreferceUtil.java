package com.mx.mxbase.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;

import java.util.Arrays;

/**
 * SharePreferce保存数据到内存中
 *
 * @author ChunfaLee(ly09219@gamil.com)
 * @date 2016-4-21 17:52:40
 */
@SuppressLint({"WorldReadableFiles", "WorldWriteableFiles"})
public class SharePreferceUtil {
    private static SharePreferceUtil tool = null;
    private SharedPreferences shareprefece;
    private SharedPreferences.Editor editor;

    /**
     * Construct
     */
    private SharePreferceUtil(Context context) {
        // Preferences对象
        shareprefece = context.getSharedPreferences("pactrera",
                Context.MODE_APPEND + Context.MODE_WORLD_READABLE
                        + Context.MODE_WORLD_WRITEABLE);
        editor = shareprefece.edit();
    }

    /**
     * 获取单例 Create at 2015-9-17
     *
     * @param context
     * @return SharePreferce
     * @author luomin
     */
    public static SharePreferceUtil getInstance(Context context) {
        if (tool == null) {
            tool = new SharePreferceUtil(context);
        }
        return tool;
    }

    public boolean isEmpty(String key) {
        return !shareprefece.contains(key);
    }

    /**
     * 清理缓存 Create at 2015-9-17
     */
    public void clearCache() {
        editor.clear();
        editor.commit();
    }

    /**
     * 设置SharedPrefere缓存 Create at 2015-9-17
     */
    public void setCache(String key, Object value) {
        if (value instanceof Boolean)// 布尔对象
            editor.putBoolean(key, (Boolean) value);
        else if (value instanceof String)// 字符串
            editor.putString(key, (String) value);
        else if (value instanceof Integer)// 整型数
            editor.putInt(key, (Integer) value);
        else if (value instanceof Long)// 长整型
            editor.putLong(key, (Long) value);
        else if (value instanceof Float)// 浮点数
            editor.putFloat(key, (Float) value);
        editor.commit();
    }

    /**
     * 保存数组
     *
     * @param key
     * @param strArray
     */
    public void saveStrArray(String key, String[] strArray) {
        JSONArray jsonArray = new JSONArray();
        for (String str : strArray) {
            jsonArray.put(str);
        }
        editor.putString(key, jsonArray.toString());
        editor.commit();
    }

    public String[] getStrArray(String key, int arrayLength) {
        String[] resArray = new String[arrayLength];
        Arrays.fill(resArray, true);
        try {
            JSONArray jsonArray = new JSONArray(shareprefece.getString(key, "[]"));
            for (int i = 0; i < jsonArray.length(); i++) {
                resArray[i] = jsonArray.getString(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resArray;
    }

    /**
     * 读取缓存中的字符串 Create at 2015-9-17
     *
     * @param key
     * @return String
     */
    public String getString(String key) {
        return shareprefece.getString(key, "");
    }

    /**
     * 读取缓存中的布尔型缓存 Create at 2015-9-17
     *
     * @param key
     * @return boolean
     */
    public boolean getBoolean(String key) {
        return shareprefece.getBoolean(key, false);
    }

    /**
     * 读取缓存中的整型数 Create at 2015-9-17
     *
     * @param key
     * @return int
     */
    public int getInt(String key) {
        return shareprefece.getInt(key, 0);
    }
    /**
     * 读取缓存中的整型数 Create at 2015-9-17
     *
     * @param key
     * @return int
     */
    public int getInt(String key,int def) {
        return shareprefece.getInt(key, def);
    }

    /**
     * 读取缓存中的长整型数 Create at 2015-9-17
     *
     * @param key
     * @return long
     */
    public long getLong(String key) {
        return shareprefece.getLong(key, 0);
    }

    /**
     * 读取缓存中的浮点数 Create at 2015-9-17
     *
     * @param key
     * @return float
     */
    public float getFloat(String key) {
        return shareprefece.getFloat(key, 0.0f);
    }

    /**
     * 判断是否有缓存
     *
     * @param string
     * @return
     */
    public boolean ifHaveShare(String string) {
        return shareprefece.contains(string);
    }

}
