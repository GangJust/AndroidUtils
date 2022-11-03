package com.yys.utils.net;

import java.net.URLEncoder;
import java.util.Map;

public class GUrlPramsUtils {
    private GUrlPramsUtils() {
        ///
    }

    /**
     * URL拼接, 通常用于GET请求
     *
     * @param baseUrl
     * @param map
     * @return
     */
    public static String urlSplicing(String baseUrl, Map<String, String> map) {
        if (baseUrl.lastIndexOf("?") == -1) baseUrl += "?";
        return baseUrl + encoding(map);
    }

    /**
     * Url参数编码, 通常用于GET请求
     *
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
}
