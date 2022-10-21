package com.freegang.androidutils.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author zqgan
 * @since 2018/9/1
 * 日期时间处理相关类
 **/

public class GDateUtil {

    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String YYYY_MM_DD_HH_MM_CHINESE = "yyyy年MM月dd日 HH点mm分";
    public static final String YYYY_MM_DD_CHINESE = "yyyy年MM月dd日";
    public static final String MM_DD_CHINESE = "MM月dd日";

    private static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    private static final String CHINA_TIME_ZONE = "Asia/Shanghai"; //上海时区

    public static final long MILLISECONDS_FOR_ONE_MINUTE = 60 * 1000;
    public static final long MILLISECONDS_FOR_ONE_HOUR = 60 * MILLISECONDS_FOR_ONE_MINUTE;
    public static final long MILLISECONDS_FOR_ONE_DAY = 24 * MILLISECONDS_FOR_ONE_HOUR;

    /**
     * 将Date转成Calendar
     */
    public static Calendar toCalendar(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;
    }

    /**
     * 日期转指定格式的时间, 默认中国标准时间
     *
     * @param date
     * @param format
     * @return
     */
    public static String dateToString(Date date, String format) {
        return dateToString(date, format, CHINA_TIME_ZONE);
    }

    public static String dateToString(Date date, String format, String timeZone) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        try {
            return dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将String转成Date，默认时区东八区，TimeZone.getTimeZone("Asia/Shanghai")
     *
     * @param dateStr 含格式的时间字符串串
     * @return Date
     */
    public static Date stringToDate(String dateStr) {
        return stringToDate(dateStr, "Asia/Shanghai");
    }

    /**
     * 将String转成Date，默认时区东八区，TimeZone.getTimeZone("Asia/Shanghai")
     *
     * @param dateStr  含格式的时间字符串串
     * @param timeZone 合格的时区格式
     * @return Date
     */
    public static Date stringToDate(String dateStr, String timeZone) {
        SimpleDateFormat format = null;
        if (dateStr.contains("T")) {
            // 2022-08-12T00:00:00
            format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
            // 2022/08/12T00:00:00
            if (dateStr.contains("/")) format = new SimpleDateFormat("yyyy/MM/dd'T'hh:mm:ss");
        } else if (dateStr.contains("/")) {
            format = new SimpleDateFormat("yyyy/MM/dd");
            if (dateStr.contains(":") && dateStr.contains(" ")) format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        } else if (dateStr.contains("-")) {
            format = new SimpleDateFormat(YYYY_MM_DD);
            if (dateStr.contains(":") && dateStr.contains(" ")) format = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        } else if (dateStr.contains("年") && dateStr.contains("月") && dateStr.contains("日")) {
            format = new SimpleDateFormat(YYYY_MM_DD_CHINESE);
        } else if (!dateStr.contains("年") && dateStr.contains("月") && dateStr.contains("日")) {
            format = new SimpleDateFormat(MM_DD_CHINESE);
        }
        if (format == null) {
            return null;
        }
        format.setTimeZone(TimeZone.getTimeZone(timeZone));
        try {
            return format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将某个日期格式化为[年-月-日]的格式
     *
     * @param date
     * @return
     */
    public static String dateToShortDateString(Date date) {
        return dateToString(date, "yyyy-MM-dd");
    }

    /**
     * 将指定具有日期格式的字符转换成所给定日期格式的字符串
     *
     * @param dateStr 具有日期格式的字符串
     * @param format  指定日期格式
     * @return 新的日期格式字符串
     */
    public static String formatDateString(String dateStr, String format) {
        return dateToString(stringToDate(dateStr), format);
    }

    /**
     * 全站时间展示规范
     * 1分钟内：刚刚
     * 超过1分钟并在1小时内：某分钟前 （1分钟前）
     * 超过1小时并在当日内：某小时前（1小时前）
     * 昨天：昨天 + 小时分钟（昨天 08:30）
     * 昨天之前并在当年内：某月某日 + 小时分钟（1月1日 08:30）
     * 隔年：某年某月某日 + 小时分钟（2015年1月1日 08:30）
     */
    public static String dateToVoString(Date date) {
        Date now = new Date();
        long deltaMilliSeconds = now.getTime() - date.getTime();
        Calendar dateCalendar = toCalendar(date);
        Calendar nowCalendar = toCalendar(now);

        if (nowCalendar.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR)) {
            if (isSameDay(date, now)) {
                if (deltaMilliSeconds < MILLISECONDS_FOR_ONE_MINUTE) {
                    return "刚刚";
                } else if (deltaMilliSeconds < MILLISECONDS_FOR_ONE_HOUR) {
                    return String.format("%d分钟前", deltaMilliSeconds / MILLISECONDS_FOR_ONE_MINUTE);
                } else if (deltaMilliSeconds < MILLISECONDS_FOR_ONE_DAY) {
                    return String.format("%d小时前", deltaMilliSeconds / MILLISECONDS_FOR_ONE_HOUR);
                }
            }

            if (isSameDay(date, increaseDays(now, -1))) {
                return String.format("昨天 %d:%02d", dateCalendar.get(Calendar.HOUR_OF_DAY),
                        dateCalendar.get(Calendar.MINUTE));
            } else {
                return String.format("%d月%d日 %d:%02d", dateCalendar.get(Calendar.MONTH) + 1,
                        dateCalendar.get(Calendar.DAY_OF_MONTH),
                        dateCalendar.get(Calendar.HOUR_OF_DAY), dateCalendar.get(Calendar.MINUTE));
            }
        } else {
            return String.format("%d年%d月%d日 %d:%02d", dateCalendar.get(Calendar.YEAR),
                    dateCalendar.get(Calendar.MONTH) + 1,
                    dateCalendar.get(Calendar.DAY_OF_MONTH), dateCalendar.get(Calendar.HOUR_OF_DAY),
                    dateCalendar.get(Calendar.MINUTE));
        }
    }

    /**
     * 获取指定日期百分比
     *
     * @param date
     * @return
     */
    public static Float toPercentage(Date date) {
        if (date == null) date = new Date();
        int day = getDay(date);
        int month = getMonth(date);
        int year = getYear(date);
        int monthDays = getMonthLastDay(year, month);
        return (float) day / (float) monthDays;
    }

    /**
     * 计算两个时间间隔的天数
     */
    public static int intervalDays(Date date1, Date date2) {
        if (date2.after(date1)) {
            return Long.valueOf((date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24)).intValue();
        } else if (date2.before(date1)) {
            return Long.valueOf((date1.getTime() - date2.getTime()) / (1000 * 60 * 60 * 24)).intValue();
        } else {
            return 0;
        }
    }

    /**
     * 计算两个时间的间隔小时，只会整除
     */
    public static int intervalHours(Date date1, Date date2) {
        if (date2.after(date1)) {
            return Long.valueOf((date2.getTime() - date1.getTime()) / (1000 * 60 * 60)).intValue();
        } else if (date2.before(date1)) {
            return Long.valueOf((date1.getTime() - date2.getTime()) / (1000 * 60 * 60)).intValue();
        } else {
            return 0;
        }
    }

    /**
     * 计算两个时间的间隔分钟数，只会整除
     */
    public static int intervalMinutes(Date date1, Date date2) {
        if (date2.after(date1)) {
            return Long.valueOf((date2.getTime() - date1.getTime()) / (1000 * 60)).intValue();
        } else if (date2.before(date1)) {
            return Long.valueOf((date1.getTime() - date2.getTime()) / (1000 * 60)).intValue();
        } else {
            return 0;
        }
    }

    /**
     * 当前日期的基础上, 增加指定天数, 可以是负数
     */
    public static Date increaseDays(Date time, int days) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(time);
        ca.add(Calendar.DATE, days);
        return stringToDate(dateToShortDateString(ca.getTime()));
    }

    /**
     * 当前日期的基础上, 增加指定小时, 可以是负数
     */
    public static Date increaseHours(Date time, int hours) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(time);
        ca.add(Calendar.HOUR, hours);
        return ca.getTime();
    }

