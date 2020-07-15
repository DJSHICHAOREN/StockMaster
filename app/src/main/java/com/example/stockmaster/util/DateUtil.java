package com.example.stockmaster.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static Date convertStringToDate(String timeStr){
        try {
            if(timeStr.contains("/")){
                timeStr = timeStr.replaceAll("/", "-");
            }
            return mSimpleDateFormat.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isMinuteEqual(Date time1, Date time2){
        if(time1.getMinutes() == time2.getTime()){
            return true;
        }
        return false;
    }

    /**
     * 将Date对象转为可以分时字符串
     * @param time
     * @return
     */
    public static String convertDateToShortDayString(Date time){
        String dayTime = "";
        if(time != null){
            String year = getDoubleNumString(time.getYear() + 1900);
            String month = getDoubleNumString(time.getMonth());
            String day = getDoubleNumString(time.getDate());
            dayTime = year + "-" + month + "-" + day;
        }
        return dayTime;
    }

    /**
     * 将Date对象转为分时字符串
     * @param time
     * @return
     */
    public static String convertDateToShortMinuteString(Date time){
        String minuteTime = "";
        if(time != null){
            String hour = getDoubleNumString(time.getHours());
            String minute = getDoubleNumString(time.getMinutes());
            String second = getDoubleNumString(time.getSeconds());
            minuteTime = hour + ":" + minute + ":" + second;
        }
        return minuteTime;
    }

    private static String getDoubleNumString(int num){
        return num >= 10 ? num + "" : "0" + num;
    }

    /**
     * 计算时间差
     * @param date1
     * @param date2
     * @return
     */
    public static long calculateMinutesGap(Date date1, Date date2){
        long gapMinutes = (date2.getTime() - date1.getTime())/(1000 * 60);
        gapMinutes = Math.abs(gapMinutes);
        return gapMinutes;
    }
}
