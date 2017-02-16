package com.sz.bookkeeping.calendar.manager;


import com.sz.bookkeeping.calendar.util.CalendarUtils;

import java.util.Calendar;

/**
 * Created with Android Studio.
 * User: dashu
 * Date: 2017/2/15
 * Time: 下午2:39
 * Desc: 天
 */

public class CalDay {

    private Calendar mCalendar;
    //是否是今天
    private boolean isToday;
    //是否被选中
    private boolean isSelected;
    //是否使能
    private boolean isEnable;
    //是否被标记
    private boolean isMarked;
    //这天的特殊描述，节日或者农历
    private String mDayDescription;

    public CalDay(int year, int month, int day) {
        mCalendar = Calendar.getInstance();
        mCalendar.set(year, month - 1, day);
        isToday = CalendarUtils.isToday(year, month, day);
        isSelected = false;
        isEnable = true;
        isMarked = false;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setToday(boolean today) {
        isToday = today;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
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
}
