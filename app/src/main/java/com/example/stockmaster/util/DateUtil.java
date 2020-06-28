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
}
