package com.sz.bookkeeping.calendar.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

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
 * Desc: 日历控件
 */

public class CalendarView extends View {

    //动画持续时间
    private static final int DURING_ANI = 300;

    private static final int STATE_WEEK = 0;
    private static final int STATE_MONTH = 1;
    //视图状态
    private int mState;

    //month的列数
    private static final int COLUMN_ITEM = 7;
    //month最大的行数，展开状态
    private static final int MAX_ROW_ITEM = 6;
    //month最小的行数，收缩状态
    private static final int MIN_ROW_ITEM = 1;
    //day视图的高度
    private int mDayHeight;
    //day视图的宽度
    private int mDayWidth;
    //当前高度
    private int mCurHeight;
    //横向移动的距离
    private int mCurOffsetHorizontal = 0;

    //无效day视图的透明度
    private static final int ALPHA_ENABLE = 0x40;
    //有效day视图的透明度
    private static final int ALPHA_NORMAL = 0xFF;

    //文字的画笔
    private TextPaint mTextPaint;
    //小圆点及背景圆的画笔
    private Paint mOvalPaint;

    //day视图的字体大小
    private float mDayTextSize;
    //day视图的文字颜色
    private int mDayTextColorNormal;

    //day视图描述文字的字体大小
    private float mDescTextSize;
    //day视图描述文字的字体大小
    private int mDescTextColorNormal;

    //小圆点大小
    private float mDotSize;
    //小圆点的颜色
    private int mDotColorNormal;

    //被选中的day视图文字颜色，包括描述文字颜色和小圆点的颜色
    private int mDayColorSelected;

    //today未被选中的背景颜色
    private int mBgColorToday;
    //被选中的day视图背景颜色
    private int mBgColorSelected;
    //背景圆的大小
    private float mBgRadiusSize;

    //day信息数据
    private CalDay mCurDay;
    //week信息数据
    private CalWeek mCurWeek;
    //month信息数据
    private CalMonth mCurMonth;

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
        mOvalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
        mDayColorSelected = ta.getColor(
                R.styleable.CalendarView_cal_day_color_selected, Color.parseColor("#FFFFFF"));
        mDayTextSize = ta.getDimension(
                R.styleable.CalendarView_cal_day_text_size, SizeUtils.dp2px(context, 16f));
        mDayTextColorNormal = ta.getColor(
                R.styleable.CalendarView_cal_day_text_color, Color.parseColor("#373737"));

        mDescTextSize = ta.getDimension(
                R.styleable.CalendarView_cal_desc_text_size, SizeUtils.dp2px(context, 8f));
        mDescTextColorNormal = ta.getColor(
                R.styleable.CalendarView_cal_desc_text_color, Color.parseColor("#AFAFAF"));

        mDotSize = ta.getDimension(
                R.styleable.CalendarView_cal_dot_size, SizeUtils.dp2px(context, 3f));
        mDotColorNormal = ta.getColor(
                R.styleable.CalendarView_cal_dot_color, Color.parseColor("#FF5252"));

        mBgColorToday = ta.getColor(
                R.styleable.CalendarView_cal_bg_color_today, Color.parseColor("#E7E7E7"));
        mBgColorSelected = ta.getColor(
                R.styleable.CalendarView_cal_bg_color_selected, Color.parseColor("#4CAF50"));
        mBgRadiusSize = ta.getDimension(
                R.styleable.CalendarView_cal_bg_radius_size, SizeUtils.dp2px(context, 18f));
        ta.recycle();

        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = configuration.getScaledMaximumFlingVelocity();

