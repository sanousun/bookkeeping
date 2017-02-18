package com.sz.bookkeeping.calendar.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.sz.bookkeeping.R;
import com.sz.bookkeeping.calendar.manager.CalDay;
import com.sz.bookkeeping.calendar.manager.CalWeek;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with Android Studio.
 * User: dashu
 * Date: 2017/2/12
 * Time: 下午8:31
 * Desc: 日历中的周视图
 */

public class WeekView extends LinearLayout {

    private CalWeek mCalWeek;
    private List<DayView> mDayViews;

    public WeekView(Context context) {
        this(context, null);
    }

    public WeekView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeekView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        inflate(context, R.layout.view_week, this);
        mDayViews = new ArrayList<>();
        mDayViews.add((DayView) findViewById(R.id.view_day0));
        mDayViews.add((DayView) findViewById(R.id.view_day1));
        mDayViews.add((DayView) findViewById(R.id.view_day2));
        mDayViews.add((DayView) findViewById(R.id.view_day3));
        mDayViews.add((DayView) findViewById(R.id.view_day4));
        mDayViews.add((DayView) findViewById(R.id.view_day5));
        mDayViews.add((DayView) findViewById(R.id.view_day6));
        if (mCalWeek != null) {
            initData();
        }
    }

    public CalWeek getCalWeek() {
        return mCalWeek;
    }

    public void setCalWeek(CalWeek calWeek) {
        mCalWeek = calWeek;
        if (mDayViews != null && mDayViews.size() != 0) {
            initData();
        }
    }

    private void initData() {
        for (int i = 0; i < mDayViews.size(); i++) {
            DayView dayView = mDayViews.get(i);
            CalDay calDay = mCalWeek.getDayList().get(i);
            dayView.setCalDay(calDay);
        }
    }
}
