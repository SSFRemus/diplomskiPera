package com.vidovic.petar.diplomski.activity;

import android.graphics.RectF;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vidovic.petar.diplomski.R;
import com.vidovic.petar.diplomski.fragment.ReservationDialogFragment;
import com.vidovic.petar.diplomski.fragment.ReservationDialogFragmentCallback;
import com.vidovic.petar.diplomski.manager.DatabaseManager;
import com.vidovic.petar.diplomski.model.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReservationsActivity extends AppCompatActivity implements ReservationDialogFragmentCallback, MonthLoader.MonthChangeListener, WeekView.EventClickListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {

    private WeekView weekView;
    private TabLayout tabLayout;
    private ProgressBar progressBar;

    private DataSnapshot eventsSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_reservations);

        FirebaseDatabase.getInstance().getReference("events").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.VISIBLE);
                eventsSnapshot = dataSnapshot;
                weekView.notifyDatasetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        weekView = findViewById(R.id.weekView);

        progressBar = findViewById(R.id.progressBar);

        tabLayout = findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch ((String) tab.getText()) {
                    case "26":
                        DatabaseManager.room26();
                        weekView.notifyDatasetChanged();
                        break;
                    case "25":
                        DatabaseManager.room25();
                        weekView.notifyDatasetChanged();
                        break;
                    case "26B":
                        DatabaseManager.room26B();
                        weekView.notifyDatasetChanged();
                        break;
                    case "60":
                        DatabaseManager.room60();
                        weekView.notifyDatasetChanged();
                        break;
                    case "70":
                        DatabaseManager.room70();
                        weekView.notifyDatasetChanged();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

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
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(final int newYear, final int newMonth) {
        final List<WeekViewEvent> events = new ArrayList<>();

        if (eventsSnapshot != null) {
            DataSnapshot currentMonthSnapshot = eventsSnapshot.child((String) tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText())
                    .child(Integer.toString(newYear)).child(Integer.toString(newMonth));

            for (DataSnapshot child: currentMonthSnapshot.getChildren()) {
                events.add(child.getValue(Event.class).toWeekViewEvent());
            }
        }

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
            beforeOpeningStartTime.set(Calendar.SECOND, 0);
            beforeOpeningStartTime.set(Calendar.MONTH, newMonth - 1);
            beforeOpeningStartTime.set(Calendar.YEAR, newYear);

            Calendar beforeOpeningEndTime = Calendar.getInstance();

            beforeOpeningEndTime.set(Calendar.DAY_OF_MONTH, i);
            beforeOpeningEndTime.set(Calendar.HOUR_OF_DAY, 7);
            beforeOpeningEndTime.set(Calendar.MINUTE, 59);
            beforeOpeningEndTime.set(Calendar.SECOND, 0);
            beforeOpeningEndTime.set(Calendar.MONTH, newMonth - 1);
            beforeOpeningEndTime.set(Calendar.YEAR, newYear);

            WeekViewEvent beforeOpeningEvent = new WeekViewEvent(1, "", beforeOpeningStartTime, beforeOpeningEndTime);
            beforeOpeningEvent.setColor(getResources().getColor(R.color.greyColor));
            events.add(beforeOpeningEvent);

            Calendar afterClosingStartTime = Calendar.getInstance();

            afterClosingStartTime.set(Calendar.DAY_OF_MONTH, i);
            afterClosingStartTime.set(Calendar.HOUR_OF_DAY, 22);
            afterClosingStartTime.set(Calendar.MINUTE, 00);
            afterClosingStartTime.set(Calendar.SECOND, 0);
            afterClosingStartTime.set(Calendar.MONTH, newMonth - 1);
            afterClosingStartTime.set(Calendar.YEAR, newYear);

            Calendar afterClosingEndTime = Calendar.getInstance();

            afterClosingEndTime.set(Calendar.DAY_OF_MONTH, i);
            afterClosingEndTime.set(Calendar.HOUR_OF_DAY, 23);
            afterClosingEndTime.set(Calendar.MINUTE, 59);
            afterClosingEndTime.set(Calendar.SECOND, 0);
            afterClosingEndTime.set(Calendar.MONTH, newMonth - 1);
            afterClosingEndTime.set(Calendar.YEAR, newYear);

            WeekViewEvent afterClosingEvent = new WeekViewEvent(2, "", afterClosingStartTime, afterClosingEndTime);
            afterClosingEvent.setColor(getResources().getColor(R.color.greyColor));
            events.add(afterClosingEvent);
        }
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        ReservationDialogFragment dialogFragment = ReservationDialogFragment.newInstance(time);
        dialogFragment.callback = this;
        dialogFragment.show(getSupportFragmentManager(), "Nova rezervacija");
    }

    @Override
    public boolean onReserve(Calendar startTIme, Calendar endTime, String name) {
        progressBar.setVisibility(View.VISIBLE);

        String year = Integer.toString(startTIme.get(Calendar.YEAR));
        String month = Integer.toString(startTIme.get(Calendar.MONTH) + 1);
        int day = startTIme.get(Calendar.DAY_OF_MONTH);
        int startHour = startTIme.get(Calendar.HOUR_OF_DAY);

        Event event = new Event(
                startTIme.get(Calendar.YEAR),
                startTIme.get(Calendar.MONTH) + 1,
                day,
                startHour,
                startTIme.get(Calendar.MINUTE),
                endTime.get(Calendar.HOUR_OF_DAY),
                endTime.get(Calendar.MINUTE),
                (String) tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText(),
                name
        );

        DataSnapshot currentMonthSnapshot = eventsSnapshot.child((String) tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText())
                .child(year)
                .child(month);

        for (DataSnapshot child: currentMonthSnapshot.getChildren()) {
            Event firstEvent = child.getValue(Event.class);

            if (firstEvent.doEventsOverlap(event)) {
                progressBar.setVisibility(View.GONE);

                return false;
            }
        }

        DatabaseManager.databaseReference.child(year).child(month).push().setValue(event);

        return true;
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
    }

}
