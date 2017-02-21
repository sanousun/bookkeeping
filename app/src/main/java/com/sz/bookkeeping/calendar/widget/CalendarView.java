package com.sz.bookkeeping.calendar.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sz.bookkeeping.calendar.manager.CalDay;
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

    private static final int DURING_COLLAPSE = 300;
    private static final int WEEK_VIEW_COUNT = CalMonth.WEEK_IN_MONTH;

    private static final int STATE_MONTH = 0;
    private static final int STATE_WEEK = 1;

    private CalMonth mCalMonth;
    private CalDay mSelectDay;
    private int mMinHeight;
    private int mMaxHeight;
    private int mCurHeight;
    private List<WeekView> mWeekViews;
    private WeekView mBottomWeekView;

    private int mState;
    private OnDayOfMonthSelectListener mOnDayOfMonthSelectListener;

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
        mWeekViews = new ArrayList<>();
        mWeekViews.add(new WeekView(context));
        mWeekViews.add(new WeekView(context));
        mWeekViews.add(new WeekView(context));
        mWeekViews.add(new WeekView(context));
        mWeekViews.add(new WeekView(context));
        mBottomWeekView = new WeekView(context);
        mWeekViews.add(mBottomWeekView);

        mMinHeight = SizeUtils.getScreenWidth(context) * 3 / 28;
        mMaxHeight = 0;
        for (int i = 0; i < mWeekViews.size(); i++) {
            WeekView weekView = mWeekViews.get(i);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, mMinHeight);
            layoutParams.setMargins(0, mMaxHeight, 0, 0);
            mMaxHeight += mMinHeight;
            weekView.setLayoutParams(layoutParams);
            addView(weekView);
        }
        mCurHeight = mState == STATE_MONTH ? mMaxHeight : mMinHeight;
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setCalMonth(mCalMonth);
        setSelectDay(mSelectDay);
        initListener();
    }

    private int touchSlop;
    private int lastX;
    private int lastY;
    private int lastTouchX;
    private int lastTouchY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean isIntercept = false;
        int action = ev.getAction();
        int currentX = (int) ev.getX();
        int currentY = (int) ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = currentX;
                lastTouchY = currentY;
                isIntercept = false;//点击事件分发给子控件
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = currentX - lastX;
                int dy = currentY - lastY;
                //父容器拦截或者分发子控件
                isIntercept = Math.abs(dx) > touchSlop / 3 || Math.abs(dy) > touchSlop / 3;
                break;
            case MotionEvent.ACTION_UP:
                isIntercept = false;//点击事件分发给子控件
                break;
        }
        //记录上次滑动的位置
        lastX = currentX;
        lastY = currentY;
        return isIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int currentX = (int) event.getX();
        int currentY = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //被DayView消费了，获取不到
                return false;
            case MotionEvent.ACTION_MOVE:
                int dx = currentX - lastTouchX;
                int dy = currentY - lastTouchY;
                mCurHeight += dy;
                if (mCurHeight > mMaxHeight) {
                    mCurHeight = mMaxHeight;
                } else if (mCurHeight < mMinHeight) {
                    mCurHeight = mMinHeight;
                }
                layoutChild();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                autoCollapse();
                break;
        }
        //记录上次滑动的位置
        lastTouchX = currentX;
        lastTouchY = currentY;
        return true;
    }

    private void initListener() {
        for (WeekView weekView : mWeekViews) {
            weekView.setOnDayOfWeekSelectListener(calDay -> {
                setSelectDay(calDay);
                if (mOnDayOfMonthSelectListener != null) {
                    mOnDayOfMonthSelectListener.onDaySelect(calDay);
                }
            });
        }
    }

    public void setOnDayOfMonthSelectListener(OnDayOfMonthSelectListener onDayOfMonthSelectListener) {
        mOnDayOfMonthSelectListener = onDayOfMonthSelectListener;
    }

    public CalDay getSelectDay() {
        return mSelectDay;
    }

    private void setSelectDay(CalDay calDay) {
        if (calDay == null) return;
        mSelectDay = calDay;
        for (WeekView weekView : mWeekViews) {
            weekView.setSelectDay(mSelectDay);
        }
    }

    public void setCalMonth(CalMonth calMonth) {
        if (calMonth == null) return;
        mCalMonth = calMonth;
        for (int i = 0; i < WEEK_VIEW_COUNT - 1; i++) {
            WeekView weekView = mWeekViews.get(i);
            CalWeek calWeek = mCalMonth.getWeekList().get(i);
            weekView.setCalWeek(calWeek);
        }
        mBottomWeekView.setCalWeek(mCalMonth.getLastWeek());
        if (mSelectDay == null || mSelectDay.getMonth() != mCalMonth.getMonth()) {
            setSelectDay(mCalMonth.getFirstDayOfMonth());
        }
        fixViewHeight();
    }

    private void fixViewHeight() {
        //判断底部weekView状态
        if (mBottomWeekView.isWeekEnable()) {
            mMaxHeight = mMinHeight * WEEK_VIEW_COUNT;
        } else {
            mMaxHeight = mMinHeight * (WEEK_VIEW_COUNT - 1);
        }
        Log.e("xyz", mMaxHeight + "");
        //如果是月视图状态，需要进行底部weekView展示或者消失的动画
        if (mState == STATE_MONTH && mCurHeight != mMaxHeight) {
            ValueAnimator valueAnimator = ValueAnimator.ofInt(
                    mCurHeight + getPaddingTop() + getPaddingBottom(),
                    mMaxHeight + getPaddingTop() + getPaddingBottom());
            valueAnimator.setInterpolator(new LinearOutSlowInInterpolator());
            valueAnimator.setDuration(DURING_COLLAPSE);
            valueAnimator.addUpdateListener(va -> {
                ViewGroup.LayoutParams lp = getLayoutParams();
                lp.height = (int) va.getAnimatedValue();
                setLayoutParams(lp);
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    LayoutParams layoutParams = (LayoutParams) mBottomWeekView.getLayoutParams();
                    layoutParams.setMargins(0, mMinHeight * (WEEK_VIEW_COUNT - 1), 0, 0);
                    mBottomWeekView.setLayoutParams(layoutParams);
                    if (mBottomWeekView.isWeekEnable()) {
                        addOrRemoveView(true);
                        mBottomWeekView.setVisibility(VISIBLE);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!mBottomWeekView.isWeekEnable()) {
                        addOrRemoveView(false);
                        mBottomWeekView.setVisibility(GONE);
                    }
                    mCurHeight = mMaxHeight;
                    ViewGroup.LayoutParams lp = getLayoutParams();
                    lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    setLayoutParams(lp);
                }
            });
            valueAnimator.start();
        } else {
            addOrRemoveView(mBottomWeekView.isWeekEnable());
        }
    }

    private void addOrRemoveView(boolean isAdd) {
        if (isAdd && !mWeekViews.contains(mBottomWeekView)) {
            mWeekViews.add(mBottomWeekView);
        } else if (!isAdd && mWeekViews.contains(mBottomWeekView)) {
            mWeekViews.remove(mBottomWeekView);
        }
    }

    public CalMonth getCalMonth() {
        return mCalMonth;
    }

    private void layoutChild() {
        int curSelectWeek = mSelectDay == null ? 0 : mSelectDay.getWeekOfMonth() - 1;
        int goneCount = (mMaxHeight - mCurHeight) / mMinHeight;
        if (goneCount < curSelectWeek) {
            int locationY = mCurHeight - mMaxHeight;
            for (int i = 0; i < mWeekViews.size(); i++) {
                WeekView weekView = mWeekViews.get(i);
                setChildLayoutY(weekView, locationY);
                locationY += mMinHeight;
            }
        } else {
            float rate = (mCurHeight - mMinHeight) * 1.0f /
                    (mMaxHeight - (curSelectWeek + 1) * mMinHeight);
            for (int i = 0; i < mWeekViews.size(); i++) {
                WeekView weekView = mWeekViews.get(i);
                if (i < curSelectWeek) {
                    weekView.setVisibility(GONE);
                } else if (i == curSelectWeek) {
                    setChildLayoutY(weekView, 0);
                } else {
                    if (rate <= 0) {
                        weekView.setVisibility(GONE);
                    } else {
                        setChildLayoutY(weekView, (int) (mMinHeight * rate * (i - curSelectWeek)));
                        weekView.setAlpha(rate);
                    }
                }
            }
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
        valueAnimator.setDuration(DURING_COLLAPSE);
        valueAnimator.addUpdateListener(va -> {
            mCurHeight = (int) va.getAnimatedValue();
            layoutChild();

        });
        valueAnimator.start();
    }

    public interface OnDayOfMonthSelectListener {
        void onDaySelect(CalDay calDay);
    }
}