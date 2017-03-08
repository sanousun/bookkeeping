package com.sz.bookkeeping.calendar.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.sz.bookkeeping.R;
import com.sz.bookkeeping.calendar.manager.CalDay;
import com.sz.bookkeeping.calendar.manager.CalMonth;
import com.sz.bookkeeping.calendar.manager.CalWeek;
import com.sz.bookkeeping.util.LogUtil;
import com.sz.bookkeeping.util.SizeUtils;

/**
 * Created with Android Studio.
 * User: dashu
 * Date: 2017/2/22
 * Time: 上午11:00
 * Desc:
 */

public class CustomerCalendarView extends View {

    private static final int DAY_IN_WEEK = 7;
    private static final int WEEK_IN_MONTH = 6;

    private static final int ALPHA_ENABLE = 0x40;
    private static final int ALPHA_NORMAL = 0xFF;

    private TextPaint mTextPaint;
    private Paint mOvalPaint;

    private int dayColorSelected;
    private float dayTextSize;
    private int dayTextColorNormal;
    private float descTextSize;
    private int descTextColorNormal;
    private float dotSize;
    private int dotColorNormal;
    private int bgColorToday;
    private int bgColorSelected;
    private float bgRadiusSize;

    private int dayHeight;
    private int dayWidth;

    private int maxHeight;
    private int minHeight;
    private int curHeight;

    private CalDay curDay;
    private CalMonth curMonth;

    private GestureDetector gestureDetector;

    public CustomerCalendarView(Context context) {
        this(context, null);
    }

