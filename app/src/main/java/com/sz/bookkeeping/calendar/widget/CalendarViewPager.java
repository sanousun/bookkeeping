package com.sz.bookkeeping.calendar.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.sz.bookkeeping.R;
import com.sz.bookkeeping.calendar.manager.CalMonth;
import com.sz.bookkeeping.util.LogUtil;
import com.sz.bookkeeping.util.SizeUtils;

import static com.sz.bookkeeping.calendar.manager.CalMonth.WEEK_IN_MONTH;

/**
 * Created with Android Studio.
 * User: dashu
 * Date: 2017/2/23
 * Time: 下午8:23
 * Desc: 自适应高度的viewpager
 */

public class CalendarViewPager extends ViewPager {

    private static final int DURING_COLLAPSE = 300;

    private static final int STATE_WEEK = 0;
    private static final int STATE_MONTH = 1;

    private static final int INVALID_POINTER = -1;
    private int scrollPointerId = INVALID_POINTER;
    private int initTouchX, initTouchY;
    private int touchSlop;

    private int curHeight;
    private int minHeight;
    private int maxHeight;
    private int state;

    private CalendarPagerAdapter adapter;

    public CalendarViewPager(Context context) {
        this(context, null);
    }

    public CalendarViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        final ViewConfiguration vc = ViewConfiguration.get(getContext());
        touchSlop = vc.getScaledTouchSlop();
        addOnPageChangeListener(new OnPageChangeListener() {

            int preState = -1;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                LogUtil.e("position:" + position + " positionOffset:" + positionOffset + " positionOffsetPixels:" + positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                LogUtil.e("state:" + state);
                /* 静止 */
                if (preState != ViewPager.SCROLL_STATE_IDLE
                        && state == ViewPager.SCROLL_STATE_IDLE) {
//                    autoFixHeight();
                }
                /* 滑动开始 */
                else if (preState != ViewPager.SCROLL_STATE_DRAGGING
                        && state == ViewPager.SCROLL_STATE_DRAGGING) {
                    if (adapter == null) {
                        return;
                    }
                    int pos = getCurrentItem();
                    CustomerCalendarView calendar = getCalendarView(pos);
                    if (calendar == null) {
                        return;
                    }
                    CalMonth calMonth = calendar.getCurMonth();
                    CustomerCalendarView pre = getCalendarView(pos - 1);
                    if (pre != null) {
                        pre.setCurMonth(calMonth.pre());
                    }
                    CustomerCalendarView next = getCalendarView(pos + 1);
                    if (next != null) {
                        next.setCurMonth(calMonth.next());
                    }
                }
                /* 滑动结束 */
                else if (preState != ViewPager.SCROLL_STATE_SETTLING
                        && state == ViewPager.SCROLL_STATE_SETTLING) {
                    autoFixHeight();
                } else {
                    preState = state;
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY) {
            width = SizeUtils.getScreenWidth(getContext());
        }
        int height = MeasureSpec.getSize(widthMeasureSpec);
        if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
            height = (width - getPaddingLeft() - getPaddingRight()) * 5 / 7
                    + getPaddingTop() + getPaddingBottom();
        }
        for (int i = 0; i < getChildCount(); i++) {
            measureChildren(
                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        }
        if (curHeight == 0) {
            maxHeight = curHeight = (height - getPaddingTop() - getPaddingBottom());
            minHeight = maxHeight / WEEK_IN_MONTH;
            state = STATE_MONTH;
            LogUtil.e("maxHeight:" + maxHeight);
        }
        setMeasuredDimension(width, curHeight);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        adapter = new CalendarPagerAdapter();
        setAdapter(adapter);
        setCurrentItem(Integer.MAX_VALUE / 2, false);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        final int actionIndex = MotionEventCompat.getActionIndex(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                scrollPointerId = ev.getPointerId(0);
                initTouchX = (int) (ev.getX() + 0.5f);
                initTouchY = (int) (ev.getY() + 0.5f);
                boolean in = super.onInterceptTouchEvent(ev);
                LogUtil.e("intercept down:" + in);
                return in;

            case MotionEventCompat.ACTION_POINTER_DOWN:
                scrollPointerId = ev.getPointerId(actionIndex);
                initTouchX = (int) (ev.getX(actionIndex) + 0.5f);
                initTouchY = (int) (ev.getY(actionIndex) + 0.5f);
                return super.onInterceptTouchEvent(ev);

            case MotionEvent.ACTION_MOVE: {
                final int index = ev.findPointerIndex(scrollPointerId);
                if (index < 0) {
                    return false;
                }
                final int x = (int) (ev.getX(index) + 0.5f);
                final int y = (int) (ev.getY(index) + 0.5f);
                final int dx = x - initTouchX;
                final int dy = y - initTouchY;
                return Math.abs(dx) > touchSlop || Math.abs(dy) > touchSlop
                        || super.onInterceptTouchEvent(ev);
            }
            default:
                return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                final int index = ev.findPointerIndex(scrollPointerId);
                if (index < 0) {
                    return false;
                }
                final int x = (int) (ev.getX(index) + 0.5f);
                final int y = (int) (ev.getY(index) + 0.5f);
                final int dx = x - initTouchX;
                final int dy = y - initTouchY;
//                LogUtil.e("dx:" + dx + " dy:" + dy);
                if (Math.abs(dx) < Math.abs(dy)) {
                    if ((dy < 0 && curHeight == minHeight)
                            || (dy > 0 && curHeight == maxHeight)) {
                        return true;
                    }
                    curHeight += dy;
                    if (curHeight > maxHeight) {
                        curHeight = maxHeight;
                    } else if (curHeight < minHeight) {
                        curHeight = minHeight;
                    }
                    setChildCurHeight();
                    initTouchX = x;
                    initTouchY = y;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (curHeight != minHeight || curHeight != maxHeight) {
                    autoCollapse();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void autoCollapse() {
        if (curHeight == maxHeight || curHeight == minHeight) return;
        int form = curHeight;
        int to;
        if (state == STATE_MONTH) {
            if (maxHeight - curHeight < minHeight / 2) {
                to = maxHeight;
            } else {
                to = minHeight;
            }
        } else {
            if (curHeight - minHeight < minHeight / 2) {
                to = minHeight;
            } else {
                to = maxHeight;
            }
        }
        ValueAnimator valueAnimator = ValueAnimator.ofInt(form, to);
        valueAnimator.setInterpolator(new LinearOutSlowInInterpolator());
        valueAnimator.setDuration(DURING_COLLAPSE);
        valueAnimator.addUpdateListener(va -> {
            curHeight = (int) va.getAnimatedValue();
            setChildCurHeight();
        });
        valueAnimator.start();
    }

    public void autoFixHeight() {
        CustomerCalendarView current = getCalendarView(getCurrentItem());
        if (current == null) return;
        int height = current.getFixHeight();
        if (state == STATE_WEEK) {
            current.setMaxHeight(height);
        } else {
            ValueAnimator valueAnimator = ValueAnimator.ofInt(curHeight, height);
            valueAnimator.setInterpolator(new LinearOutSlowInInterpolator());
            valueAnimator.setDuration(DURING_COLLAPSE);
            valueAnimator.addUpdateListener(va -> {
                curHeight = (int) va.getAnimatedValue();
                setChildFixHeight();
            });
            valueAnimator.start();
        }
    }

    private CustomerCalendarView getCalendarView(int pos) {
        View current = findViewWithTag(pos);
        if (current != null && current instanceof CustomerCalendarView) {
            return (CustomerCalendarView) current;
        }
        return null;
    }

    private void setChildCurHeight() {
        if (curHeight <= minHeight) {
            state = STATE_WEEK;
        } else if (curHeight >= maxHeight) {
            state = STATE_MONTH;
        }
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof CustomerCalendarView) {
                ((CustomerCalendarView) getChildAt(i)).setCurHeight(curHeight);
            }
        }
        requestLayout();
    }

    private void setChildFixHeight() {
        CustomerCalendarView current = getCalendarView(getCurrentItem());
        if (current != null) {
            current.setMaxHeight(curHeight);
            current.setCurHeight(curHeight);
        }
        requestLayout();
    }

    public class CalendarPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.view_cal, container, false);
            view.setTag(position);
            container.addView(view);
            LogUtil.e("instantiateItem");
            return view;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}