        initDefaultDay();
    }

    /**
     * 初始化month信息和day信息
     */
    private void initDefaultDay() {
        mCurDay = CalDay.getToday();
        mCurWeek = mCurDay.getCalWeekForCurrentDayMonth();
        mCurMonth = mCurDay.getCalMonth();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY) {
            width = SizeUtils.getScreenWidth(getContext());
        }
        int horizontalSpace = getPaddingLeft() + getPaddingRight();
        int verticalSpace = getPaddingTop() + getPaddingBottom();

        mDayWidth = (int) ((width - horizontalSpace) * 1f / COLUMN_ITEM);
        mDayHeight = (int) (mDayWidth * 5f / 7);
        int viewHeight = mDayHeight * MAX_ROW_ITEM + verticalSpace;
        mCurHeight = viewHeight - verticalSpace;
        mCurOffsetHorizontal = 0;

        LogUtil.e("left:" + getPaddingLeft()
                + ", right:" + getPaddingRight()
                + ", top:" + getPaddingTop()
                + ", bottom:" + getPaddingBottom());
        setMeasuredDimension(width, viewHeight);
    }

    public void setCurMonth(int year, int month) {
        mCurDay = new CalDay(year, month, 1);
        mCurWeek = mCurDay.getCalWeekForCurrentDayMonth();
        mCurMonth = mCurDay.getCalMonth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //根据curHeight获取视图状态
        if (mCurMonth == null) return;
        if (mCurHeight == MAX_ROW_ITEM * mDayHeight) {
            mState = STATE_MONTH;
            drawViewWithoutExpandVertical(canvas);
        } else if (mCurHeight == MIN_ROW_ITEM * mDayHeight) {
            mState = STATE_WEEK;
            drawViewWithoutExpandVertical(canvas);
        } else {
            drawViewWithoutMoveHorizontal(canvas);
        }
    }

    /**
     * 绘制没有处于上下伸展状态下的view，此时视图可能左右移动
     */
    private void drawViewWithoutExpandVertical(Canvas canvas) {
        //这是对横向移动的处理
        if (mCurOffsetHorizontal == mDayWidth * COLUMN_ITEM) {
            mCurOffsetHorizontal = 0;
            if (mState == STATE_WEEK) {
                mCurDay = mCurDay.weekPre();
                mCurWeek = mCurDay.getCalWeekForCurrentDayMonth();
                mCurMonth = mCurDay.getCalMonth();
            } else {
                mCurMonth = mCurMonth.pre();
                if (mCurMonth.isContain(mCurDay)) {
                    mCurDay.setEnable(true);
                    mCurWeek = mCurDay.getCalWeekForCurrentDayMonth();
                } else {
                    mCurWeek = mCurMonth.getFirstWeek();
                    mCurDay = mCurMonth.getFirstDayOfMonth();
                }
            }
        } else if (mCurOffsetHorizontal == -mDayWidth * COLUMN_ITEM) {
            mCurOffsetHorizontal = 0;
            if (mState == STATE_WEEK) {
                mCurDay = mCurDay.weekNext();
                mCurWeek = mCurDay.getCalWeekForCurrentDayMonth();
                mCurMonth = mCurDay.getCalMonth();
            } else {
                mCurMonth = mCurMonth.next();
                if (mCurMonth.isContain(mCurDay)) {
                    mCurDay.setEnable(true);
                    mCurWeek = mCurDay.getCalWeekForCurrentDayMonth();
                } else {
                    mCurWeek = mCurMonth.getFirstWeek();
                    mCurDay = mCurMonth.getFirstDayOfMonth();
                }
            }
        }
        //周视图状态下
        if (mState == STATE_WEEK) {
            if (mCurOffsetHorizontal > 0) {
                CalDay pre = mCurDay.weekPre();
                drawWeekView(canvas, -mDayWidth * COLUMN_ITEM,
                        pre.getCalWeekForCurrentDayMonth(), pre);
            }
            if (mCurOffsetHorizontal < 0) {
                CalDay next = mCurDay.weekNext();
                drawWeekView(canvas, mDayWidth * COLUMN_ITEM,
                        next.getCalWeekForCurrentDayMonth(), next);
            }
            drawWeekView(canvas, 0, mCurWeek, mCurDay);
        } else {
            if (mCurOffsetHorizontal > 0) {
                CalMonth pre = mCurMonth.pre();
                drawMonthView(canvas, -mDayWidth * COLUMN_ITEM,
                        pre, pre.isContain(mCurDay) ? mCurDay : pre.getFirstDayOfMonth());
            }
            if (mCurOffsetHorizontal < 0) {
                CalMonth next = mCurMonth.next();
                drawMonthView(canvas, mDayWidth * COLUMN_ITEM,
                        next, next.isContain(mCurDay) ? mCurDay : next.getFirstDayOfMonth());
            }
            drawMonthView(canvas, 0, mCurMonth, mCurDay);
        }
    }

    /**
     * 周视图状态下的绘制
     *
     * @param canvas  画布
     * @param offsetX 整个视图的偏移量，并不是左右滑动的偏移
     * @param calWeek 周数据信息
     * @param calDay  被选中的day数据信息
     */

    private void drawWeekView(Canvas canvas, int offsetX, CalWeek calWeek, CalDay calDay) {
        for (int i = 0; i < calWeek.getDayList().size(); i++) {
            int startX = getPaddingLeft() + mDayWidth * i + mCurOffsetHorizontal + offsetX;
            drawDayView(canvas,
                    startX, getPaddingTop(),
                    1f,
                    calWeek.getDayList().get(i), calDay);
        }
    }

    /**
     * 月视图状态下的绘制
     *
     * @param canvas   画布
     * @param offsetX  整个视图的偏移量，并不是左右滑动的偏移
     * @param calMonth 月数据信息
     * @param calDay   被选中的day数据信息
     */
    private void drawMonthView(Canvas canvas, int offsetX, CalMonth calMonth, CalDay calDay) {
        for (int i = 0; i < calMonth.getWeekList().size(); i++) {
            CalWeek calWeek = calMonth.getWeekList().get(i);
            //计算起始y
            int startY = getPaddingTop() + mDayHeight * i;
            for (int j = 0; j < calWeek.getDayList().size(); j++) {
                //计算起始x
                int startX = getPaddingLeft() + mDayWidth * j + mCurOffsetHorizontal + offsetX;
                drawDayView(canvas,
                        startX, startY,
                        1f,
                        calWeek.getDayList().get(j), calDay);
            }
        }
    }

    /**
     * 绘制处于上下伸展状态下的view，此时视图不能左右移动
     */
    private void drawViewWithoutMoveHorizontal(Canvas canvas) {
        int selectRow = mCurDay == null ? 0 : mCurDay.getWeekOfMonth() - 1;
        //因为滑动导致的隐藏的行数
        int goneRow = (mDayHeight * MAX_ROW_ITEM - mCurHeight) / mDayHeight;
        //用于计算对应行数的透明度
        float alpha = (mCurHeight - mDayHeight * MIN_ROW_ITEM) * 1f
                / (mDayHeight * MAX_ROW_ITEM - mDayHeight * MIN_ROW_ITEM);
        //隐藏行数小于当前被选中的行数，隐藏行数不会绘制
        if (goneRow < selectRow) {
            //计算出第一行距离上边的距离，这个为负数，当整行bottom都小于view的top不需要绘制
            int beyondY = mCurHeight - mDayHeight * MAX_ROW_ITEM;
            drawMonthView1(canvas, selectRow, alpha, beyondY, mCurMonth);
        }
        //隐藏行数大于等于被选中的行数，第一行是被选中的需要展示全部，其他行数需要做特殊处理
        else {
            //由于第一行中的元素被选中，需要对后面的行数进行特殊处理，距离view的top距离需要重新算，还要加上透明度
            float rate = (mCurHeight - mDayHeight) * 1.0f
                    / (mDayHeight * MAX_ROW_ITEM - (selectRow + 1) * mDayHeight);
            drawMonthView2(canvas, selectRow, alpha, rate, mCurMonth);
        }
    }

    /**
     * 不可见的高度未超过被选中的行
     *
     * @param canvas    画布
     * @param selectRow 被选中的行
     * @param alpha     行的透明
     * @param beyondY   y的起始高度
     * @param calMonth  月数据信息
     */
    private void drawMonthView1(Canvas canvas, int selectRow, float alpha, int beyondY, CalMonth calMonth) {
        for (int i = 0; i < calMonth.getWeekList().size(); i++) {
            CalWeek calWeek = calMonth.getWeekList().get(i);
            //计算起始y
            int startY = getPaddingTop() + mDayHeight * i + beyondY;
            for (int j = 0; j < calWeek.getDayList().size(); j++) {
                //计算起始x
                int startX = getPaddingLeft() + mDayWidth * j + mCurOffsetHorizontal;
                drawDayView(canvas,
                        startX, startY,
                        (i <= selectRow ? 1f : alpha),
                        calWeek.getDayList().get(j), mCurDay);
            }
        }
    }

    /**
     * 不可见的高度超过被选中的行，选中的行永远保留在上方，底下的行需要进行特殊处理，加上透明度及计算距离顶部的高度
     *
     * @param canvas    画布
     * @param selectRow 被选中的行
     * @param alpha     行的透明度
     * @param rate      行距离顶部的高度占比
     * @param calMonth  月结构数据
     */
    private void drawMonthView2(Canvas canvas, int selectRow, float alpha, float rate, CalMonth calMonth) {
        for (int i = 0; i < calMonth.getWeekList().size(); i++) {
            CalWeek calWeek = calMonth.getWeekList().get(i);
            int startY;
            if (i < selectRow) {
                startY = getPaddingTop() - mDayHeight;
            }
            //被选中的row不会被隐藏
            else if (i == selectRow) {
                startY = getPaddingTop();
            }
            //被选中的row上方的row需要特殊处理
            else {
                startY = getPaddingTop() + (int) (mDayHeight * rate * (i - selectRow));
            }
            for (int j = 0; j < calWeek.getDayList().size(); j++) {
                int startX = getPaddingLeft() + mDayWidth * j + mCurOffsetHorizontal;
                drawDayView(canvas,
                        startX, startY,
                        (i <= selectRow ? 1f : alpha),
                        calWeek.getDayList().get(j), mCurDay);
            }
        }
    }


    /**
     * 绘制day视图
     *
     * @param canvas    画布
     * @param startX    起始位置x
     * @param startY    起始位置y
     * @param dayAlpha  day视图的透明度
     * @param calDay    day数据
     * @param selectDay 被选中的day数据
     */
    private void drawDayView(Canvas canvas, float startX, float startY, float dayAlpha,
                             CalDay calDay, CalDay selectDay) {
        //透明度小于0，不绘制
        if (dayAlpha <= 0) {
            return;
        }
        //起始的Y在view上方-dayHeight距离以外不绘制
        if (startY + mDayHeight <= getPaddingTop()) {
            return;
        }
        //起始的X在view左侧-dayWidth距离以外或者右侧以外不绘制
        if (startX + mDayWidth <= getPaddingLeft() || startX > (getPaddingLeft() + mDayWidth * COLUMN_ITEM)) {
            return;
        }
        float midX = startX + mDayWidth / 2f;
        float midY = startY + mDayHeight / 2f;
        boolean isSelect = calDay.equals(selectDay);
        boolean isToday = calDay.isToday();
        boolean isEnable = calDay.isEnable();
        boolean isMarked = calDay.isMarked();
        int a = (int) (isEnable ? ALPHA_NORMAL * dayAlpha : ALPHA_ENABLE * dayAlpha);
        //绘制背景圆
        if (isSelect || isToday) {
            if (isSelect) {
                mOvalPaint.setColor(mBgColorSelected);
            } else {
                mOvalPaint.setColor(mBgColorToday);
            }
            mOvalPaint.setAlpha(a);
            canvas.drawOval(
                    midX - mBgRadiusSize, midY - mBgRadiusSize,
                    midX + mBgRadiusSize, midY + mBgRadiusSize, mOvalPaint);
        }
        //绘制日期文字
        if (isSelect || isToday) {
            mTextPaint.setColor(mDayColorSelected);
        } else {
            mTextPaint.setColor(mDayTextColorNormal);
        }
        mTextPaint.setAlpha(a);
        mTextPaint.setTextSize(mDayTextSize);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        //计算文字baseline
        float textBaseY = (int) (midY - fontMetrics.top / 2 - fontMetrics.bottom / 2 - mDescTextSize / 4);
        canvas.drawText(String.valueOf(calDay.getSolar().solarDay), midX, textBaseY, mTextPaint);
        //绘制日期描述文字
        if (isSelect || isToday) {
            mTextPaint.setColor(mDayColorSelected);
        } else {
            mTextPaint.setColor(mDescTextColorNormal);
        }
        mTextPaint.setAlpha(a);
        mTextPaint.setTextSize(mDescTextSize);
        canvas.drawText(calDay.getDayDescription(), midX, textBaseY + mDescTextSize, mTextPaint);
        //绘制日期头上的标志
        if (isMarked) {
            if (isSelect || isToday) {
                mOvalPaint.setColor(mDayColorSelected);
            } else {
                mOvalPaint.setColor(mDotColorNormal);
            }
            mOvalPaint.setAlpha(a);
            float dotY = textBaseY - mDayTextSize;
            canvas.drawOval(
                    midX - mDotSize / 2, dotY - mDotSize / 2,
                    midX + mDotSize / 2, dotY + mDotSize / 2, mOvalPaint);
        }
    }

    private int mInitTouchX, mInitTouchY;
    private boolean isAutoMove;
    private boolean isVerticalMoved;
    private boolean isHorizontalMoved;
    private boolean isHorizontalFling;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        if (!isAutoMove) {
            obtainVelocityTracker(ev);
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    isVerticalMoved = false;
                    isHorizontalMoved = false;
                    mInitTouchX = (int) (ev.getX() + 0.5f);
                    mInitTouchY = (int) (ev.getY() + 0.5f);
                    break;
                case MotionEvent.ACTION_MOVE:
                    int x = (int) (ev.getX() + 0.5f);
                    int y = (int) (ev.getY() + 0.5f);
                    int dx = x - mInitTouchX;
                    int dy = y - mInitTouchY;
                    if (!isHorizontalMoved && !isVerticalMoved &&
                            (Math.abs(dy) > mTouchSlop || Math.abs(dx) > mTouchSlop)) {
                        if (Math.abs(dx) < Math.abs(dy)) {
                            isVerticalMoved = true;
                            expandVertical(dy);
                        } else {
                            isHorizontalMoved = true;
                            moveHorizontal(dx);
                        }
                        mInitTouchX = x;
                        mInitTouchY = y;
                    } else if (isVerticalMoved) {
                        expandVertical(dy);
                        mInitTouchX = x;
                        mInitTouchY = y;
                    } else if (isHorizontalMoved) {
                        moveHorizontal(dx);
                        mInitTouchX = x;
                        mInitTouchY = y;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (!isVerticalMoved && !isHorizontalMoved) {
                        click();
                    } else if (isVerticalMoved) {
                        autoExpandVertical();
                    } else {
                        final VelocityTracker velocityTracker = mVelocityTracker;
                        velocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
                        int initialVelocity = (int) velocityTracker.getYVelocity();
                        isHorizontalFling = Math.abs(initialVelocity) > mMinFlingVelocity;
                        autoMoveHorizontal();
                        releaseVelocityTracker();
                    }
                    isVerticalMoved = false;
                    isHorizontalMoved = false;
                    break;
                default:
            }
        }
        return true;
    }

    private void obtainVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * 根据移动距离将view展开或者收缩
     *
     * @param dy 竖直移动距离
     */
    private void expandVertical(int dy) {
        mCurHeight += dy;
        if (mCurHeight > mDayHeight * MAX_ROW_ITEM) {
            mCurHeight = mDayHeight * MAX_ROW_ITEM;
        } else if (mCurHeight < mDayHeight * MIN_ROW_ITEM) {
            mCurHeight = mDayHeight * MIN_ROW_ITEM;
        }
        invalidate();
    }

    /**
     * 根据移动距离左右移动
     *
     * @param dx 水平移动距离
     */
    private void moveHorizontal(int dx) {
        mCurOffsetHorizontal += dx;
        if (mCurOffsetHorizontal > mDayWidth * COLUMN_ITEM) {
            mCurOffsetHorizontal = mDayWidth * COLUMN_ITEM;
        } else if (mCurOffsetHorizontal < -mDayWidth * COLUMN_ITEM) {
            mCurOffsetHorizontal = -mDayWidth * COLUMN_ITEM;
        }
        invalidate();
    }

    private void autoMoveHorizontal() {
        int maxWidth = COLUMN_ITEM * mDayWidth;
        int from = mCurOffsetHorizontal;
        int to = 0;
        if (mCurOffsetHorizontal == 0) return;
        if (mCurOffsetHorizontal > 0) {
            if (mCurOffsetHorizontal >= maxWidth * 4f / 7 || isHorizontalFling) {
                to = maxWidth;
            }
        } else if (mCurOffsetHorizontal < 0) {
            if (mCurOffsetHorizontal <= -maxWidth * 4f / 7 || isHorizontalFling) {
                to = -maxWidth;
            }
        }

        ValueAnimator valueAnimator = ValueAnimator.ofInt(from, to);
        valueAnimator.setInterpolator(new

                LinearOutSlowInInterpolator());
        valueAnimator.setDuration(DURING_ANI);
        valueAnimator.addUpdateListener(va -> {
            int target = (int) va.getAnimatedValue();
            if (mCurOffsetHorizontal != target
                    && !(mCurOffsetHorizontal == 0 && target == maxWidth)
                    && !(mCurOffsetHorizontal == 0 && target == -maxWidth)) {
                mCurOffsetHorizontal = target;
                invalidate();
                LogUtil.e(mCurOffsetHorizontal + "");
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAutoMove = true;
                isHorizontalFling = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAutoMove = false;
            }
        });
        valueAnimator.start();
    }

    /**
     * 当触摸事件结束将会自动触发展开或者收回操作
     */
    private void autoExpandVertical() {
        int maxHeight = MAX_ROW_ITEM * mDayHeight;
        int minHeight = MIN_ROW_ITEM * mDayHeight;
        if (mCurHeight == maxHeight || mCurHeight == minHeight) return;
        int from = mCurHeight;
        int to;
        if (mState == STATE_MONTH) {
            if (maxHeight - mCurHeight < mDayHeight / 2) {
                to = maxHeight;
            } else {
                to = minHeight;
            }
        } else {
            if (mCurHeight - minHeight < mDayHeight / 2) {
                to = minHeight;
            } else {
                to = maxHeight;
            }
        }
        ValueAnimator valueAnimator = ValueAnimator.ofInt(from, to);
        valueAnimator.setInterpolator(new LinearOutSlowInInterpolator());
        valueAnimator.setDuration(DURING_ANI);
        valueAnimator.addUpdateListener(va -> {
            if (mCurHeight != (int) va.getAnimatedValue()) {
                mCurHeight = (int) va.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAutoMove = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAutoMove = false;
            }
        });
        valueAnimator.start();
    }

    /**
     * 点击事件
     */
    private void click() {
        if (mCurMonth == null) return;
        int i = (mInitTouchX - getPaddingLeft()) / mDayWidth;
        if (mState == STATE_MONTH) {
            //月视图状态的week行数
            int j = (mInitTouchY - getPaddingTop()) / mDayHeight;
            mCurWeek = mCurMonth.getWeekList().get(j);
            mCurDay = mCurWeek.getDayList().get(i);
            if (!mCurDay.isEnable()) {
                CalMonth calMonth = mCurDay.getCalMonth();
                if (calMonth.getMonth() > mCurMonth.getMonth()) {
                    mCurOffsetHorizontal = -1;
                } else if (calMonth.getMonth() < mCurMonth.getMonth()) {
                    mCurOffsetHorizontal = 1;
                }
                isHorizontalFling = true;
                autoMoveHorizontal();
                return;
            }
        } else if (mState == STATE_WEEK) {
            //周视图状态当前行的week行数
            mCurDay = mCurWeek.getDayList().get(i);
            mCurWeek = mCurDay.getCalWeekForCurrentDayMonth();
        } else {
            return;
        }
        //获取选中的day
        invalidate();
    }
}