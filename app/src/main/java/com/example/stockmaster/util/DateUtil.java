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
     * 将Date对象转为可以对比的字符串
     * @param time
     * @return
     */
    public static String convertDateToShortString(Date time){
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
}
