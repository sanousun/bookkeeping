package com.sz.bookkeeping.calendar.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.sz.bookkeeping.R;

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
    }
}
