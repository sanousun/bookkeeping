package com.sz.bookkeeping.calendar.manager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with Android Studio.
 * User: dashu
 * Date: 2017/2/15
 * Time: 下午2:39
 * Desc: 周
 */

public class CalWeek {

    private static final int DAY_IN_WEEK = 7;

    private int mCurMonth;
    private List<CalDay> mDayList;

    public CalWeek(int year, int month, int day, int curMonth) {
        mCurMonth = curMonth;
        mDayList = new ArrayList<>();
        CalDay start = new CalDay(year, month, day);
        mDayList.add(start);
        start.setEnable(start.getMonth() == curMonth);
        for (int i = 0; i < DAY_IN_WEEK - 1; i++) {
            CalDay calDay = mDayList.get(i).next();
            calDay.setEnable(calDay.getMonth() == curMonth);
            mDayList.add(calDay);
        }
    }

    public List<CalDay> getDayList() {
        return mDayList;
    }

    public CalWeek pre() {
        CalDay start = mDayList.get(0).add(-DAY_IN_WEEK);
        return start.getCalWeek();
    }

    public CalWeek next() {
        CalDay start = mDayList.get(0).add(DAY_IN_WEEK);
        return start.getCalWeek();
    }

    /**
     * 获取当前月份下的下周信息，仅供CalMonth构造使用
     * @return 下周信息
     */
    public CalWeek nextWithCur() {
        CalDay start = mDayList.get(0).add(DAY_IN_WEEK);
        return start.getCalWeek(mCurMonth);
    }

    public CalDay getFirstDayOfWeek() {
        return mDayList.get(0);
    }

    public boolean isEnable() {
        return mDayList.get(0).getMonth() == mCurMonth || mDayList.get(DAY_IN_WEEK - 1).getMonth() == mCurMonth;
    }

    public boolean isEndDayEnable() {
        return mDayList.get(DAY_IN_WEEK - 1).getMonth() == mCurMonth;
    }
}
