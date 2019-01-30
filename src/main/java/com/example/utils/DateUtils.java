package com.example.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 时间转换工具类
 */
public class DateUtils {
	
	/** yyyyMMdd */
	public static final String YYYYMMDD = "yyyyMMdd";
	
	/** yyyy-MM-dd HH:mm:ss */
	public static final String YYYY_MM_DDHHMMSS = "yyyy-MM-dd HH:mm:ss";
	
	/** yyyy-MM-dd HH:mm */
	public static final String YYYYMMDDHHMM = "yyyy-MM-dd HH:mm";
	
	/** yyyy-MM-dd */
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	
	/** yyyyMMddHHmmss */
	public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
	
	/** yyyyMMddHHmmssSSS */
	public static final String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";
	
	/** 锁对象 */
    private static final Object lockObj = new Object();
	
	/** 存放不同的日期模板格式的sdf的Map */
    private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();
    
    /**
     * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
     */
    private static SimpleDateFormat getSdf(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);
        // 此处的双重判断和同步是为了防止sdfMap这个单例被多次put重复的sdf
        if (tl == null) {
            synchronized (lockObj) {
                tl = sdfMap.get(pattern);
                if (tl == null) {
                    tl = new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected SimpleDateFormat initialValue() {
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    sdfMap.put(pattern, tl);
                }
            }
        }
        return tl.get();
    }

	/**
	 * 按照指定pattern格式化日期
	 * 
	 * @param pattern
	 *            日期pattern
	 * @return 格式化的日期字符串
	 */
	public static final String format(String pattern) {
		return getSdf(pattern).format(new Date());
	}

	/**
	 * 格式化日期为指定pattern形式的字符串
	 * 
	 * @param date
	 *            待格式化的日期对象
	 * @param pattern
	 *            日期pattern
	 * @return 日期字符串
	 */
	public static final String getDefaultformat(Date date) {
		if(null != date){
			return getSdf(YYYYMMDDHHMMSS).format(date);
		}
		return null;
	}
	
	/**
	 * 格式化日期为指定pattern形式的字符串
	 * 
	 * @param date
	 *            待格式化的日期对象
	 * @param pattern
	 *            日期pattern
	 * @return 日期字符串
	 */
	public static final String format(Date date, String pattern) {
		if(null != date){
			return getSdf(pattern).format(date);
		}
		return null;
	}

	/**
	 * 解析日期字符串为日期对象
	 * 
	 * @param dateStr
	 *            日期字符串
	 * @param pattern
	 *            日期pattern
	 * @return 日期对象
	 */
	public static final Date parse(String dateStr, String pattern) {
		if(dateStr!=null && dateStr.length()>0) {
			try {
				return getSdf(pattern).parse(dateStr);
			} catch (ParseException e) {
				return null;
			}
		}
		return null;
	}
	
	/**
	 * 比较两个时间日期天数大小
	 * 如果firstDate大于secondDate，则返回1
	 * 若相等，则返回0，小于则返回-1。
	 */
	public static Integer compareDay(Date firstDate,Date secondDate){
		Calendar c1 = Calendar.getInstance();
		c1.setTime(firstDate);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(secondDate);
		if(c1.get(Calendar.YEAR)>c2.get(Calendar.YEAR)){
			return 1;
		}else if (c1.get(Calendar.YEAR)<c2.get(Calendar.YEAR)){
			return -1;
		}else{
			int firstDay=c1.get(Calendar.DAY_OF_YEAR);
			int secondDay=c2.get(Calendar.DAY_OF_YEAR);
			if(firstDay>secondDay){
				return 1;
			}else if(firstDay<secondDay){
				return -1;
			}else{
				return 0;
			}
		}
		
	}
	
	/**
     * 获取当前日期
     * @return 当前日期
     */
    public static final Date getCurrentDate(){
        return new Date();
    }
    
    public static final int getHours(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }
    
