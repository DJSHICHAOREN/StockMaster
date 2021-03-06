package com.example.stockmaster.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.stockmaster.util.DateUtil.convertStringToDate;

public class TextUtil {
    public static boolean isEmpty(String s){
        return s==null || s.equals("");
    }

    /**
     * 获取所有满足正则表达式的字符串
     * @param str 需要被获取的字符串
     * @param regex 正则表达式
     * @return 所有满足正则表达式的字符串
     */
    public static ArrayList<String> getAllSatisfyStrings(String str, String regex) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        ArrayList<String> allSatisfyStr = new ArrayList<>();
        if (regex == null || regex.isEmpty()) {
            allSatisfyStr.add(str);
            return allSatisfyStr;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            allSatisfyStr.add(matcher.group());
        }
        return allSatisfyStr;
    }

    public static List<Date> convertStringToDateList(String str){
        List<String> dateStringList = getAllSatisfyStrings(str,
                "\"date\":\"\\d*\\-\\d*\\-\\d*\"");

        List<Date> dateList = new ArrayList<>();
        for(String dateString : dateStringList){
            dateString = dateString.split(":")[1].replaceAll("\"", "");
            dateList.add(convertStringToDate(dateString + " " + "09:31:00"));
        }
        return dateList;
    }


    public static String searchDateString(String str){
        List<String> dateStringList = getAllSatisfyStrings(str,
                "\\d*/\\d*/\\d*");
        if(dateStringList.size() > 0){
            return dateStringList.get(0);
        }
        return null;
    }

    public static Date searchDate(String str){
        String dateString = searchDateString(str);
        Date date = convertStringToDate(dateString + " " + "09:31:00");
        return date;
    }


}
