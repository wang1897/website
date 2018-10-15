package com.aethercoder.foundation.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Guo Feiyan on 2017/8/30.
 */
public class DateUtil {


    /**
     * 当前时间+1
     *
     * @param date
     * @return
     */
    public static Date getNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, +1);//+1当前时间的时间加一天
        date = calendar.getTime();
        return date;
    }


    /**
     * 当前时间-1
     *
     * @param date
     * @return
     */
    public static Date getBeforeDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, -24);//-24当前时间的前一天内
        date = calendar.getTime();
        return date;
    }

    /**
     * 比较两个时间大小
     *
     * @param d1
     * @param d2
     * @return
     */
    public static boolean dateCompare(Date d1, Date d2) {
        if (d1.before(d2)) {
            //表示d1小于d2
            return true;
        } else {
            return false;
        }
    }



    public static Date stringToDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return date;
    }


    public static Date stringToDateFormat(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return date;
    }

    /**
     * 获取时间戳
     * 输出结果:1438692801766
     */
    public static long getTimeStamp() {
        Date date = new Date();
        long times = date.getTime();
        return times;
        //第二种方法：
        // new Date().getTime();
    }

    /**
     * 获取格式化的时间
     * 输出格式：2017-08-04 20:55:35
     */
    public static String dateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * 获取格式化的时间
     * 输出格式：2017-08-04 20:55:35
     */
    public static String dateToStringYYYYMMDD(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * 将时间戳转化为标准时间
     * 输出：Tue Oct 07 12:04:36 CST 2014
     */
    public static String timestampToDate(long times) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(times);
        return formatter.format(date);
    }


    /**
     * date2比date1多的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        //同一年
        if (year1 != year2)
        {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                //闰年
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0)
                {
                    timeDistance += 366;
                } else    //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2 - day1);
        } else    //不同年
        {
            return day2 - day1;
        }
    }

    /**
     * 设定一个日期时间，加几分钟(小时或者天）后得到新的日期
     *
     * @param date
     * @param x    24小时制
     * @return
     */
    public static Date addDateMinut(Date date, Integer x) {
        //引号里面个格式也可以是 HH:mm:ss或者HH:mm等等，很随意的，不过在主函数调用时，要和输入的变
        //量day格式一致
        if (date != null){
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            // 24小时制
            cal.add(Calendar.MINUTE, x);
            date = cal.getTime();
        }
        return date;

    }


    public  static  Date addDateDay(Date date,Integer day){

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.WEDNESDAY, -1);
        return  cal.getTime();
    }

    /**
     * 获取上月时间的当天
     * @param date
     * @return
     */
    public  static Date getLastMouth(Date date){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date); // 设置为当前时间
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1); // 设置为上一个月
        date = calendar.getTime();
        return date;
    }

    /**
     * 上月第1天
     * @param date
     * @return
     */
    public  static Date getLastMouthFirstDay(Date date)  {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        // GregorianCalendar是Calendar的一个具体子类
        GregorianCalendar gc1 =(GregorianCalendar)Calendar.getInstance();
        gc1.setTime(date);
        gc1.set(Calendar.DAY_OF_MONTH,  1 );      //设置该月的第一天
        gc1.set(Calendar.HOUR_OF_DAY,0);
        gc1.set(Calendar.MINUTE,0);
        gc1.set(Calendar.SECOND,0);
        return gc1.getTime();
    }

    /**
     * 获取指定时间在本年中的周次
     * @param date
     * @return
     */
    public  static Long getWeekthByDate(Date date)  {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        int i = ca.get(Calendar.WEEK_OF_YEAR);
        Long aLong = new Long(i);
        return aLong;
    }

    /**
     * 获取指定时间在本年中的月次
     * @param date
     * @return
     */
    public  static Long getMonthByDate(Date date)  {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        int i = ca.get(Calendar.MONTH);
        Long aLong = new Long(i);
        return aLong;
    }

    /**
     * 获取指定时间的年份
     * @param date
     * @return
     */
    public  static Long getYearOfDate(Date date)  {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        int i = ca.get(Calendar.YEAR);
        Long aLong = new Long(i);
        return aLong;
    }

    /**
     * 获取今天的零点零分零秒 YYYYMMDD HHMMSS
     * @param
     * @return
     */
    public  static Date getTodayBegin()  {
        //当前时间毫秒数
        long current = System.currentTimeMillis();
        //今天零点零分零秒的毫秒数
        long zero = current/(1000*3600*24)*(1000*3600*24)- TimeZone.getDefault().getRawOffset();
        //今天23点59分59秒的毫秒数
        long twelve = zero+24*60*60*1000-1;
        //今天零点零分零秒
        Timestamp timestamp = new Timestamp(zero);
        return timestamp;
    }

    /**
     * 今天23点59分59秒 YYYYMMDD HHMMSS
     * @param
     * @return
     */
    public  static Date getTodayEnd()  {
        //当前时间毫秒数
        long current = System.currentTimeMillis();
        //今天零点零分零秒的毫秒数
        long zero = current/(1000*3600*24)*(1000*3600*24)- TimeZone.getDefault().getRawOffset();
        //今天23点59分59秒的毫秒数
        long twelve = zero+24*60*60*1000-1;
        //今天23点59分59秒
        Timestamp timestamp = new Timestamp(twelve);
        return timestamp;
    }

    public static Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(8);
        Date currentTime_2 = formatter.parse(dateString, pos);
        return currentTime_2;
    }

    /**
     * 取某天零点
     * @param
     * @return
     */
    public static Date getStartTimeOfDay(Date date){
        Calendar day = Calendar.getInstance();
        day.setTime(date);
        day.set(Calendar.HOUR_OF_DAY,0);
        day.set(Calendar.MINUTE,0);
        day.set(Calendar.SECOND,0);
        day.set(Calendar.MILLISECOND,0);
        return day.getTime();
    }

    /**
     * 取某天末点
     * @param
     * @return
     */
    public static Date getEndTimeOfDay(Date date){
        Calendar day = Calendar.getInstance();
        day.setTime(date);
        day.set(Calendar.HOUR_OF_DAY,23);
        day.set(Calendar.MINUTE,59);
        day.set(Calendar.SECOND,59);
        return day.getTime();
    }
}



