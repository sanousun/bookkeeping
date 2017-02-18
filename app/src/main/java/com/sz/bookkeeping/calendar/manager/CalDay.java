package com.sz.bookkeeping.calendar.manager;


import com.sz.bookkeeping.calendar.util.CalendarUtils;
import com.sz.bookkeeping.calendar.util.LunarUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created with Android Studio.
 * User: dashu
 * Date: 2017/2/15
 * Time: 下午2:39
 * Desc: 天
 */

public class CalDay {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private Solar solar;
    private Lunar lunar;
    //是否是今天
    private boolean isToday;
    //是否使能
    private boolean isEnable;
    //是否被标记
    private boolean isMarked;
    //这天的特殊描述，节日或者农历
    private String mDayDescription;

    public CalDay(int year, int month, int day) {
        solar = new Solar();
        solar.solarYear = year;
        solar.solarMonth = month;
        solar.solarDay = day;
        lunar = LunarUtils.SolarToLunar(solar);
        mDayDescription = LunarUtils.getDescriptionOfDay(solar, lunar);
        isToday = CalendarUtils.isToday(year, month, day);
        isEnable = true;
        isMarked = false;
    }

    public Solar getSolar() {
        return solar;
    }

    public Lunar getLunar() {
        return lunar;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setToday(boolean today) {
        isToday = today;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public void setMarked(boolean marked) {
        isMarked = marked;
    }

    public String getDayDescription() {
        return mDayDescription;
    }

    public void setDayDescription(String dayDescription) {
        mDayDescription = dayDescription;
    }

    public CalDay pre() {
        return add(-1);
    }

    public CalDay next() {
        return add(1);
    }

    public CalDay add(int date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(solar.solarYear, solar.solarMonth - 1, solar.solarDay);
        calendar.add(Calendar.DATE, date);
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH) + 1;
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        return new CalDay(y, m, d);
    }

    public int getMonth() {
        return solar.solarMonth;
    }

    public static class Lunar {
        //是否润月
        public boolean isLeap;
        public int lunarDay;
        public int lunarMonth;
        public int lunarYear;
    }

    public static class Solar {
        public int solarDay;
        public int solarMonth;
        public int solarYear;

        @Override
        public String toString() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.set(solarYear, solarMonth - 1, solarDay);
            return sdf.format(calendar.getTime());
        }
    }
}
