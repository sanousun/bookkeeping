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
    private CalDay mSelectDay;

    private List<DayView> mDayViews;
    private OnDayOfWeekSelectListener mOnDayOfWeekSelectListener;

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
        mDayViews.add((DayView) findViewById(R.id.view_day1));
        mDayViews.add((DayView) findViewById(R.id.view_day2));
        mDayViews.add((DayView) findViewById(R.id.view_day3));
        mDayViews.add((DayView) findViewById(R.id.view_day4));
        mDayViews.add((DayView) findViewById(R.id.view_day5));
        mDayViews.add((DayView) findViewById(R.id.view_day6));
        setCalWeek(mCalWeek);
        setSelectDay(mSelectDay);
        initListener();
    }

    public void setOnDayOfWeekSelectListener(OnDayOfWeekSelectListener onDayOfWeekSelectListener) {
        mOnDayOfWeekSelectListener = onDayOfWeekSelectListener;
    }

    private void initListener() {
        for (DayView dayView : mDayViews) {
            dayView.setOnClickListener(view -> {
                if (!view.isSelected()) {
                    CalDay calDay = ((DayView) view).getCalDay();
                    setSelectDay(calDay);
                    if (mOnDayOfWeekSelectListener != null) {
                        mOnDayOfWeekSelectListener.onDaySelect(calDay);
                    }
                }
            });
        }
    }

    public void setCalWeek(CalWeek calWeek) {
        if (calWeek == null) return;
        mCalWeek = calWeek;
        for (int i = 0; i < mDayViews.size(); i++) {
            DayView dayView = mDayViews.get(i);
            CalDay calDay = mCalWeek.getDayList().get(i);
            dayView.setCalDay(calDay);
        }
    }

    public void setSelectDay(CalDay calDay) {
        if (calDay == null) return;
        mSelectDay = calDay;
        for (DayView dayView : mDayViews) {
            dayView.setSelected(mSelectDay.equals(dayView.getCalDay()));
        }
    }

    public CalWeek getCalWeek() {
        return mCalWeek;
    }

    public CalDay getSelectDay() {
        return mSelectDay;
    }

    public interface OnDayOfWeekSelectListener {
        void onDaySelect(CalDay calDay);
    }

    public boolean isWeekEnable() {
        return mCalWeek == null || mCalWeek.isEnable();
    }
}
