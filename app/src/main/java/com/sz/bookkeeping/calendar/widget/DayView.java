package com.sz.bookkeeping.calendar.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sz.bookkeeping.R;
import com.sz.bookkeeping.calendar.manager.CalDay;
import com.sz.bookkeeping.util.SizeUtils;

/**
 * Created with Android Studio.
 * User: dashu
 * Date: 2017/2/10
 * Time: 上午10:11
 * Desc: 日历中的天视图
 */

public class DayView extends LinearLayout {

    private CalDay mCalDay;

    private TextView mDayTv;
    private TextView mDescTv;
    private ImageView mDotIv;
    private View mBgView;

    private int dayColorSelected;
    private float dayTextSize;
    private int dayTextColorNormal;
    private float descTextSize;
    private int descTextColorNormal;
    private float dotSize;
    private int dotColorNormal;
    private int bgColorToday;
    private int bgColorSelected;

    public DayView(Context context) {
        this(context, null);
    }

    public DayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        inflate(context, R.layout.view_day, this);
        mBgView = findViewById(R.id.view_bg);
        mDayTv = (TextView) findViewById(R.id.tv_day);
        mDescTv = (TextView) findViewById(R.id.tv_desc);
        mDotIv = (ImageView) findViewById(R.id.iv_dot);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DayView);
        dayColorSelected = ta.getColor(
                R.styleable.DayView_day_color_selected, Color.parseColor("#FFFFFF"));
        String dayText = ta.getString(R.styleable.DayView_day_text);
        dayTextSize = ta.getDimension(
                R.styleable.DayView_day_text_size, 16f);
        dayTextColorNormal = ta.getColor(
                R.styleable.DayView_day_text_color, Color.parseColor("#373737"));
        initDayTv(dayText);

        String descText = ta.getString(R.styleable.DayView_desc_text);
        descTextSize = ta.getDimension(R.styleable.DayView_desc_text_size, 8f);
        descTextColorNormal = ta.getColor(
                R.styleable.DayView_desc_text_color, Color.parseColor("#AFAFAF"));
        initDescTv(descText);

        dotSize = ta.getDimension(
                R.styleable.DayView_dot_size, SizeUtils.dp2px(context, 3f));
        dotColorNormal = ta.getColor(
                R.styleable.DayView_dot_color, Color.parseColor("#FF5252"));
        initDotIv();

        bgColorToday = ta.getColor(
                R.styleable.DayView_bg_color_today, Color.TRANSPARENT);
        bgColorSelected = ta.getColor(
                R.styleable.DayView_bg_color_selected, Color.parseColor("#4CAF50"));
        initBgOval();

        ta.recycle();
        if (mCalDay != null) {
            initData();
        }
    }

    /**
     * 初始化日期文字
     */
    private void initDayTv(String dayText) {
        int[] dayColors = {dayColorSelected, dayTextColorNormal};
        int[][] dayStates = new int[2][];
        dayStates[0] = new int[]{android.R.attr.state_selected};
        dayStates[1] = new int[]{};
        ColorStateList dayColor = new ColorStateList(dayStates, dayColors);
        mDayTv.setTextColor(dayColor);
        mDayTv.setTextSize(dayTextSize);
        mDayTv.setText(dayText);
    }

    /**
     * 初始化日期的描述文字
     */
    private void initDescTv(String descText) {
        if (mDescTv == null) return;
        int[] descColors = {dayColorSelected, descTextColorNormal};
        int[][] descStates = new int[2][];
        descStates[0] = new int[]{android.R.attr.state_selected};
        descStates[1] = new int[]{};
        ColorStateList descColor = new ColorStateList(descStates, descColors);
        mDescTv.setTextColor(descColor);
        mDescTv.setTextSize(descTextSize);
        mDescTv.setText(descText);
    }

    /**
     * 初始化日期的标志点
     */
    private void initDotIv() {
        if (mDotIv == null) return;
        GradientDrawable dotSelected = new GradientDrawable();
        dotSelected.setShape(GradientDrawable.OVAL);
        dotSelected.setSize((int) (dotSize + 0.5), (int) (dotSize + 0.5));
        dotSelected.setColor(dayColorSelected);
        GradientDrawable dotNormal = new GradientDrawable();
        dotNormal.setShape(GradientDrawable.OVAL);
        dotNormal.setSize((int) (dotSize + 0.5), (int) (dotSize + 0.5));
        dotNormal.setColor(dotColorNormal);
        StateListDrawable dotDb = new StateListDrawable();
        dotDb.addState(new int[]{android.R.attr.state_selected}, dotSelected);
        dotDb.addState(new int[]{}, dotNormal);
        mDotIv.setImageDrawable(dotDb);
    }

    /**
     * 初始化背景圆
     */
    private void initBgOval() {
        if (mBgView == null) return;
        GradientDrawable bgSelected = new GradientDrawable();
        bgSelected.setColor(bgColorSelected);
        GradientDrawable bgNormal = new GradientDrawable();
        bgNormal.setShape(GradientDrawable.OVAL);
        bgSelected.setShape(GradientDrawable.OVAL);
        if (mCalDay == null || !mCalDay.isToday()) {
            bgNormal.setColor(Color.TRANSPARENT);
        } else {
            bgNormal.setColor(bgColorToday);
        }
        StateListDrawable bgDb = new StateListDrawable();
        bgDb.addState(new int[]{android.R.attr.state_selected}, bgSelected);
        bgDb.addState(new int[]{}, bgNormal);
        mBgView.setBackground(bgDb);
    }

    public CalDay getCalDay() {
        return mCalDay;
    }

    public void setCalDay(CalDay calDay) {
        mCalDay = calDay;
        if (mDayTv != null && mDescTv != null
                && mDotIv != null) {
            initData();
        }
    }

    private void initData() {
        setAlpha(mCalDay.isEnable() ? 1 : 0.3f);
        mDayTv.setText(String.valueOf(mCalDay.getSolar().solarDay));
        mDescTv.setText(mCalDay.getDayDescription());
        mDotIv.setVisibility(mCalDay.isMarked() ? VISIBLE : GONE);
        initBgOval();
    }
}