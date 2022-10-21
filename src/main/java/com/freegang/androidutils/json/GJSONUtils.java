package com.freegang.androidutils.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.channels.OverlappingFileLockException;

/**
 * org.json
 * <p>
 * GJSONUtils
 */
public class GJSONUtils {
    private GJSONUtils() {
        ///
    }

    /**
     * 解析具有json格式的字符串
     *
     * @param jsonStr
     * @return
     */
    public static JSONObject parse(String jsonStr) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 解析具有jsonArray格式的字符串
     *
     * @param jsonStr
     * @return
     */
    public static JSONArray parseArray(String jsonStr) {
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = new JSONArray(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    /**
     * 将一个JSONObject转换为json格式的字符串
     *
     * @param jsonObject
     * @return
     */
    public static String toJson(JSONObject jsonObject) {
        return jsonObject.toString();
    }

    /**
     * 将一个JSONArray转换为jsonArray格式的字符串
     *
     * @param jsonArray
     * @return
     */
    public static String toJson(JSONArray jsonArray) {
        return jsonArray.toString();
    }

    //----------- 如果指定节点存在, 则返回该节点的值, 否则返回给定的默认值 -----------//
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

    public static Object getObject(JSONObject jsonObject, String key) {
        return getObject(jsonObject, key, null);
    }

    public static String getString(JSONObject jsonObject, String key, String defValue) {
        if (isNull(jsonObject, key)) return defValue;
        try {
            defValue = jsonObject.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public static Boolean getBoolean(JSONObject jsonObject, String key, Boolean defValue) {
        if (isNull(jsonObject, key)) return defValue;
        try {
            defValue = jsonObject.getBoolean(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public static Integer getInt(JSONObject jsonObject, String key, Integer defValue) {
        if (isNull(jsonObject, key)) return defValue;
        try {
            defValue = jsonObject.getInt(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public static Long getLong(JSONObject jsonObject, String key, Long defValue) {
        if (isNull(jsonObject, key)) return defValue;
        try {
            defValue = jsonObject.getLong(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public static Double getDouble(JSONObject jsonObject, String key, Double defValue) {
        if (isNull(jsonObject, key)) return defValue;
        try {
            defValue = jsonObject.getDouble(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public static Object getObject(JSONObject jsonObject, String key, Object defValue) {
        if (isNull(jsonObject, key)) return defValue;
        try {
            defValue = jsonObject.get(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    //----------- 如果指定Array的下标存在, 则返回该节点的值, 否则返回给定的默认值 -----------//
    public static String getString(JSONArray jsonArray, int index) {
        return getString(jsonArray, index, "");
    }

    public static Boolean getBoolean(JSONArray jsonArray, int index) {
        return getBoolean(jsonArray, index, false);
    }

    public static Integer getInt(JSONArray jsonArray, int index) {
        return getInt(jsonArray, index, 0);
    }

    public static Long getLong(JSONArray jsonArray, int index) {
        return getLong(jsonArray, index, 0L);
    }

    public static Double getDouble(JSONArray jsonArray, int index) {
        return getDouble(jsonArray, index, 0.0);
    }

    public static Object getObject(JSONArray jsonArray, int index) {
        return getObject(jsonArray, index, null);
    }

    public static String getString(JSONArray jsonArray, int index, String defValue) {
        if (jsonArray.length() == 0 || index >= jsonArray.length()) return defValue;
        try {
            defValue = jsonArray.getString(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public static Boolean getBoolean(JSONArray jsonArray, int index, Boolean defValue) {
        if (jsonArray.length() == 0 || index >= jsonArray.length()) return defValue;

        try {
            defValue = jsonArray.getBoolean(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public static Integer getInt(JSONArray jsonArray, int index, Integer defValue) {
        if (jsonArray.length() == 0 || index >= jsonArray.length()) return defValue;

        try {
            defValue = jsonArray.getInt(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public static Long getLong(JSONArray jsonArray, int index, Long defValue) {
        if (jsonArray.length() == 0 || index >= jsonArray.length()) return defValue;

        try {
            defValue = jsonArray.getLong(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public static Double getDouble(JSONArray jsonArray, int index, Double defValue) {
        if (jsonArray.length() == 0 || index >= jsonArray.length()) return defValue;

        try {
            defValue = jsonArray.getDouble(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public static Object getObject(JSONArray jsonArray, int index, Object defValue) {
        if (jsonArray.length() == 0 || index >= jsonArray.length()) return defValue;

        try {
            defValue = jsonArray.get(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    /**
     * 如果某个子节点存在, 则返回该子节点, 否则返null
     *
     * @param jsonObject
     * @param key
     * @return
     */
    public static JSONObject get(JSONObject jsonObject, String key) {
        if (isNull(jsonObject, key)) return new JSONObject();
        try {
            return jsonObject.getJSONObject(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    /**
     * 如果某个子节点存在, 则返回该子节点, 否则返null
     *
     * @param jsonArray
     * @param index
     * @return
     */
    public static JSONObject get(JSONArray jsonArray, int index) {
        if (jsonArray.length() == 0 || index >= jsonArray.length()) return new JSONObject();

        try {
            return jsonArray.getJSONObject(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    /**
     * 如果某个子节点存在, 则返回该子节点, 否则返null
     *
     * @param jsonObject
     * @param key
     * @return
     */
    public static JSONArray getArray(JSONObject jsonObject, String key) {
        if (isNull(jsonObject, key)) return new JSONArray();
        try {
            return jsonObject.getJSONArray(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    /**
     * 如果某个子节点存在, 则返回该子节点, 否则返null
     *
     * @param jsonArray
     * @param index
     * @return
     */
    public static JSONArray getArray(JSONArray jsonArray, int index) {
        if (jsonArray.length() == 0 || index >= jsonArray.length()) return new JSONArray();

        try {
            return jsonArray.getJSONArray(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new JSONArray();
    }

    /**
     * 将某个 JSONArray 直接打散成 JSONObject 数组
     *
     * @param jsonObject
     * @param key
     * @return
     */
    public static JSONObject[] getJSONArray(JSONObject jsonObject, String key) {
        if (isNull(jsonObject, key)) return new JSONObject[0];

        JSONObject[] jsonObjects = new JSONObject[0];
        try {
            return getJSONArray(jsonObject.getJSONArray(key));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObjects;
    }

    /**
     * 将某个 JSONArray 直接打散成 JSONObject 数组
     *
     * @param jsonArray
     * @return
     */
    public static JSONObject[] getJSONArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() == 0) return new JSONObject[0];

        JSONObject[] jsonObjects = new JSONObject[0];
        try {
            jsonObjects = new JSONObject[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObjects[i] = jsonArray.getJSONObject(i);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObjects;
    }

    /**
     * 获取某个key节点; keys 必须是 [从左到右] -> [从父到子] 的层级关系; 若某个key不存在, 则会直接返回 null, 该 keys 的最右边节点应该是一个 JSONObject
     *
     * @param jsonObject
     * @param keys
     * @return
     */
    public static JSONObject getUntil(JSONObject jsonObject, String... keys) {
        JSONObject resultJSON = jsonObject;

        for (String key : keys) {
            if (isNull(jsonObject, key)) return new JSONObject();
            try {
                resultJSON = resultJSON.getJSONObject(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return resultJSON;
    }

    /**
     * 获取某个key节点; keys 必须是 [从左到右] -> [从父到子] 的层级关系; 若某个key不存在, 则会直接返回 null, 该 keys 的最右边节点应该是一个 JSONArray
     *
     * @param jsonObject
     * @param keys
     * @return
     */
    public static JSONArray getArrayUntil(JSONObject jsonObject, String... keys) {
        JSONObject resultJSON = jsonObject;

        try {
            for (int i = 0; i < keys.length - 1; i++) {
                if (isNull(jsonObject, keys[i])) return new JSONArray();
                resultJSON = resultJSON.getJSONObject(keys[i]);
            }

            return resultJSON.getJSONArray(keys[keys.length - 1]);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new JSONArray();
    }

    /**
     * 判断某个节点是否存在
     *
     * @param jsonObject
     * @param key
     * @return
     */
    public static boolean hasKey(JSONObject jsonObject, String key) {
        if (jsonObject == null) return false;
        return jsonObject.has(key);
    }

    /**
     * 判断某个节点是否存在, 并且值是否为 null
     *
     * @param jsonObject
     * @param key
     * @return
     */
    public static boolean isNull(JSONObject jsonObject, String key) {
        if (jsonObject == null) return true;
        return jsonObject.isNull(key);
    }
}
