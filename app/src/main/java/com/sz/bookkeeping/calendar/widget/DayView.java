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
import com.sz.bookkeeping.util.SizeUtils;
import com.sz.bookkeeping.util.StringUtils;

/**
 * Created with Android Studio.
 * User: dashu
 * Date: 2017/2/10
 * Time: 上午10:11
 * Desc: 日历中的天视图
 */

public class DayView extends LinearLayout {

    private TextView mDayTv;
    private TextView mDescTv;
    private ImageView mDotIv;
    private View mBgView;

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
        String dayText = ta.getString(R.styleable.DayView_day_text);
        float dayTextSize = ta.getDimension(
                R.styleable.DayView_day_text_size, 16f);
        int dayTextColorNormal = ta.getColor(
                R.styleable.DayView_day_text_color, Color.parseColor("#373737"));
        int dayTextColorSelected = ta.getColor(
                R.styleable.DayView_day_text_color_selected, Color.parseColor("#FFFFFF"));
        int[] dayColors = {dayTextColorSelected, dayTextColorNormal};
        int[][] dayStates = new int[2][];
        dayStates[0] = new int[]{android.R.attr.state_selected};
        dayStates[1] = new int[]{};
        ColorStateList dayColor = new ColorStateList(dayStates, dayColors);

        mDayTv.setTextColor(dayColor);
        mDayTv.setTextSize(dayTextSize);
        mDayTv.setText(dayText);

        String descText = ta.getString(R.styleable.DayView_desc_text);
        float descTextSize = ta.getDimension(R.styleable.DayView_desc_text_size, 8f);
        int descTextColorNormal = ta.getColor(
                R.styleable.DayView_desc_text_color, Color.parseColor("#AFAFAF"));
        int descTextColorSelected = ta.getColor(
                R.styleable.DayView_desc_text_color_selected, Color.parseColor("#FCFCFC"));
        int[] descColors = {descTextColorSelected, descTextColorNormal};
        int[][] descStates = new int[2][];
        descStates[0] = new int[]{android.R.attr.state_selected};
        descStates[1] = new int[]{};
        ColorStateList descColor = new ColorStateList(descStates, descColors);

        mDescTv.setTextColor(descColor);
        mDescTv.setTextSize(descTextSize);
        mDescTv.setText(descText);

        float dotSize = ta.getDimension(
                R.styleable.DayView_dot_size, SizeUtils.dp2px(context, 3f));
        int dotColorNormal = ta.getColor(
                R.styleable.DayView_dot_color, Color.parseColor("#FF5252"));
        int dotColorSelected = ta.getColor(
                R.styleable.DayView_dot_color_selected, Color.parseColor("#FFFFFF"));
        GradientDrawable dotSelected = new GradientDrawable();
        dotSelected.setShape(GradientDrawable.OVAL);
        dotSelected.setSize((int) (dotSize + 0.5), (int) (dotSize + 0.5));
        dotSelected.setColor(dotColorSelected);
        GradientDrawable dotNormal = new GradientDrawable();
        dotNormal.setShape(GradientDrawable.OVAL);
        dotNormal.setSize((int) (dotSize + 0.5), (int) (dotSize + 0.5));
        dotNormal.setColor(dotColorNormal);
        StateListDrawable dotDb = new StateListDrawable();
        dotDb.addState(new int[]{android.R.attr.state_selected}, dotSelected);
        dotDb.addState(new int[]{}, dotNormal);
        mDotIv.setImageDrawable(dotDb);

        int bgColorNormal = ta.getColor(
                R.styleable.DayView_bg_color, Color.TRANSPARENT);
        int bgColorSelected = ta.getColor(
                R.styleable.DayView_bg_color_selected, Color.parseColor("#4CAF50"));

        GradientDrawable bgSelected = new GradientDrawable();
        bgSelected.setShape(GradientDrawable.OVAL);
        bgSelected.setColor(bgColorSelected);
        GradientDrawable bgNormal = new GradientDrawable();
        bgNormal.setShape(GradientDrawable.OVAL);
        bgNormal.setColor(bgColorNormal);
        StateListDrawable bgDb = new StateListDrawable();
        bgDb.addState(new int[]{android.R.attr.state_selected}, bgSelected);
        bgDb.addState(new int[]{}, bgNormal);
        mBgView.setBackground(bgDb);
        ta.recycle();
    }

    public void setDay(int day) {
        if (mDayTv != null) {
            mDayTv.setText(String.valueOf(day));
        }
    }

    public void setDesc(String desc) {
        if (mDescTv != null) {
            mDescTv.setText(desc);
        }
    }

    public void setHasData(boolean hasData) {
        if (mDotIv != null) {
            mDotIv.setVisibility(hasData ? VISIBLE : INVISIBLE);
        }
    }

    public int getDay() {
        return StringUtils.parseInt(mDayTv.getText().toString(), 0);
    }

}