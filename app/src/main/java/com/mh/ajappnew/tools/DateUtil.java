package com.mh.ajappnew.tools;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    @SuppressLint("SimpleDateFormat")
    public static String getNowDateTime() {
        SimpleDateFormat s_format = new SimpleDateFormat("yyyyMMddHHmmss");
        return s_format.format(new Date());
    }

    public static String getNowDateTime1() {
        SimpleDateFormat s_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return s_format.format(new Date());
    }


    @SuppressLint("SimpleDateFormat")
    public static String getNowTime() {
        SimpleDateFormat s_format = new SimpleDateFormat("HH:mm:ss");
        return s_format.format(new Date());
    }

    @SuppressLint("SimpleDateFormat")
    public static String getNowday() {
        SimpleDateFormat s_format = new SimpleDateFormat("dd");
        return s_format.format(new Date());
    }

    @SuppressLint("SimpleDateFormat")
    public static String getNowyear() {
        SimpleDateFormat s_format = new SimpleDateFormat("yyyy");
        return s_format.format(new Date());
    }

    @SuppressLint("SimpleDateFormat")
    public static String getNowmonth() {
        SimpleDateFormat s_format = new SimpleDateFormat("MM");
        return s_format.format(new Date());
    }

    public static long TimeCompareNow(String date) {
        //格式化时间
        SimpleDateFormat CurrentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long cha = 0;
        try {
            Date serverdate = CurrentTime.parse(date);
            Date localdate = new Date();
            //判断是否大于两天
            cha = (serverdate.getTime() - localdate.getTime())/1000;

        } catch (Exception ex) {
            cha = 0;
        }

        return cha;
    }

}

