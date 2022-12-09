package com.freegang.androidutils.net;

import java.net.URLEncoder;
import java.util.Map;

public class GURLPramsUtils {
	
    private GURLPramsUtils() {
        ///
    }

    /**
     * URL拼接, 通常用于GET请求
     *
     * @param baseUrl url
     * @param map     参数列表
     * @return
     */
    public static String urlSplicing(String baseUrl, Map<String, String> map) {
        return urlSplicing(baseUrl, map, true);
    }

    /**
     * URL拼接, 通常用于GET请求
     *
     * @param baseUrl      url
     * @param map          参数列表
     * @param needEncoding 是否需要URL编码
     * @return
     */
    public static String urlSplicing(String baseUrl, Map<String, String> map, boolean needEncoding) {
        if (baseUrl.lastIndexOf("?") == -1) baseUrl += "?";
        return baseUrl + (needEncoding ? encoding(map) : original(map));
    }

    /**
     * Url参数编码, 通常用于GET请求
     *
     * @param map 参数列表
     * @return
     */
    public static String encoding(Map<String, String> map) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue())).append("&");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    /**
     * 将Map转换为URL参数类型, 需要注意的是该参数作为原始参数, 并未 encoding
     *
     * @param map 参数列表
     * @return
     */
    public static String original(Map<String, String> map) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}
