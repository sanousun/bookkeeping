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

    //公历信息
    private Solar mSolar;
    //农历信息
    private Lunar mLunar;
    //是否是今天
    private boolean isToday;
    //是否使能
    private boolean isEnable;
    //是否被标记
    private boolean isMarked;
    //这天的特殊描述，节日或者农历
    private String mDayDescription;

    public static CalDay getCalDay(Calendar calendar) {
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH) + 1;
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        return new CalDay(y, m, d);
    }

    public static CalDay getToday() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new CalDay(year, month, day);
    }

    public CalDay(int year, int month, int day) {
        mSolar = new Solar();
        mSolar.solarYear = year;
        mSolar.solarMonth = month;
        mSolar.solarDay = day;
        mLunar = LunarUtils.SolarToLunar(mSolar);
        mDayDescription = LunarUtils.getDescriptionOfDay(mSolar, mLunar);
        isToday = CalendarUtils.isToday(year, month, day);
        isEnable = true;
        isMarked = false;
    }

    public Solar getSolar() {
        return mSolar;
    }

    public Lunar getLunar() {
        return mLunar;
    }

    public boolean isToday() {
        return isToday;
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

    /**
     * 天数据结构的加减法
     *
     * @param date 加减的天数
     * @return 天数据结构
     */
    public CalDay add(int date) {
        Calendar calendar = mSolar.getCalendar();
        calendar.add(Calendar.DATE, date);
        return CalDay.getCalDay(calendar);
    }

    public CalDay pre() {
        return add(-1);
    }

    public CalDay next() {
        return add(1);
    }

    public CalWeek getCalWeek() {
        CalDay calDay = add(1 - getDayOfWeek());
        Solar solar = calDay.getSolar();
        return new CalWeek(solar.solarYear, solar.solarMonth, solar.solarDay, solar.solarMonth);
    }

    /**
     * 有时候获取的周信息可能并不是当前月份的
     * @param curMonth 当前月份
     * @return 周信息
     */
    public CalWeek getCalWeek(int curMonth) {
        CalDay calDay = add(1 - getDayOfWeek());
        Solar solar = calDay.getSolar();
        return new CalWeek(solar.solarYear, solar.solarMonth, solar.solarDay, curMonth);
    }

    public CalMonth getCalMonth() {
        return new CalMonth(mSolar.solarYear, mSolar.solarMonth);
    }

    public int getMonth() {
        return mSolar.solarMonth;
    }

    /**
     * 星期一作为第一天，获取当前的日期是一个月中的第几个礼拜
     * @return 第一个礼拜:0
     */
    public int getWeekOfMonth() {
        Calendar calendar = mSolar.getCalendar();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }

    /**
     * 获取当前的日期是一个礼拜中的第几天
     * @return 星期一:1; 星期天:7
     */
    public int getDayOfWeek() {
        Calendar calendar = mSolar.getCalendar();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (day == 0) {
            day = 7;
        }
        return day;
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

        public Calendar getCalendar() {
            Calendar calendar = Calendar.getInstance();
            calendar.set(solarYear, solarMonth - 1, solarDay);
            return calendar;
        }

        @Override
        public String toString() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.set(solarYear, solarMonth - 1, solarDay);
            return sdf.format(calendar.getTime());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Solar)) {
                return false;
            }
            Solar solar = (Solar) obj;
            return solar.solarDay == this.solarDay
                    && solar.solarMonth == this.solarMonth
                    && solar.solarYear == this.solarYear;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof CalDay)) {
            return false;
        }
        CalDay calDay = (CalDay) obj;
        return calDay.mSolar.equals(mSolar);
    }
}
