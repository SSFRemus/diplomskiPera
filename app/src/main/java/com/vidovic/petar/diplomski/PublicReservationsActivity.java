package com.vidovic.petar.diplomski;

import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PublicReservationsActivity extends AppCompatActivity implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {

    public WeekView weekView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_reservations);

        weekView = findViewById(R.id.weekView);
        weekView.goToHour(8);

        weekView.setOnEventClickListener(this);
        weekView.setMonthChangeListener(this);
        weekView.setEventLongPressListener(this);
        weekView.setEmptyViewLongPressListener(this);
        weekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" d/MM/yy", Locale.getDefault());

                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour < 10 ? "0" + hour + ":00" : hour + ":00";
            }
        });
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        List<WeekViewEvent> events = new ArrayList<>();

        greyOutNonWorkingHours(events, newYear, newMonth);

        return events;
    }

    private void greyOutNonWorkingHours(List<WeekViewEvent> events, int newYear, int newMonth) {
        int daysInCurrentMonth = Calendar.getInstance().getMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < daysInCurrentMonth; i++) {
            Calendar startTime = Calendar.getInstance();
            Calendar endTime;
            WeekViewEvent event;

            startTime.set(Calendar.DAY_OF_MONTH, i + 1);
            startTime.set(Calendar.HOUR_OF_DAY, 0);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.MONTH, newMonth - 1);
            startTime.set(Calendar.YEAR, newYear);

            endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.HOUR, 8);
            endTime.set(Calendar.MONTH, newMonth - 1);

            event = new WeekViewEvent(1, "", startTime, endTime);
            event.setColor(getResources().getColor(R.color.greyColor));
            events.add(event);

            startTime = Calendar.getInstance();
            startTime.set(Calendar.DAY_OF_MONTH, i + 1);
            startTime.set(Calendar.HOUR_OF_DAY, 22);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.MONTH, newMonth - 1);
            startTime.set(Calendar.YEAR, newYear);

            endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.HOUR, 2);
            endTime.set(Calendar.MONTH, newMonth - 1);

            event = new WeekViewEvent(2, "", startTime, endTime);
            event.setColor(getResources().getColor(R.color.greyColor));
            events.add(event);
        }
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
    }
}
