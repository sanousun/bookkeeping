package com.sz.bookkeeping;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sz.bookkeeping.calendar.manager.CalMonth;
import com.sz.bookkeeping.calendar.widget.CalendarView;

public class MainActivity extends AppCompatActivity {

    private boolean isOne = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CalendarView calendarView = (CalendarView) findViewById(R.id.view_calendar);
        CalMonth calMonth = new CalMonth(2017, 2);
        isOne = false;
        calendarView.setCalMonth(calMonth);
        findViewById(R.id.btn_change).setOnClickListener(view -> {
            CalMonth month;
            if (isOne) {
                month = new CalMonth(2017, 1);
            } else {
                month = new CalMonth(2017, 2);
            }
            isOne = !isOne;
            calendarView.setCalMonth(month);
        });
//        calendarView.setOnDayOfMonthSelectListener(calDay -> {
//            Log.e("xyz", calDay.getSolar().toString());
//        });
    }
}
