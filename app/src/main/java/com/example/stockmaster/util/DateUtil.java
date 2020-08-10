package com.example.stockmaster.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    private static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static Date convertStringToDate(String timeStr){
        try {
            if(timeStr.contains("/")){
                timeStr = timeStr.replaceAll("/", "-");
            }
            return mSimpleDateFormat.parse(timeStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断小时和分钟是否相等
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isDateEqual(Date time1, Date time2){
        if(time1.getDate() == time2.getDate() && time1.getHours() == time2.getHours() && time1.getMinutes() == time2.getMinutes()){
            return true;
        }
        else{
            return false;
        }
    }

    public static boolean isDateEqual(Date time1, int day, int hour, int minute){
        if(time1.getDate() == day && time1.getHours() == hour && time1.getMinutes() == minute){
            return true;
        }
        else{
            return false;
        }
    }

    public static boolean isDateAfter(Date time1, Date time2){
        if(     (time1.getMonth() > time2.getMonth()) ||
                (time1.getMonth() == time2.getMonth() && time1.getDate() > time2.getDate()) ||
                (time1.getMonth() == time2.getMonth() && time1.getDate() == time2.getDate() && time1.getHours() > time2.getHours()) ||
                (time1.getMonth() == time2.getMonth() && time1.getDate() == time2.getDate() && time1.getHours() == time2.getHours() && time1.getMinutes() > time2.getMinutes()) ){
            return true;
        }
        else{
            return false;
        }
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
            String month = getDoubleNumString(time.getMonth() + 1);
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

    public static String convertDateToShortString(Date time){
        return convertDateToShortDayString(time) + " " + convertDateToShortMinuteString(time);
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


    public static boolean isDealTime(){
        Calendar calendar = Calendar.getInstance();
        //
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        // 1:sunday,7:saturday
        if(dayOfWeek == 1 || dayOfWeek == 7){
            return false;
        }
        //获取系统时间
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if(hour < 9 || hour > 15){
            return false;
        }
        if(hour == 12){
            return false;
        }
        return true;
    }
}
