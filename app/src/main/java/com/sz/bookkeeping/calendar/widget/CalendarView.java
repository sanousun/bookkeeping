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

    private static final int STATE_MONTH = 0;
    private static final int STATE_WEEK = 1;

    private List<WeekView> mWeekViews;
    private WeekView mLastWeekView;
    private boolean isLastWeekViewEnable = true;

    private CalMonth mCalMonth;
    private CalDay mSelectDay;
    private int mMinHeight;
    private int mMaxHeight;
    private int mCurHeight;

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
        mLastWeekView = new WeekView(context);
        mWeekViews.add(mLastWeekView);
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
        setCalMonth(mCalMonth);
        setSelectDay(mSelectDay);
        initListener();
        final ViewConfiguration vc = ViewConfiguration.get(context);
        touchSlop = vc.getScaledTouchSlop();
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
                Log.e("xyz", "currentY：" + currentY + " lastTouchY:" + lastTouchY + " dy：" + dy);
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

    public void setOnDayOfMonthSelectListener(OnDayOfMonthSelectListener onDayOfMonthSelectListener) {
        mOnDayOfMonthSelectListener = onDayOfMonthSelectListener;
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
        for (int i = 0; i < mCalMonth.getWeekList().size() - 1; i++) {
            WeekView weekView = mWeekViews.get(i);
            CalWeek calWeek = mCalMonth.getWeekList().get(i);
            weekView.setCalWeek(calWeek);
        }
        mLastWeekView.setCalWeek(mCalMonth.getLastWeek());
        if (mSelectDay == null || mSelectDay.getMonth() != mCalMonth.getMonth()) {
            setSelectDay(mCalMonth.getFirstDayOfMonth());
        }
        fixLastWeekViewStates();
    }

    private void fixLastWeekViewStates() {
        //如果最后的weekView状态和上次的不一致时，需要更改view的状态
        if (isLastWeekViewEnable ^ mLastWeekView.isWeekEnable()) {
            if (isLastWeekViewEnable) {
                mMaxHeight -= mMinHeight;
            } else {
                mMaxHeight += mMinHeight;
            }
            Log.e("xyz", "mMaxHeight：" + mMaxHeight / mMinHeight);
            //如果是月视图状态，需要进行最后的weekView展示消失的动画
            if (mState == STATE_MONTH) {
                ValueAnimator valueAnimator = ValueAnimator.ofInt(
                        mCurHeight + getPaddingTop() + getPaddingBottom(),
                        mMaxHeight + getPaddingTop() + getPaddingBottom());
                valueAnimator.setInterpolator(new LinearOutSlowInInterpolator());
                valueAnimator.setDuration(DURING_COLLAPSE);
                valueAnimator.addUpdateListener(va -> {
                    ViewGroup.LayoutParams lp = getLayoutParams();
                    lp.height = (int) va.getAnimatedValue();
                    setLayoutParams(lp);
                    Log.e("xyz", "height：" + lp.height);
                });
                valueAnimator.addListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (mLastWeekView.isWeekEnable()) {
                            if (!mWeekViews.contains(mLastWeekView)) {
                                mWeekViews.add(mLastWeekView);
                            }
                            mLastWeekView.setVisibility(VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewGroup.LayoutParams lp = getLayoutParams();
                        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        setLayoutParams(lp);
                        if (!mLastWeekView.isWeekEnable()) {
                            if (mWeekViews.contains(mLastWeekView)) {
                                mWeekViews.remove(mLastWeekView);
                            }
                            mLastWeekView.setVisibility(GONE);
                        }
                        mCurHeight = mMaxHeight;
                    }
                });
                valueAnimator.start();
            } else {
                if (mLastWeekView.isWeekEnable()) {
                    if (!mWeekViews.contains(mLastWeekView)) {
                        mWeekViews.add(mLastWeekView);
                    }
                } else {
                    if (mWeekViews.contains(mLastWeekView)) {
                        mWeekViews.remove(mLastWeekView);
                    }
                }
            }
        }
        //状态赋值
        isLastWeekViewEnable = mLastWeekView.isWeekEnable();
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
            float rate = (mCurHeight - mMinHeight) * 1.0f / (mMaxHeight - (curSelectWeek + 1) * mMinHeight);
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