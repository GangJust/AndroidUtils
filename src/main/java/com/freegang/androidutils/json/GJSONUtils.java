package com.freegang.utils.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.channels.OverlappingFileLockException;

/**
 * JsonUtils
 */
public class GJSONUtils {
    private GJSONUtils() {
        ///
    }

    public static JSONObject parse(String jsonStr) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONArray parseArray(String jsonStr) {
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = new JSONArray(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public static String toJson(JSONObject jsonObject) {
        return jsonObject.toString();
    }

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
        if (jsonArray.length() == 0) return defValue;
        try {
            defValue = jsonArray.getString(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public static Boolean getBoolean(JSONArray jsonArray, int index, Boolean defValue) {
        if (jsonArray.length() == 0) return defValue;

        try {
            defValue = jsonArray.getBoolean(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public static Integer getInt(JSONArray jsonArray, int index, Integer defValue) {
        if (jsonArray.length() == 0) return defValue;

        try {
            defValue = jsonArray.getInt(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public static Long getLong(JSONArray jsonArray, int index, Long defValue) {
        if (jsonArray.length() == 0) return defValue;

        try {
            defValue = jsonArray.getLong(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public static Double getDouble(JSONArray jsonArray, int index, Double defValue) {
        if (jsonArray.length() == 0) return defValue;

        try {
            defValue = jsonArray.getDouble(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public static Object getObject(JSONArray jsonArray, int index, Object defValue) {
        if (jsonArray.length() == 0) return defValue;

        try {
            defValue = jsonArray.get(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    //----------- 如果某个子节点存在, 则返回该子节点, 否则返null -----------//
    public static JSONObject get(JSONObject jsonObject, String key) {
        if (isNull(jsonObject, key)) return new JSONObject();
        try {
            return jsonObject.getJSONObject(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public static JSONObject get(JSONArray jsonArray, int index) {
        if (jsonArray.length() == 0) return new JSONObject();

        try {
            return jsonArray.getJSONObject(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public static JSONArray getArray(JSONObject jsonObject, String key) {
        if (isNull(jsonObject, key)) return new JSONArray();
        try {
            return jsonObject.getJSONArray(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public static JSONArray getArray(JSONArray jsonArray, int index) {
        if (jsonArray.length() == 0) return new JSONArray();

        try {
            return jsonArray.getJSONArray(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new JSONArray();
    }

    //----------- 将某个 JSONArray 直接打散成 JSONObject 数组 -----------//
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

    //获取某个key节点; keys 必须是 [从左到右] -> [从父到子] 的层级关系; 若某个key不存在, 则会直接返回 null, 该 keys 的最右边节点应该是一个 JSONObject
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

    //获取某个key节点; keys 必须是 [从左到右] -> [从父到子] 的层级关系; 若某个key不存在, 则会直接返回 null, 该 keys 的最右边节点应该是一个 JSONArray
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

        return null;
    }

    //----------- 判断某个节点是否存在 -----------//
    public static boolean hasKey(JSONObject jsonObject, String key) {
        if (jsonObject == null) return false;
        return jsonObject.has(key);
    }

    //----------- 判断某个节点是否存在, 并且值是否为 null -----------//
    public static boolean isNull(JSONObject jsonObject, String key) {
        if (jsonObject == null) return false;
        return jsonObject.isNull(key);
    }

    //
    public static class Factory {
        private final JSONObject mJsonObject;

        private JSONObject jsonObject;

        public Factory(JSONObject jsonObject) {
            this.mJsonObject = jsonObject;
            this.jsonObject = jsonObject;
        }

        /**
         * @param key 节点名
         * @return 返回该[key]节点项, JSONObject 需要注意的是该项应该是 JSONObject 而不是一个value
         */
        public Factory get(String key) {
            if (isNull(jsonObject, key)) throw new NullPointerException("`" + key + "` non existent!");

            try {
                jsonObject = jsonObject.getJSONObject(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }

        /**
         * @param key   节点名
         * @param index 数组下标
         * @return 返回该[key]节点数组下的第[index]项, 需要注意的是该项应该是 JSONObject 而不是一个value
         * @throws IndexOutOfBoundsException
         */
        public Factory get(String key, int index) {
            if (isNull(jsonObject, key)) throw new NullPointerException("`" + key + "` non existent!");

            try {
                JSONArray jsonArray = jsonObject.getJSONArray(key);
                if (index >= jsonArray.length() || index < 0) {
                    throw new IndexOutOfBoundsException("`" + index + "` out of bounds, current length: " + jsonArray.length());
                }
                jsonObject = jsonArray.getJSONObject(index);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }

        public JSONObject originalJSONObject() {
            return mJsonObject;
        }

        public JSONObject finalJSONObject(String key) {
            if (isNull(jsonObject, key)) return new JSONObject();

            try {
                return jsonObject.getJSONObject(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        public JSONArray finalJSONArray(String key) {
            if (isNull(jsonObject, key)) return new JSONArray();

            try {
                return jsonObject.getJSONArray(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

    }
}
