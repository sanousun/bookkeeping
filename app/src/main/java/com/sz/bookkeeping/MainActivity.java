package com.sz.bookkeeping;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sz.bookkeeping.calendar.widget.CalendarViewPager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CalendarViewPager viewPager = (CalendarViewPager) findViewById(R.id.vp_calendar);
    }
}
