package com.freegang.androidutils.text;


import java.util.ArrayList;
import java.util.Random;

/// 字符串工具类
public class GTextUtils {

    private GTextUtils() {
        ///
    }

    //英文字母表
    public static final String alphabets = "abcdefghijklmnopqrstuvwxyz";

    //字母、数字、符号表
    public static final String charTable = "abcdefghijklmnopqrstuvwxyz0123456789~!@#$%^&*()_+[]{}:'\"\\|,<.>/?";

    /// 空判断
    public static <S extends CharSequence> boolean isEmpty(S text) {
        return text == null || isEmpty(text.toString());
    }

    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty() || value.trim().equals("null");
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

    /// 字符完全包含, 与给定字符串数组做匹配比较, 如果每项都包含, 则表示完全包含.
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

    /// 字符任意包含m 与给定字符串数组做匹配比较, 如果其中一项包含, 这表示匹配成功
    public static <S extends CharSequence> boolean anyContains(S text, String... comps) {
        if (text == null) return false;
        return anyContains(text.toString(), comps);
    }

    public static boolean anyContains(String value, String... comps) {
        if (value == null || comps == null || comps.length == 0) return false;
        for (String comp : comps) {
            if (value.contains(comp)) return true;
        }
        return false;
    }

    /// 字符比较, 与给定字符串数组做比较, 如果有任意一项匹配, 都表示匹配成功.
    public static <S extends CharSequence> boolean anyEquals(S value, String... equals) {
        if (value == null) return false;
        return anyEquals(value.toString(), equals);
    }

    public static boolean anyEquals(String value, String... equals) {
        if (value == null || equals == null || equals.length == 0) return false;
        for (String e : equals) {
            if (value.equals(e)) return true;
        }
        return false;
    }

    /// 判断某个字符串是否是全空白
    public static <S extends CharSequence> boolean isSpace(S text) {
        if (isEmpty(text)) return true;
        return isSpace(text.toString());
    }

    public static boolean isSpace(String value) {
        if (isEmpty(value)) return true;

        char[] chars = value.toCharArray();
        ArrayList<Boolean> booleans = new ArrayList<>();
        for (char c : chars) {
            booleans.add(Character.isWhitespace(c));
        }

        return !(booleans.contains(false)); //不包含false
    }

    /// 去掉字符串前后的空白字符
    public static <S extends CharSequence> String to(S text) {
        if (isEmpty(text)) return "";
        return to(text.toString());
    }

    public static String to(String value) {
        if (isEmpty(value)) return "";
        return value.trim();
    }

    /// 去掉字符串中的所有空白字符
    public static <S extends CharSequence> String toAll(S text) {
        if (isEmpty(text)) return "";
        return toAll(text.toString());
    }

    public static String toAll(String value) {
        if (isEmpty(value)) return "";
        return value.trim().replaceAll("\\s", "");
    }

    /// 如果某个字符为空, 则返回给定默认值
    public static <S extends CharSequence> String get(S maybeNullValue, String defaultValue) {
        if (maybeNullValue == null) return defaultValue;
        return get(maybeNullValue.toString(), defaultValue);
    }

    public static String get(String maybeNullValue, String defaultValue) {
        if (isEmpty(maybeNullValue)) return defaultValue;
        return maybeNullValue;
    }

    /// 截取某字符串前面的所有内容, 成功返回操作后的值, 失败返回它本身
    public static <S extends CharSequence> String front(S value, String target) {
        if (value == null) return "";
        return front(value.toString(), target);
    }

    public static String front(String value, String target) {
        if (isEmpty(value) || isEmpty(target)) return "";
        if (!value.contains(target)) return value;
        return value.substring(0, value.indexOf(target));
    }

    /// 截取某字符串后面的所有内容, 成功返回操作后的值, 失败返回它本身
    public static <S extends CharSequence> String behind(S value, String target) {
        if (value == null) return "";
        return behind(value.toString(), target);
    }

    public static String behind(String value, String target) {
        if (isEmpty(value) || isEmpty(target)) return "";
        if (!value.contains(target)) return value;
        return value.substring(value.indexOf(target) + target.length());
    }

    /// 截取某字符串中间的内容, 成功返回操作后的值, 失败返回它本身
    public static <S extends CharSequence> String middle(S value, String start, String end) {
        if (value == null) return "";
        return middle(value.toString(), start, end);
    }

    public static String middle(String value, String start, String end) {
        if (isEmpty(value) || isEmpty(start) || isEmpty(end)) return value;

        //如果前置包含, 则截取后段
        if (value.contains(start)) {
            value = value.substring(value.indexOf(start) + start.length());
        }
        //如果后置包含, 则截取前段
        if (value.contains(end)) {
            value = value.substring(0, value.indexOf(end));
        }
        return value;
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

    // ------------More------------ //

    /// 某个字符串不足最小指定长度, 左填充指定字符
    public static String padLeft(String value, int minLength, char pad) {
        if (minLength <= 0) throw new IllegalArgumentException("need: `minLength > 0`, current: minLength = " + minLength);
        if (value.length() >= minLength) return value;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < minLength - value.length(); i++) builder.append(pad);
        builder.append(value);
        return builder.toString();
    }

    /// 某个字符串不足最小指定长度, 右填充指定字符
    public static String padRight(String value, int minLength, String pad) {
        if (minLength <= 0) throw new IllegalArgumentException("need: `minLength > 0`, current: minLength = " + minLength);
        if (value.length() >= minLength) return value;
        StringBuilder builder = new StringBuilder();
        builder.append(value);
        for (int i = 0; i < minLength - value.length(); i++) builder.append(pad);
        return builder.toString();
    }

    /// 获取随机英文字母组合
    public static String randomAlphabet() {
        return randomAlphabet(new Random());
    }

    public static String randomAlphabet(Random random) {
        int length = random.nextInt();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            //随机大小写
            if (random.nextInt() % random.nextInt() != 0) {
                builder.append(alphabets.charAt(i));
            } else {
                builder.append((char) (alphabets.codePointAt(i) - 32));
            }
        }
        return builder.toString();
    }

    /// 从指定样本文本中, 获取随机文本
    public static String randomText(String sample, int length) {
        return randomText(sample, length, new Random());
    }

    public static String randomText(String sample, int length, Random random) {
        StringBuilder builder = new StringBuilder();
        char[] chars = sample.toCharArray();
        for (int i = 0; i < length; i++) {
            builder.append(chars[random.nextInt(chars.length)]);
        }
        return builder.toString();
    }

    /// 字符串数组随机获取
    public static String random(String[] strings) {
        return random(strings, new Random());
    }

    public static String random(String[] strings, Random random) {
        return strings[random.nextInt(strings.length)];
    }

    /// unicode编码, Example: 中国
    public static String enUnicode(String unicode) {
        StringBuilder builder = new StringBuilder();
        char[] chars = unicode.toCharArray();
        for (char c : chars) {
            String hexString = Integer.toHexString(c);
            hexString = "\\u" + padLeft(hexString, 4, '0');
            builder.append(hexString);
        }
        return builder.toString();
    }

    /// unicode解码, Example: \u4e2d\u56fd
    public static String deUnicode(String unicode) {
        StringBuilder builder = new StringBuilder();
        String[] split = unicode.split("\\\\u");
        for (int i = 1; i < split.length; i++) {
            String target = padLeft(split[i], 4, '0');
            builder.append((char) Integer.parseInt(target, 16));
        }
        return builder.toString();
    }
}