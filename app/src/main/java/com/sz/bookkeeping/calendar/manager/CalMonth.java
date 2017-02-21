package com.sz.bookkeeping.calendar.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created with Android Studio.
 * User: dashu
 * Date: 2017/2/15
 * Time: 下午2:39
 * Desc: 月
 */

public class CalMonth {

    private static final int WEEK_IN_MONTH = 6;

    private List<CalWeek> mWeekList;
    private int mYear;
    private int mMonth;

    public CalMonth(int year, int month) {
        mYear = year;
        mMonth = month;
        mWeekList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        calendar.add(Calendar.DATE, 1 - dayOfWeek);
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH) + 1;
        int d = calendar.get(Calendar.DATE);
        CalWeek calWeek = new CalWeek(y, m, d, month);
        mWeekList.add(calWeek);
        for (int i = 0; i < WEEK_IN_MONTH - 1; i++) {
            mWeekList.add(mWeekList.get(i).next());
        }
    }

    public List<CalWeek> getWeekList() {
        return mWeekList;
    }

    public CalWeek getLastWeek() {
        return mWeekList.get(WEEK_IN_MONTH - 1);
    }

    public CalDay getFirstDayOfMonth() {
        return new CalDay(mYear, mMonth, 1);
    }
}