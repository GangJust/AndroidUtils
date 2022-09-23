package com.freegang.androidutils.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * JsonUtils
 */
public class GJSONUtils {
    private GJSONUtils() {
        ///
    }

    //----------- 如果指定节点存在, 则返回, 构造返回, 给定的默认值 ----------------------//
    public static String getString(JSONObject jsonObject, String key) {
        return getString(jsonObject, key, "");
    }

    public static Boolean getBoolean(JSONObject jsonObject, String key) {
        return getBoolean(jsonObject, key, false);
    }

    public static Integer getInt(JSONObject jsonObject, String key) {
        return getInt(jsonObject, key, 0);
    }

    public static Long getLong(JSONObject jsonObject, String key) {
        return getLong(jsonObject, key, 0L);
    }

    public static Double getDouble(JSONObject jsonObject, String key) {
        return getDouble(jsonObject, key, 0.0);
    }

    public static Object get(JSONObject jsonObject, String key) {
        return get(jsonObject, key, null);
    }

    //----------- 如果指定节点存在, 则返回, 构造返回, 给定的默认值 ----------------------//
    public static String getString(JSONObject jsonObject, String key, String defValue) {
        if (hasKey(jsonObject, key)) return defValue;
        try {
            defValue = jsonObject.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public static Boolean getBoolean(JSONObject jsonObject, String key, Boolean defValue) {
        if (hasKey(jsonObject, key)) return defValue;
        try {
            defValue = jsonObject.getBoolean(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public static Integer getInt(JSONObject jsonObject, String key, Integer defValue) {
        if (hasKey(jsonObject, key)) return defValue;
        try {
            defValue = jsonObject.getInt(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public static Long getLong(JSONObject jsonObject, String key, Long defValue) {
        if (hasKey(jsonObject, key)) return defValue;
        try {
            defValue = jsonObject.getLong(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public static Double getDouble(JSONObject jsonObject, String key, Double defValue) {
        if (hasKey(jsonObject, key)) return defValue;
        try {
            defValue = jsonObject.getDouble(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public static Object get(JSONObject jsonObject, String key, Object defValue) {
        if (hasKey(jsonObject, key)) return defValue;
        try {
            defValue = jsonObject.get(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    //----------- 遍历某个Array节点 ----------------------//
    public static JSONObject[] getJSONArray(JSONObject jsonObject, String key) {
        if (hasKey(jsonObject, key)) return new JSONObject[0];

        JSONObject[] jsonObjects = new JSONObject[0];
        try {
            JSONArray array = jsonObject.getJSONArray(key);
            if (array.length() <= 0) return jsonObjects;

            jsonObjects = new JSONObject[array.length()];
            for (int i = 0; i < array.length(); i++) {
                jsonObjects[i] = array.getJSONObject(i);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObjects;
    }

    //----------- 判断某个节点是否存在 -------------------//
    public static boolean hasKey(JSONObject jsonObject, String key) {
        if (jsonObject == null) return false;
        return jsonObject.has(key);
    }
}
