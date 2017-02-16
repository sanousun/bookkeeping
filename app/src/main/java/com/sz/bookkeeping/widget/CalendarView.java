package com.sz.bookkeeping.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.sz.bookkeeping.util.SizeUtils;
import com.sz.bookkeeping.util.SolarDate;

/**
 * Created with Android Studio.
 * User: dashu
 * Date: 2017/2/9
 * Time: 下午5:29
 * Desc: 日历视图控件，默认状态为月视图
 */

public class CalendarView extends View {


    private static final float RATE_FOR_HW = 0.75f;

    private SolarDate[][] mSolarDates;
    private int mColumn;
    private float mColumnWidth;
    private int mRow;
    private float mRowHeight;

    private int mCurrentMonth;
    private int mCurrentDay;
    private int mSelectDay;

    //周视图模式高度即最小高度
    private int mWeekModeHeight;
    //月视图模式高度即最大高度
    private int mMonthModeHeight;
    //当前的高度
    private int mCurrentHeight;

    private TextPaint mTextPaint;
    private Point mCenterPoint;
    private float mDayTextSize;
    private int mDayTextColor;
    private int mOutOfMonthDayTextColor;

    private float mDescTextSize;
    private int mDescTextColor;

    private Paint mOvalPaint;
    private float mDotRadius;
    private int mDotColor;
    private float mBgRadius;

    private int mSelectColor;
    private int mSelectBgColor;
    private int mCurrentBgColor;

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
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mCenterPoint = new Point();
        mDayTextSize = SizeUtils.sp2px(context, 16f);
        mDayTextColor = Color.parseColor("#373737");
        mOutOfMonthDayTextColor = Color.parseColor("#AFAFAF");

        mDescTextSize = SizeUtils.sp2px(context, 8f);
        mDescTextColor = Color.parseColor("#AFAFAF");

        mOvalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgRadius = mDayTextSize + mDescTextSize / 6;
        mDotColor = Color.parseColor("#388E3C");
        mDotRadius = SizeUtils.dp2px(context, 1);

        mSelectColor = Color.parseColor("#FCFCFC");
        mSelectBgColor = Color.parseColor("#4CAF50");
        mCurrentBgColor = Color.parseColor("#AFAFAF");
        initTest();
        initColumnAndRow();

        mGestureDetector = new GestureDetector(context, new MyGestureDetectorListener());
    }

    private void initColumnAndRow() {
        mRow = mSolarDates.length;
        mColumn = mSolarDates[0].length;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            widthSize = SizeUtils.getScreenWidth(getContext());
        }
        mColumnWidth = (widthSize - getPaddingLeft() - getPaddingRight()) * 1.0f / mColumn;
        if (heightMode != MeasureSpec.EXACTLY) {
            heightSize = (int) (mColumnWidth * RATE_FOR_HW * mRow) + getPaddingTop() + getPaddingBottom();
        }
        mRowHeight = (heightSize - getPaddingTop() - getPaddingBottom()) * 1.0f / mRow;
        mWeekModeHeight = (int) (mRowHeight + getPaddingTop() + getPaddingBottom());
        mMonthModeHeight = heightSize;
        mCurrentHeight = mMonthModeHeight;
        setMeasuredDimension(widthSize, mCurrentHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isDataValid()) return;
        int left = getPaddingLeft();
        int top = getPaddingTop();
        for (int i = 0; i < mRow; i++) {
            for (int j = 0; j < mColumn; j++) {
                SolarDate solarDate = mSolarDates[i][j];
                mCenterPoint.set(
                        (int) (left + (j + 0.5f) * mColumnWidth),
                        (int) (top + (i + 0.5f) * mRowHeight));
                drawDay(canvas, mCenterPoint, solarDate);
            }
        }
    }

    private void drawDay(Canvas canvas, Point center, SolarDate solarDate) {
        if (solarDate == null) return;
        boolean isCurrentMonth = (solarDate.solarMonth == mCurrentMonth);
        boolean isCurrentDay = (solarDate.solarDay == mCurrentDay);
        boolean isSelectDay = (solarDate.solarDay == mSelectDay);
        if (isCurrentMonth && (isCurrentDay || isSelectDay)) {
            if (isSelectDay) {
                mOvalPaint.setColor(mSelectBgColor);
            } else {
                mOvalPaint.setColor(mCurrentBgColor);
            }
            canvas.drawOval(
                    center.x - mBgRadius, center.y - mBgRadius,
                    center.x + mBgRadius, center.y + mBgRadius,
                    mOvalPaint);
        }
        mTextPaint.setTextSize(mDayTextSize);
        if (isCurrentMonth) {
            if (isCurrentDay || isSelectDay) {
                mTextPaint.setColor(mSelectColor);
            } else {
                mTextPaint.setColor(mDayTextColor);
            }
        } else {
            mTextPaint.setColor(mOutOfMonthDayTextColor);
        }
        Paint.FontMetrics pf = mTextPaint.getFontMetrics();
        int baseLineY = (int) (center.y - pf.top / 2 - pf.bottom / 2 - mDescTextSize / 4);
        canvas.drawText(String.valueOf(solarDate.solarDay), center.x, baseLineY, mTextPaint);
        mTextPaint.setTextSize(mDescTextSize);
        if (isCurrentMonth && (isSelectDay || isCurrentDay)) {
            mTextPaint.setColor(mSelectColor);
        } else {
            mTextPaint.setColor(mDescTextColor);
        }
        canvas.drawText(solarDate.desc, center.x, baseLineY + mDescTextSize, mTextPaint);
    }

    private boolean isDataValid() {
        return !(mSolarDates == null || mSolarDates.length == 0
                || mSolarDates[0] == null || mSolarDates[0].length == 0);
    }

    private void initTest() {
        SolarDate[][] solarDates = new SolarDate[6][7];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                SolarDate solarDate = new SolarDate();
                int day = i * 7 + j + 1;
                solarDate.solarDay = day % 31;
                solarDate.solarMonth = day / 31 + 1;
                solarDate.desc = "中秋节";
                solarDates[i][j] = solarDate;
            }
        }
        mSolarDates = solarDates;
        mCurrentMonth = 1;
        mCurrentDay = 2;
        mSelectDay = 1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    private class MyGestureDetectorListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("xyz", "onSingleTapConfirmed() --> e:" + e.toString());
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.i("xyz", "onScroll() --> e1:" + e1.toString() + " e2:" + e2.toString() + " dx:" + distanceX + " dy:" + distanceY);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.i("xyz", "onFling() --> e1:" + e1.toString() + " e2:" + e2.toString() + " vx:" + velocityX + " vy:" + velocityY);
            return true;
        }
    }
}