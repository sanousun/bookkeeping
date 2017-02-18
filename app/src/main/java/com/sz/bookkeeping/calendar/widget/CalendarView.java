package com.sz.bookkeeping.calendar.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.sz.bookkeeping.R;
import com.sz.bookkeeping.calendar.manager.CalMonth;
import com.sz.bookkeeping.calendar.manager.CalWeek;
import com.sz.bookkeeping.util.SizeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with Android Studio.
 * User: dashu
 * Date: 2017/2/9
 * Time: 下午5:29
 * Desc: 日历视图控件，默认状态为月视图，仿氢OS日历实现
 */

public class CalendarView extends FrameLayout {

    private static final int DURING_COLLAPSE = 500;

    private static final int STATE_MONTH = 0;
    private static final int STATE_WEEK = 1;

    private List<WeekView> mWeekViews;
    private CalMonth mCalMonth;
    private int mMinHeight;
    private int mMaxHeight;
    private int mCurHeight;
    private int mCurSelectWeek;

    private int mState;

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
        mGestureDetector = new GestureDetector(context, new MyGestureDetector());
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
        mCurHeight = mState == STATE_MONTH ? mMaxHeight : mMinHeight;
        mCurSelectWeek = 2;
        if (mCalMonth != null) {
            initData();
        }
    }

    public CalMonth getCalMonth() {
        return mCalMonth;
    }

    public void setCalMonth(CalMonth calMonth) {
        mCalMonth = calMonth;
        if (mWeekViews != null && mWeekViews.size() != 0) {
            initData();
        }
    }

    private void initData() {
        for (int i = 0; i < mWeekViews.size(); i++) {
            WeekView weekView = mWeekViews.get(i);
            CalWeek calWeek = mCalMonth.getWeekList().get(i);
            weekView.setCalWeek(calWeek);
        }
    }

    private void layoutChild() {
        int goneCount = (mMaxHeight - mCurHeight) / mMinHeight;
        if (goneCount < mCurSelectWeek) {
            int locationY = mCurHeight - mMaxHeight;
            for (int i = 0; i < mWeekViews.size(); i++) {
                WeekView weekView = mWeekViews.get(i);
                setChildLayoutY(weekView, locationY);
                locationY += mMinHeight;
            }
        } else {
            float rate = (mCurHeight - mMinHeight) * 1.0f / (mMaxHeight - (mCurSelectWeek + 1) * mMinHeight);
            Log.e("xyz", "rate:" + rate);
            for (int i = 0; i < mWeekViews.size(); i++) {
                WeekView weekView = mWeekViews.get(i);
                if (i < mCurSelectWeek) {
                    weekView.setVisibility(GONE);
                } else if (i == mCurSelectWeek) {
                    setChildLayoutY(weekView, 0);
                } else {
                    if (rate <= 0) {
                        weekView.setVisibility(GONE);
                    } else {
                        setChildLayoutY(weekView, (int) (mMinHeight * rate * (i - mCurSelectWeek)));
                        weekView.setAlpha(rate);
                    }
                }
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < mWeekViews.size(); i++) {
                builder.append("|i=").append(i).append(";visible").append(mWeekViews.get(i).getVisibility());
            }
            Log.e("xyz", builder.toString());
        }
        if (mCurHeight == mMinHeight) {
            mState = STATE_WEEK;
        } else if (mCurHeight == mMaxHeight) {
            mState = STATE_MONTH;
        }
    }

    private void setChildLayoutY(WeekView weekView, int locationY) {
        if (locationY <= -mMinHeight) {
            weekView.setVisibility(GONE);
        } else {
            weekView.setVisibility(VISIBLE);
            LayoutParams layoutParams = (LayoutParams) weekView.getLayoutParams();
            layoutParams.setMargins(0, locationY, 0, 0);
            weekView.setLayoutParams(layoutParams);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            autoCollapse();
        }
        return true;
    }

    private void autoCollapse() {
        int form = mCurHeight;
        int to;
        if (mState == STATE_MONTH) {
            if (mMaxHeight - mCurHeight < mMinHeight / 2) {
                to = mMaxHeight;
            } else {
                to = mMinHeight;
            }
        } else {
            if (mCurHeight - mMinHeight < mMinHeight / 2) {
                to = mMinHeight;
            } else {
                to = mMaxHeight;
            }
        }
        ValueAnimator valueAnimator = ValueAnimator.ofInt(form, to);
        valueAnimator.setInterpolator(new LinearOutSlowInInterpolator());
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(va -> {
            mCurHeight = (int) va.getAnimatedValue();
            layoutChild();

        });
        valueAnimator.start();
    }

    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.e("xyz", "distanceX:" + distanceX + "; distanceY" + distanceY);
            mCurHeight -= distanceY * 3 / 4;
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