package com.freegang.androidutils.text;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

    //默认随机数种子
    public static final Random defaultRandom = new Random();

    public interface TextOperatorFunction {
        String operator(String str1, String str2);
    }

    /**
     * 空字符串判断
     *
     * @param text 被操作的字符串
     * @param <S>  extends CharSequence
     * @return String
     */
    public static <S extends CharSequence> boolean isEmpty(S text) {
        return text == null || isEmpty(text.toString());
    }

    public static boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty() || text.trim().equals("null");
    }

    /**
     * 空字符串数组判断
     *
     * @param texts 被操作的字符串
     * @param <S>   extends CharSequence
     * @return String
     */
    public static <S extends CharSequence> boolean isEmpties(S... texts) {
        if (texts.length == 0) return true;

        String[] values = new String[texts.length];
        for (int i = 0; i < texts.length; i++) {
            values[i] = texts[i].toString();
        }

        return isEmpties(values);
    }

    public static boolean isEmpties(String... values) {
        if (values.length == 0) return false;

        ArrayList<Boolean> booleans = new ArrayList<>(); //Boolean列表, 当出现 false 后, 则表示这并不是一个空的字符数组
        for (String text : values) {
            booleans.add(isEmpty(text));
        }

        return !(booleans.contains(false)); //当不包含false, 表示整个数组都是空的
    }

    /**
     * 非空字符串判断
     *
     * @param text 被操作的字符串
     * @param <S>  extends CharSequence
     * @return String
     */
    public static <S extends CharSequence> boolean isNotEmpty(S text) {
        return !isEmpty(text);
    }

    public static boolean isNotEmpty(String text) {
        return !isEmpty(text);
    }

    /**
     * 非空字符串数组判断
     *
     * @param text 被操作的字符串
     * @param <S>  extends CharSequence
     * @return String
     */
    public static <S extends CharSequence> boolean isNotEmpties(S... text) {
        return !isEmpties(text);
    }

    public static boolean isNotEmpties(String... values) {
        return !isEmpties(values);
    }

    /**
     * 判断某个字符串是否是全空白
     *
     * @param text 被操作的字符串
     * @param <S>  extends CharSequence
     * @return String
     */
    public static <S extends CharSequence> boolean isSpace(S text) {
        if (isEmpty(text)) return true;
        return isSpace(text.toString());
    }

    public static boolean isSpace(String text) {
        if (isEmpty(text)) return true;

        char[] chars = text.toCharArray();
        ArrayList<Boolean> booleans = new ArrayList<>();
        for (char c : chars) {
            booleans.add(Character.isWhitespace(c));
        }

        return !(booleans.contains(false)); //不包含false
    }

    /**
     * 字符完全包含, 与给定字符串数组做匹配比较, 如果每项都包含, 则表示完全包含.
     *
     * @param text  被操作的字符串
     * @param comps 被比较的字符串数组
     * @param <S>   extends CharSequence
     * @return String
     */
    public static <S extends CharSequence> boolean contains(S text, String... comps) {
        if (text == null) return false;
        return contains(text.toString(), comps);
    }

    public static boolean contains(String text, String... comps) {
        if (text == null || comps == null || comps.length == 0) return false;

        ArrayList<Boolean> booleans = new ArrayList<>(); //Boolean列表, 当出现 false 后, 则表示这并不是完全匹配
        for (String comp : comps) {
            booleans.add(text.contains(comp));
        }

        return !(booleans.contains(false)); //不包含false, 表示全部匹配
    }

    /**
     * 字符任意包含, 与给定字符串数组做匹配比较, 如果其中一项包含, 这表示匹配成功
     *
     * @param text  被操作的字符串
     * @param comps 被比较的字符串数组
     * @param <S>   extends CharSequence
     * @return String
     */
    public static <S extends CharSequence> boolean anyContains(S text, String... comps) {
        if (text == null) return false;
        return anyContains(text.toString(), comps);
    }

    public static boolean anyContains(String text, String... comps) {
        if (text == null || comps == null || comps.length == 0) return false;
        for (String comp : comps) {
            if (text.contains(comp)) return true;
        }
        return false;
    }

    /**
     * 字符比较, 与给定字符串数组做比较, 如果有任意一项匹配, 都表示匹配成功.
     *
     * @param text  被操作的字符串
     * @param equals 被比较的字符串
     * @param <S>    extends CharSequence
     * @return String
     */
    public static <S extends CharSequence> boolean anyEquals(S text, String... equals) {
        if (text == null) return false;
        return anyEquals(text.toString(), equals);
    }

    public static boolean anyEquals(String text, String... equals) {
        if (text == null || equals == null || equals.length == 0) return false;
        for (String e : equals) {
            if (text.equals(e)) return true;
        }
        return false;
    }

    /**
     * 去掉字符串前后的空白字符
     *
     * @param text 被操作的字符串
     * @param <S>  extends CharSequence
     * @return String
     */
    public static <S extends CharSequence> String to(S text) {
        if (isEmpty(text)) return "";
        return to(text.toString());
    }

    public static String to(String text) {
        if (isEmpty(text)) return "";
        return text.trim();
    }

    /**
     * 去掉字符串中的所有空白字符
     *
     * @param text 被操作的字符串
     * @param <S>  extends CharSequence
     * @return String
     */
    public static <S extends CharSequence> String toAll(S text) {
        if (isEmpty(text)) return "";
        return toAll(text.toString());
    }

    public static String toAll(String text) {
        if (isEmpty(text)) return "";
        return text.trim().replaceAll("\\s", "");
    }


    /**
     * 如果某个字符为空, 则返回给定默认字符串
     *
     * @param maybeNullValue 被操作的字符串
     * @param defaultValue   默认值
     * @param <S>            extends CharSequence
     * @return String
     */
    public static <S extends CharSequence> String get(S maybeNullValue, String defaultValue) {
        if (maybeNullValue == null) return defaultValue;
        return get(maybeNullValue.toString(), defaultValue);
    }

    public static String get(String maybeNullValue, String defaultValue) {
        if (isEmpty(maybeNullValue)) return defaultValue;
        return maybeNullValue;
    }

    /**
     * 如果某个对象为空, 则返回给定的默认字符串, 否则调用它的toString()
     *
     * @param maybeNull    被操作的字符串
     * @param defaultValue 默认值
     * @return String
     */
    public static String get(Object maybeNull, String defaultValue) {
        if (maybeNull == null) return defaultValue;
        return maybeNull.toString();
    }

    /**
     * 截取某字符串前面的所有内容, 成功返回操作后的值, 失败返回它本身
     *
     * @param text  被操作的字符串
     * @param target 被匹配的字符串
     * @param <S>    extends CharSequence
     * @return target之前的所有内容
     */
    public static <S extends CharSequence> String front(S text, String target) {
        if (text == null) return "";
        return front(text.toString(), target);
    }

    public static String front(String text, String target) {
        if (isEmpty(text) || isEmpty(target)) return "";
        if (!text.contains(target)) return text;
        return text.substring(0, text.indexOf(target));
    }

    /**
     * 截取字符串样例中的某个出现的字符串后面的所有内容, 成功返回操作后的值, 失败返回它本身
     *
     * @param text  被操作的字符串
     * @param target 被匹配的字符串
     * @param <S>    extends CharSequence
     * @return target之后的所有内容
     */
    public static <S extends CharSequence> String after(S text, String target) {
        if (text == null) return "";
        return after(text.toString(), target);
    }

    public static String after(String text, String target) {
        if (isEmpty(text) || isEmpty(target)) return "";
        if (!text.contains(target)) return text;
        return text.substring(text.indexOf(target) + target.length());
    }

    /**
     * 截取某字符串中间的内容, 成功返回操作后的值, 失败返回它本身
     *
     * @param text 被操作的字符串
     * @param start 开始的字符串
     * @param end   结束的字符串
     * @param <S>   extends CharSequence
     * @return String
     */
    public static <S extends CharSequence> String middle(S text, String start, String end) {
        if (text == null) return "";
        return middle(text.toString(), start, end);
    }

    public static String middle(String text, String start, String end) {
        if (isEmpty(text) || isEmpty(start) || isEmpty(end)) return text;

        //如果前置包含, 则截取后段
        if (text.contains(start)) {
            text = text.substring(text.indexOf(start) + start.length());
        }
        //如果后置包含, 则截取前段
        if (text.contains(end)) {
            text = text.substring(0, text.indexOf(end));
        }
        return text;
    }

    /**
     * 字符转整型
     *
     * @param text 被操作的字符串
     * @param <S>  extends CharSequence
     * @return Integer
     */
    public static <S extends CharSequence> Integer toInt(S text) {
        if (text == null) return 0;
        return toInt(text.toString());
    }

    public static Integer toInt(String str) {
        if (isEmpty(str)) return 0;
        return Integer.parseInt(str, 10);
    }

    public static <S extends CharSequence> Integer toInt(S text, int radix) {
        if (text == null) return 0;
        return toInt(text.toString(), radix);
    }

    public static Integer toInt(String str, int radix) {
        if (isEmpty(str)) return 0;
        return Integer.parseInt(str, radix);
    }

    /**
     * 字符转单精度小数
     *
     * @param text 被操作的字符串
     * @param <S>  extends CharSequence
     * @return Float
     */
    public static <S extends CharSequence> Float toFloat(S text) {
        if (text == null) return 0.0f;
        return toFloat(text.toString());
    }

    public static Float toFloat(String str) {
        if (isEmpty(str)) return 0.0f;
        return Float.parseFloat(str);
    }

    /**
     * 字符双精度小数
     *
     * @param text 被操作的字符串
     * @param <S>  extends CharSequence
     * @return Double
     */
    public static <S extends CharSequence> Double toDouble(S text) {
        if (text == null) return 0.0;
        return toDouble(text.toString());
    }

    public static Double toDouble(String str) {
        if (isEmpty(str)) return 0.0;
        return Double.parseDouble(str);
    }

    // ------------More------------ //

    /**
     * 对字符串数组中的前、后项进行遍历输出, 可以通过 operator 对其做相应运算
     * <p>
     * 例1：取出字符串数组中最大长度字符串
     * GTextUtils.reduce(new String[]{"123", "12"}, (str1, str2) -> str1.length() > str2.length() ? str1 : str2)
     * 例2: 将字符串数组合并为一个新的字符串, 类似于: GTextUtils#splicing
     * GTextUtils.reduce(new String[]{"123", "12"}, (str1, str2) -> str1.concat(str2));
     *
     * @param values   被操作的字符数组
     * @param operator 运算表达式
     * @return String
     */
    public static String reduce(String[] values, TextOperatorFunction operator) {
        if (values == null || values.length == 0) return "";

        String result = values[0];
        for (int i = 0; i < values.length - 1; i++) {
            result = operator.operator(values[i], values[i + 1]);
        }
        return result;
    }

    /**
     * 将一个字符串数组拼接成一个字符串, 用指定文本分割, 若操作失败, 则返回 "" 字符串
     *
     * @param values ["a", "b", "s"]
     * @param s       `!`
     * @return "a!b!s"
     */
    public static String splicing(String[] values, String s) {
        return splicing(new ArrayList<>(Arrays.asList(values)), s);
    }

    public static String splicing(Collection<String> values, String s) {
        if (values == null || values.isEmpty()) return "";

        StringBuilder builder = new StringBuilder();
        for (String string : values) {
            builder.append(string).append(s);
        }
        int start = builder.length() - s.length();
        int end = builder.length();
        builder.delete(start, end);
        return builder.toString();
    }

    /**
     * 某个字符串不足最小指定长度, 左填充指定字符
     *
     * @param text     "Hello"
     * @param minLength 8
     * @param pad       '0'
     * @return "000Hello"
     */
    public static String padLeft(String text, int minLength, char pad) {
        if (minLength <= 0) throw new IllegalArgumentException("need: `minLength > 0`, current: minLength = " + minLength);
        if (text.length() >= minLength) return text;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < minLength - text.length(); i++) builder.append(pad);
        builder.append(text);
        return builder.toString();
    }

    /**
     * 某个字符串不足最小指定长度, 右填充指定字符
     *
     * @param text     "Hello"
     * @param minLength 8
     * @param pad       '0'
     * @return "Hello000"
     */
    public static String padRight(String text, int minLength, char pad) {
        if (minLength <= 0) throw new IllegalArgumentException("need: `minLength > 0`, current: minLength = " + minLength);
        if (text.length() >= minLength) return text;
        StringBuilder builder = new StringBuilder();
        builder.append(text);
        for (int i = 0; i < minLength - text.length(); i++) builder.append(pad);
        return builder.toString();
    }

    /**
     * 获取随机符号表组合
     *
     * @return 从符号表中随机返回任意长度的乱序字符串
     */
    public static String randomCharTable() {
        return randomCharTable(defaultRandom);
    }

    public static String randomCharTable(Random random) {
        int length = random.nextInt(charTable.length());
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            //随机大小写
            char c = charTable.charAt(i);
            if (random.nextInt() % random.nextInt() != 0) {
                builder.append(c);
            } else {
                if (c >= 'a' && c <= 'z') {
                    builder.append((char) (c - 32));
                } else {
                    builder.append(c);
                }
            }
        }
        return builder.toString();
    }

    /**
     * 从指定样本文本中, 获取随机文本
     *
     * @param sample "abcdef"
     * @param length 3
     * @return "aba" or "abd" or ....
     */
    public static String randomText(String sample, int length) {
        return randomText(sample, length, defaultRandom);
    }

    public static String randomText(String sample, int length, Random random) {
        StringBuilder builder = new StringBuilder();
        char[] chars = sample.toCharArray();
        for (int i = 0; i < length; i++) {
            builder.append(chars[random.nextInt(chars.length)]);
        }
        return builder.toString();
    }

    /**
     * 字符串数组随机获取
     *
     * @param values "abcd"
     * @return 随机从 abcd 四个字母中返回随机的字母
     */
    public static String random(String[] values) {
        return random(values, defaultRandom);
    }

    public static String random(String[] values, Random random) {
        return values[random.nextInt(values.length)];
    }

    /**
     * unicode编码
     *
     * @param unicode "中国"
     * @return "\u4e2d\u56fd"
     */
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

    /**
     * unicode解码
     *
     * @param unicode "\u4e2d\u56fd"
     * @return "中国"
     */
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