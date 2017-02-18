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

    private List<CalDay> mDayList;
    private int curMonth;

    public List<CalDay> getDayList() {
        return mDayList;
    }

    public CalWeek(int sy, int sm, int sd, int curMonth) {
        this.curMonth = curMonth;
        mDayList = new ArrayList<>();
        CalDay start = new CalDay(sy, sm, sd);
        mDayList.add(start);
//        if (start.getMonth() > curMonth) {
//            curMonth = start.getMonth();
//        } else if (start.add(DAY_IN_WEEK - 1).getMonth() < curMonth) {
//            curMonth = start.add(DAY_IN_WEEK - 1).getMonth();
//        }
        start.setEnable(start.getMonth() == curMonth);
        for (int i = 0; i < DAY_IN_WEEK - 1; i++) {
            CalDay calDay = mDayList.get(i).next();
            calDay.setEnable(calDay.getMonth() == curMonth);
            mDayList.add(calDay);
        }
    }

    public CalWeek pre() {
        CalDay start = mDayList.get(0).add(-DAY_IN_WEEK);
        CalDay.Solar solar = start.getSolar();
        return new CalWeek(solar.solarYear, solar.solarMonth, solar.solarDay, curMonth);
    }

    public CalWeek next() {
        CalDay start = mDayList.get(0).add(DAY_IN_WEEK);
        CalDay.Solar solar = start.getSolar();
        return new CalWeek(solar.solarYear, solar.solarMonth, solar.solarDay, curMonth);
    }

    public boolean isEnable() {
        return mDayList.get(0).getMonth() == curMonth
                && mDayList.get(DAY_IN_WEEK - 1).getMonth() == curMonth;
    }

    public boolean isEndDayEnable() {
        return mDayList.get(DAY_IN_WEEK - 1).getMonth() == curMonth;
    }
}
