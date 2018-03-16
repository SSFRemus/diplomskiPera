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

public class PublicReservationsActivity extends AppCompatActivity implements MonthLoader.MonthChangeListener, WeekView.EventClickListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {

    public WeekView weekView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_reservations);

        weekView = findViewById(R.id.weekView);

        weekView.setMonthChangeListener(this);
        weekView.setOnEventClickListener(this);
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

        weekView.goToHour(8);
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        List<WeekViewEvent> events = new ArrayList<>();

        greyOutNonWorkingHours(events, newYear, newMonth);

        return events;
    }

    private void greyOutNonWorkingHours(List<WeekViewEvent> events, int newYear, int newMonth) {
        Calendar newMonthsCalendar = Calendar.getInstance();
        newMonthsCalendar.set(Calendar.MONTH, newMonth - 1);

        int daysInCurrentMonth = newMonthsCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= daysInCurrentMonth; i++) {
            Calendar beforeOpeningStartTime = Calendar.getInstance();

            beforeOpeningStartTime.set(Calendar.DAY_OF_MONTH, i);
            beforeOpeningStartTime.set(Calendar.HOUR_OF_DAY, 0);
            beforeOpeningStartTime.set(Calendar.MINUTE, 0);
            beforeOpeningStartTime.set(Calendar.MONTH, newMonth - 1);
            beforeOpeningStartTime.set(Calendar.YEAR, newYear);

            Calendar beforeOpeningEndTime = Calendar.getInstance();

            beforeOpeningEndTime.set(Calendar.DAY_OF_MONTH, i);
            beforeOpeningEndTime.set(Calendar.HOUR_OF_DAY, 8);
            beforeOpeningEndTime.set(Calendar.MINUTE, 0);
            beforeOpeningEndTime.set(Calendar.MONTH, newMonth - 1);
            beforeOpeningEndTime.set(Calendar.YEAR, newYear);

            WeekViewEvent beforeOpeningEvent = new WeekViewEvent(1, "", beforeOpeningStartTime, beforeOpeningEndTime);
            beforeOpeningEvent.setColor(getResources().getColor(R.color.greyColor));
            events.add(beforeOpeningEvent);

            Calendar afterClosingStartTime = Calendar.getInstance();

            afterClosingStartTime.set(Calendar.DAY_OF_MONTH, i);
            afterClosingStartTime.set(Calendar.HOUR_OF_DAY, 22);
            afterClosingStartTime.set(Calendar.MINUTE, 0);
            afterClosingStartTime.set(Calendar.MONTH, newMonth - 1);
            afterClosingStartTime.set(Calendar.YEAR, newYear);

            Calendar afterClosingEndTime = Calendar.getInstance();

            afterClosingEndTime.set(Calendar.DAY_OF_MONTH, i);
            afterClosingEndTime.set(Calendar.HOUR_OF_DAY, 23);
            afterClosingEndTime.set(Calendar.MINUTE, 59);
            afterClosingEndTime.set(Calendar.MONTH, newMonth - 1);
            afterClosingEndTime.set(Calendar.YEAR, newYear);

            WeekViewEvent afterClosingEvent = new WeekViewEvent(2, "", afterClosingStartTime, afterClosingEndTime);
            afterClosingEvent.setColor(getResources().getColor(R.color.greyColor));
            events.add(afterClosingEvent);
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
