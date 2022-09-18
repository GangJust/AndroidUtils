package com.freegang.androidutils.text;


import java.util.ArrayList;

/// 字符串工具类
public class GTextUtils {

    private GTextUtils() {
        ///
    }

    /// 空判断
    public static <S extends CharSequence> boolean isEmpty(S text) {
        return text == null || isEmpty(text.toString());
    }

    public static boolean isEmpty(String value) {
        return value == null || to(value).isEmpty() || to(value).equals("null");
    }

    /// 空数组判断
    public static <S extends CharSequence> boolean isEmpties(S... texts) {
        if (texts.length == 0) return true;

        String[] strings = new String[texts.length];
        for (int i = 0; i < texts.length; i++) {
            strings[i] = texts[i].toString();
        }

        return isEmpties(strings);
    }

    public static boolean isEmpties(String... values) {
        if (values.length == 0) return false;

        ArrayList<Boolean> booleans = new ArrayList<>(); //Boolean列表, 当出现 false 后, 则表示这并不是一个空的字符数组
        for (String value : values) {
            booleans.add(isEmpty(value));
        }

        return !(booleans.contains(false)); //当不包含false, 表示整个数组都是空的
    }

    /// 非空判断
    public static <S extends CharSequence> boolean isNotEmpty(S text) {
        return !isEmpty(text);
    }

    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    /// 非空数组判断
    public static <S extends CharSequence> boolean isNotEmpties(S... text) {
        return !isEmpties(text);
    }

    public static boolean isNotEmpties(String... values) {
        return !isEmpties(values);
    }

    /// 字符包含比较, 与给定字符数组做匹配比较
    public static <S extends CharSequence> boolean contains(S text, String... comps) {
        if (text == null) return false;
        return contains(text.toString(), comps);
    }

    public static boolean contains(String value, String... comps) {
        if (value == null || comps == null || comps.length == 0) return false;

        ArrayList<Boolean> booleans = new ArrayList<>(); //Boolean列表, 当出现 false 后, 则表示这并不是完全匹配
        for (String comp : comps) {
            booleans.add(value.contains(comp));
        }

        return !(booleans.contains(false)); //不包含false, 表示全部匹配
    }

    /// 去掉字符串前后的空白字符
    public static <S extends CharSequence> String to(S text) {
        if (text == null) return "";
        return to(text.toString());
    }

    public static String to(String value) {
        if (value == null) return "";
        return value.trim();
    }

    /// 去掉字符串中的所有空白字符
    public static <S extends CharSequence> String toAll(S text) {
        if (text == null) return "";
        return toAll(text.toString());
    }

    public static String toAll(String value) {
        if (value == null) return "";
        return value.trim().replaceAll("\\s", "");
    }

    /// 字符转整型
    public static <S extends CharSequence> Integer toInt(S text) {
        return toInt(text.toString());
    }

    public static Integer toInt(String str) {
        return Integer.parseInt(to(str), 10);
    }

    public static Integer toInt(String str, int radix) {
        return Integer.parseInt(str, radix);
    }

    /// 字符转单精度小数
    public static <S extends CharSequence> Float toFloat(S text) {
        return toFloat(text.toString());
    }

    public static Float toFloat(String str) {
        return Float.parseFloat(to(str));
    }

    /// 字符双精度小数
    public static <S extends CharSequence> Double toDouble(S text) {
        return toDouble(text.toString());
    }

    public static Double toDouble(String str) {
        return Double.parseDouble(to(str));
    }
}