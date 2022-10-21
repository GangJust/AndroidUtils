package com.freegang.androidutils.collection;

import java.util.HashMap;
import java.util.Map;

public class GCollectionUtils {
    private GCollectionUtils() {
        ///
    }

    /**
     * 将具有map简单格式的字符串转成map
     * 如果是复杂格式, 不建议使用
     *
     * @param mapText 举例: "{key1=李四, key2=王五}"
     * @return
     */
    public static Map<String, String> parseMap(String mapText) {
        return parseMap(mapText, "=");
    }

    /**
     * 将具有map简单格式的字符串转成map
     * 如果是复杂格式, 不建议使用
     *
     * @param mapText 举例: "{key1=李四, key2=王五}"
     * @return
     */
    public static Map<String, String> parseMap(String mapText, String kvSymbol) {
        Map<String, String> resultMap = new HashMap<>();
        mapText = mapText.substring(1, mapText.length() - 1);
        String[] split = mapText.split(",");
        for (String s : split) {
            String trimItem = s.trim();
            String[] keyValue = trimItem.split(kvSymbol);
            if (keyValue.length == 2) {
                resultMap.put(keyValue[0], keyValue[1]);
            } else {
                resultMap.put(keyValue[0], "");
            }
        }
        return resultMap;
    }
}
