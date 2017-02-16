package com.sz.bookkeeping.calendar.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.sz.bookkeeping.R;
import com.sz.bookkeeping.util.SizeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with Android Studio.
 * User: dashu
 * Date: 2017/2/9
 * Time: 下午5:29
 * Desc: 日历视图控件，默认状态为月视图
 */

public class CalendarView extends FrameLayout {

    private List<WeekView> mWeekViews;
    private int mMinHeight;
    private int mMaxHeight;
    private int mCurHeight;

    private GestureDetector mGestureDetector;

    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        inflate(context, R.layout.view_calendar, this);
        mWeekViews = new ArrayList<>();
        mWeekViews.add((WeekView) findViewById(R.id.view_week0));
        mWeekViews.add((WeekView) findViewById(R.id.view_week1));
        mWeekViews.add((WeekView) findViewById(R.id.view_week2));
        mWeekViews.add((WeekView) findViewById(R.id.view_week3));
        mWeekViews.add((WeekView) findViewById(R.id.view_week4));
        mWeekViews.add((WeekView) findViewById(R.id.view_week5));
        mMinHeight = SizeUtils.getScreenWidth(context) * 3 / 28;
        mMaxHeight = 0;
        for (int i = 0; i < mWeekViews.size(); i++) {
            WeekView weekView = mWeekViews.get(i);
            LayoutParams layoutParams = (LayoutParams) weekView.getLayoutParams();
            layoutParams.setMargins(0, mMaxHeight, 0, 0);
            layoutParams.height = mMinHeight;
            mMaxHeight += mMinHeight;
            weekView.setLayoutParams(layoutParams);
        }
        mCurHeight = mMaxHeight;
        mGestureDetector = new GestureDetector(context, new MyGestureDetector());
    }

    private void layoutChild() {
        int goneCount = (mMaxHeight - mCurHeight) / mMinHeight;
        int locationY = -(mMaxHeight - mCurHeight) % mMinHeight;
        Log.e("xyz", "goneCount:" + goneCount + "; locationY:" + locationY);
        for (int i = 0; i < mWeekViews.size(); i++) {
            WeekView weekView = mWeekViews.get(i);
            if (i < goneCount) {
                weekView.setVisibility(GONE);
            } else {
                weekView.setVisibility(VISIBLE);
                LayoutParams layoutParams = (LayoutParams) weekView.getLayoutParams();
                layoutParams.setMargins(0, locationY, 0, 0);
                locationY += mMinHeight;
                weekView.setLayoutParams(layoutParams);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.e("xyz", "distanceX:" + distanceX + "; distanceY" + distanceY);
            mCurHeight -= distanceY;
            if (mCurHeight > mMaxHeight) {
                mCurHeight = mMaxHeight;
            } else if (mCurHeight < mMinHeight) {
                mCurHeight = mMinHeight;
            }
            layoutChild();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

}