    public CustomerCalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomerCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mOvalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DayView);
        dayColorSelected = ta.getColor(
                R.styleable.DayView_day_color_selected, Color.parseColor("#FFFFFF"));
        dayTextSize = ta.getDimension(
                R.styleable.DayView_day_text_size, SizeUtils.dp2px(context, 16f));
        dayTextColorNormal = ta.getColor(
                R.styleable.DayView_day_text_color, Color.parseColor("#373737"));

        descTextSize = ta.getDimension(
                R.styleable.DayView_desc_text_size, SizeUtils.dp2px(context, 8f));
        descTextColorNormal = ta.getColor(
                R.styleable.DayView_desc_text_color, Color.parseColor("#AFAFAF"));

        dotSize = ta.getDimension(
                R.styleable.DayView_dot_size, SizeUtils.dp2px(context, 3f));
        dotColorNormal = ta.getColor(
                R.styleable.DayView_dot_color, Color.parseColor("#FF5252"));

        bgColorToday = ta.getColor(
                R.styleable.DayView_bg_color_today, Color.parseColor("#E7E7E7"));
        bgColorSelected = ta.getColor(
                R.styleable.DayView_bg_color_selected, Color.parseColor("#4CAF50"));
        bgRadiusSize = ta.getDimension(
                R.styleable.DayView_bg_radius_size, SizeUtils.dp2px(context, 18f));
        ta.recycle();
        gestureDetector = new GestureDetector(context, new MyGestureDetector());
        initDefaultDay();
    }

    private void initDefaultDay() {
        curMonth = CalMonth.getCurMonth();
        curDay = CalDay.getToday();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY) {
            width = SizeUtils.getScreenWidth(getContext());
        }
        dayWidth = (width - getPaddingLeft() - getPaddingRight()) / DAY_IN_WEEK;
        int height = (width - getPaddingLeft() - getPaddingRight()) * 5 / 7
                + getPaddingTop() + getPaddingBottom();
        maxHeight = curHeight = (height - getPaddingTop() - getPaddingBottom());
        minHeight = dayHeight = maxHeight / WEEK_IN_MONTH;
        minHeight = dayHeight = maxHeight / WEEK_IN_MONTH;
        LogUtil.e("left:" + getPaddingLeft() + " right:" + getPaddingRight() + " top:" + getPaddingTop() + " bottom:" + getPaddingBottom());
        setMeasuredDimension(width, height);
    }

    public void setCurMonth(CalMonth curMonth) {
        this.curMonth = curMonth;
        curDay = curMonth.getFirstDayOfMonth();
    }

    public CalMonth getCurMonth() {
        return curMonth;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        LogUtil.e("maxHeight:" + maxHeight);
    }

    public void setCurHeight(int curHeight) {
        this.curHeight = curHeight;
        invalidate();
    }

    public int getFixHeight() {
        if (curMonth.isLastWeekEnable()) {
            return dayHeight * WEEK_IN_MONTH;
        } else {
            return dayHeight * (WEEK_IN_MONTH - 1);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int top = getPaddingTop();
        int left = getPaddingLeft();
        if (curMonth == null) return;
        int curSelectWeek = curDay == null ? 0 : curDay.getWeekOfMonth() - 1;
        int goneCount = (maxHeight - curHeight) / dayHeight;
        float alpha = (curHeight - minHeight) * 1f / (maxHeight - minHeight);
        if (goneCount < curSelectWeek) {
            int beyondY = curHeight - maxHeight;
            for (int i = 0; i < curMonth.getWeekList().size(); i++) {
                CalWeek calWeek = curMonth.getWeekList().get(i);
                int startY = top + dayHeight * i + beyondY;
                for (int j = 0; j < calWeek.getDayList().size(); j++) {
                    int startX = left + dayWidth * j;
                    drawDayView(canvas, startX, startY,
                            i > curSelectWeek, alpha, calWeek.getDayList().get(j));
                }
            }
        } else {
            float rate = (curHeight - dayHeight) * 1.0f /
                    (maxHeight - (curSelectWeek + 1) * dayHeight);
            for (int i = 0; i < curMonth.getWeekList().size(); i++) {
                CalWeek calWeek = curMonth.getWeekList().get(i);
                int startY;
                if (i < curSelectWeek) {
                    startY = top - dayHeight;
                } else if (i == curSelectWeek) {
                    startY = top;
                } else {
                    startY = top + (int) (dayHeight * rate * (i - curSelectWeek));
                }
                for (int j = 0; j < calWeek.getDayList().size(); j++) {
                    int startX = left + dayWidth * j;
                    drawDayView(canvas, startX, startY,
                            i > curSelectWeek, alpha, calWeek.getDayList().get(j));
                }
            }
        }
    }

    private void drawDayView(Canvas canvas, float startX, float startY,
                             boolean afterSelect, float a0, CalDay calDay) {
        float alpha = afterSelect ? a0 : 1;
        if (startY + dayHeight <= getPaddingTop() || alpha <= 0) return;
        float midX = startX + dayWidth / 2f;
        float midY = startY + dayHeight / 2f;
        boolean isSelect = calDay.equals(curDay);
        boolean isToday = calDay.isToday();
        boolean isEnable = calDay.isEnable();
        boolean isMarked = calDay.isMarked();
        int a = (int) (isEnable ? ALPHA_NORMAL * alpha : ALPHA_ENABLE * alpha);
        //绘制背景圆
        if (isSelect || isToday) {
            if (isSelect) {
                mOvalPaint.setColor(bgColorSelected);
            } else {
                mOvalPaint.setColor(bgColorToday);
            }
            mOvalPaint.setAlpha(a);
            canvas.drawOval(
                    midX - bgRadiusSize, midY - bgRadiusSize,
                    midX + bgRadiusSize, midY + bgRadiusSize, mOvalPaint);
        }
        //绘制日期文字
        if (isSelect || isToday) {
            mTextPaint.setColor(dayColorSelected);
        } else {
            mTextPaint.setColor(dayTextColorNormal);
        }
        mTextPaint.setAlpha(a);
        mTextPaint.setTextSize(dayTextSize);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        // 计算文字baseline
        float textBaseY = (int) (midY - fontMetrics.top / 2 - fontMetrics.bottom / 2 - descTextSize / 4);
        canvas.drawText(String.valueOf(calDay.getSolar().solarDay), midX, textBaseY, mTextPaint);
        //绘制日期描述文字
        if (isSelect || isToday) {
            mTextPaint.setColor(dayColorSelected);
        } else {
            mTextPaint.setColor(descTextColorNormal);
        }
        mTextPaint.setAlpha(a);
        mTextPaint.setTextSize(descTextSize);
        canvas.drawText(calDay.getDayDescription(), midX, textBaseY + descTextSize, mTextPaint);
        //绘制日期头上的标志
        if (isMarked) {
            if (isSelect || isToday) {
                mOvalPaint.setColor(dayColorSelected);
            } else {
                mOvalPaint.setColor(dotColorNormal);
            }
            mOvalPaint.setAlpha(a);
            float dotY = textBaseY - dayTextSize;
            canvas.drawOval(
                    midX - dotSize / 2, dotY - dotSize / 2,
                    midX + dotSize / 2, dotY + dotSize / 2, mOvalPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            LogUtil.e("onSingleTapConfirmed");
            float x = e.getX();
            float y = e.getY();
            int j;
            LogUtil.e("curHeight:" + curHeight + ",maxHeight:" + maxHeight + ",minHeight:" + minHeight);
            if (curHeight >= maxHeight) {
                j = ((int) (y - getPaddingTop())) / dayHeight;
            } else if (curHeight <= minHeight) {
                j = curDay == null ? 0 : curDay.getWeekOfMonth() - 1;
            } else {
                return false;
            }
            int i = ((int) (x - getPaddingLeft())) / dayWidth;
            if (curMonth != null) {
                curDay = curMonth.getWeek(j).getDay(i);
                invalidate();
                LogUtil.e("invalidate");
            }
            return true;
        }
    }
}