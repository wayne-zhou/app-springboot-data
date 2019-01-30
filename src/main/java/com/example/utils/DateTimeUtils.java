package com.example.utils;/**
 * Created by zhouwei03 on 2017/11/13.
 */

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;

/**
 * @author zhouwei03
 * @create 2017/11/27
 * 时间工具类
 */
public class DateTimeUtils {

    public  static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public  static final String DATE_DAY_PATTERN = "yyyy-MM-dd";

    //获取当前时间的LocalDateTime对象
    //LocalDateTime.now();

    //根据年月日构建LocalDateTime
    //LocalDateTime.of();

    //比较日期先后
    //LocalDateTime.now().isBefore(),
    //LocalDateTime.now().isAfter(),

    //Date转换为LocalDateTime
    public static LocalDateTime convertDateToLDT(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    //LocalDateTime转换为Date
    public static Date convertLDTToDate(LocalDateTime time) {
        return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
    }


    //获取指定日期的毫秒
    public static Long getMilliByTime(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    //获取指定日期的秒
    public static Long getSecondsByTime(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    //获取指定时间的指定格式
    public static String formatTime(LocalDateTime time,String pattern) {
        return time.format(DateTimeFormatter.ofPattern(pattern));
    }

    //获取指定时间的默认格式
    public static String formatTime(LocalDateTime time) {
        return formatTime(time, DATE_TIME_PATTERN);
    }


    //获取当前时间的指定格式
    public static String formatNow(String pattern) {
        return  formatTime(LocalDateTime.now(), pattern);
    }

    //获取当前时间的默认格式
    public static String formatNowDefault() {
        return  formatTime(LocalDateTime.now(), DATE_TIME_PATTERN);
    }

    //指定给定日期的时分秒
    public static LocalDateTime setDayOfTime(LocalDateTime time, int hour, int minute, int second) {
        return time.withHour(hour)
                .withMinute(minute)
                .withSecond(second)
                .withNano(0);
    }

    //指定当前日期的时分秒
    public static LocalDateTime setDayOfTime(int hour, int minute, int second) {
        return setDayOfTime(LocalDateTime.now(),hour, minute, second);
    }

    //获取一天的开始时间，2017,7,22 00:00
    public static LocalDateTime getDayStart(LocalDateTime time) {
        return time.withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }

    //获取一天的结束时间，2017,7,22 23:59:59.999999999
    public static LocalDateTime getDayEnd(LocalDateTime time) {
        return time.withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(999999999);
    }

    //指定日期加上一个数,根据field不同加不同值,field为ChronoUnit.*
    public static LocalDateTime plus(LocalDateTime time, long number, TemporalUnit field) {
        return time.plus(number, field);
    }

    //当前日期加上一个数,根据field不同加不同值,field为ChronoUnit.*
    public static LocalDateTime plus(long number, TemporalUnit field) {
        return plus(LocalDateTime.now(), number, field);
    }

    //指定日期减去一个数,根据field不同减不同值,field参数为ChronoUnit.*
    public static LocalDateTime minu(LocalDateTime time, long number, TemporalUnit field){
        return time.minus(number,field);
    }

    //当前日期减去一个数,根据field不同减不同值,field参数为ChronoUnit.*
    public static LocalDateTime minu(long number, TemporalUnit field){
        return minu(LocalDateTime.now(), number, field);
    }

    /**
     * 获取两个日期的差  field参数为ChronoUnit.*
     * @param startTime
     * @param endTime
     * @param field  单位(年月日时分秒)
     * @return
     */
    public static long betweenTwoTime(LocalDateTime startTime, LocalDateTime endTime, ChronoUnit field) {
        Period period = Period.between(LocalDate.from(startTime), LocalDate.from(endTime));
        if (field == ChronoUnit.YEARS) return period.getYears();
        if (field == ChronoUnit.MONTHS) return period.getYears() * 12 + period.getMonths();
        return field.between(startTime, endTime);
    }
}