    /**
     * 当前日期的基础上, 增加指定分钟, 可以是负数
     */
    public static Date increaseMinute(Date time, int minute) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(time);
        ca.add(Calendar.MINUTE, minute);
        return ca.getTime();
    }

    /**
     * 比较两个时间是否是相同的天数
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (intervalDays(date1, date2) == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取某个年份
     *
     * @param date
     * @return
     */
    public static int getYear(Date date) {
        if (date == null) date = new Date();
        String year = new SimpleDateFormat("yyyy").format(date);
        return Integer.parseInt(year);
    }

    /**
     * 获取月份
     *
     * @param date
     * @return
     */
    public static int getMonth(Date date) {
        String month = new SimpleDateFormat("MM").format(date);
        return Integer.parseInt(month);
    }

    /**
     * 获取日期
     *
     * @param date
     * @return
     */
    public static int getDay(Date date) {
        String day = new SimpleDateFormat("dd").format(date);
        return Integer.parseInt(day);
    }

    /**
     * 获取当前日期，只包含年月日
     */
    public static Date getCurrentDate() {
        Calendar c = Calendar.getInstance();
        return stringToDate(dateToShortDateString(c.getTime()));
    }

    /**
     * 获取当前年份
     *
     * @return
     */
    public static int getCurrentYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR);
    }

    /**
     * 获取当前月份
     *
     * @return
     */
    public static int getCurrentMonth() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    public static int getCurrentDay() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前日期是星期几
     *
     * @return
     */
    public static int getDayOfWeek() {
        Calendar c = Calendar.getInstance();
        return getDayOfWeek(c.getTime());
    }

    /**
     * 返回日期对应的是星期几
     */
    public static int getDayOfWeek(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        int dayOfWeek;
        if (ca.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            dayOfWeek = 7;
        } else {
            dayOfWeek = ca.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return dayOfWeek;
    }

    /**
     * 获取某月的第一天
     *
     * @param year
     * @param month
     * @return
     */
    public static int getMonthFirstDay(int year, int month) {
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.YEAR, year);
        instance.set(Calendar.MONTH, month - 1);//0代表一月, 故减1
        instance.set(Calendar.DATE, 1);//把日期设置为当月第一天
        return instance.get(Calendar.DATE);
    }

    /**
     * 获取某月的最后一天
     *
     * @param year
     * @param month
     * @return
     */
    public static int getMonthLastDay(int year, int month) {
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.YEAR, year);
        instance.set(Calendar.MONTH, month - 1);//0代表一月, 故减1
        instance.set(Calendar.DATE, 1);//把日期设置为当月第一天
        instance.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        return instance.get(Calendar.DATE);
    }

    /**
     * 获取传入当日0点
     *
     * @param time
     * @return
     */
    public static Date getDayStart(Date time) {
        if (time == null) time = new Date();
        Calendar todayStart = Calendar.getInstance();
        todayStart.setTime(time);
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    /**
     * 获取传入当日最后一刻
     *
     * @param time
     * @return
     */
    public static Date getDayEnd(Date time) {
        if (time == null) time = new Date();
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.setTime(time);
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }

    /**
     * 获取今天的分钟数，如今天18:05，则返回1805
     * @return
     */
    public static int getTodayMinutes() {
        Calendar ca = Calendar.getInstance();
        int hours = ca.get(Calendar.HOUR_OF_DAY);
        int minutes = ca.get(Calendar.MINUTE);
        return hours * 60 + minutes;
    }

    /**
     * 获取今天的秒数
     * @return
     */
    public static int getTodaySeconds(){
        return getTodayMinutes() * 60;
    }

    /**
     * 获取今天的毫秒数
     * @return
     */
    public static long getTodayMillisecond(){
        return getTodaySeconds() * 1000;
    }

    /**
     * 判断是否是闰年
     *
     * @param year
     * @return
     */
    public static boolean isLeap(int year) {
        return ((year % 100 == 0) && year % 400 == 0) || ((year % 100 != 0) && year % 4 == 0);
    }
}