    public static final int getMinutes(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MINUTE);
    }
    
    public static final int getSeconds(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.SECOND);
    }
    
    public static final int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }
    
    public static final int getMonths(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1;//Calendar中1月返回的为0
    }
    
    public static final int getDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前日期，同时对<b> 时、分、秒、毫秒 全部清零</b><br/>
     * (例：2014-04-16 00:00:00)
     * @return 当前日期对象
     */
    public static final Date getWithoutTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    
    /**
     * 清零指定日期的<b> 时、分、秒、毫秒</b><br/>
     * (例：2014-10-23 00:00:00)
     * @param date 待处理日期
     * @return 日期
     */
    public static final Date getWithoutTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    
    /**
     * 设置指定日期的时、分、秒-毫秒设置为0<br/>
     * @param date 待设置日期
     * @param hour 待设置小时
     * @param minute 待设置分钟
     * @param second 待设置秒
     * @return 日期
     */
    public static final Date setTime(Date date, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    
    /**
     * 在指定日期的基础上，增加、减少天数<br/>
     * <b>amount为正数：天数增加，amount为负数：天数减少</b>
     * @param date 基础日期
     * @param amount 增加、减少天数
     * @return 增加、减少后的日期
     */
    public static final Date getDateWithAddDays(Date date, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, amount);
        return calendar.getTime();
    }
    
    /**
     * 判断日期是否在指定时间段内-前后均为闭区间
     * @param date 待比较日期
     * @param startDate 时间段-起始日期
     * @param endDate 时间段-截止日期
     * @return true：在该时间段内，false：不在该时间段内
     */
    public static final boolean betweenDateDuring(Date date, Date startDate, Date endDate) {
        if (date.compareTo(startDate) >= 0 && date.compareTo(endDate) <= 0) {
            return true;
        }
        return false;
    }
    
    /**
     * 判断日期是否在指定时间段内-前闭后开
     * @param date 待比较日期
     * @param startDate 时间段-起始日期
     * @param endDate 时间段-截止日期
     * @return true：在该时间段内，false：不在该时间段内
     */
    public static final boolean betweenDateDuringL(Date date, Date startDate, Date endDate) {
        if (date.compareTo(startDate) >= 0 && date.compareTo(endDate) < 0) {
            return true;
        }
        return false;
    }
    
    /**
     * 在指定日期的基础上，增加、减少分钟<br/>
     * <b>amount为正数：分钟增加，amount为负数：分钟减少</b>
     */
    public static final Date getDateWithAddMins(Date date,int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE,amount);
        return calendar.getTime();
    }
    
    /**  
     * 计算两个日期之间相差的毫秒数
     * @param smdate 较小的时间 
     * @param bdate  较大的时间 
     * @return 相差的毫秒数
     */    
    public static final Long timeInMillisBetween(Date smdate,Date bdate){ 
    	if(null == smdate || null == bdate){
    		return 0L;
    	}
        Calendar cal = Calendar.getInstance();    
        cal.setTime(smdate);    
        Long time1 = cal.getTimeInMillis();                 
        cal.setTime(bdate);    
        Long time2 = cal.getTimeInMillis();         
        return time2-time1;  
    } 
    
    
    /**  
     * 计算两个日期之间相差的天数  
     * @param smdate 较小的时间 
     * @param bdate  较大的时间 
     * @return 相差天数 
     */    
    public static final Long daysBetween(Date smdate,Date bdate){  
    	if(null == smdate || null == bdate){
    		return 0L;
    	}
        smdate=getWithoutTime(smdate);  
        bdate=getWithoutTime(bdate);  
        Calendar cal = Calendar.getInstance();    
        cal.setTime(smdate);    
        Long time1 = cal.getTimeInMillis();                 
        cal.setTime(bdate);    
        Long time2 = cal.getTimeInMillis();         
        return (time2-time1)/(1000*3600*24);  
    }
}
