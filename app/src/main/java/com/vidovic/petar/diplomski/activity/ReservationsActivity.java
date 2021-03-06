package com.vidovic.petar.diplomski.activity;

import android.content.DialogInterface;
import android.graphics.RectF;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.concurrent.CancellationException;

public class ReservationsActivity extends AppCompatActivity implements ReservationDialogFragmentCallback, MonthLoader.MonthChangeListener, WeekView.EventClickListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {

    private WeekView weekView;
    private TabLayout tabLayout;
    private ProgressBar progressBar;
    private DataSnapshot eventsSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);

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
            public String interpretTime(int hour, int minutes) {
                return hour < 10 ? "0" + hour + ":00" : hour + ":00";
            }
        });

        weekView.goToHour(8);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.standard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(final int newYear, final int newMonth) {
        final List<WeekViewEvent> events = new ArrayList<>();

        int nextYear = (newMonth == 12) ? newYear + 1 : newYear;
        int previousYear = (newMonth == 1) ? newYear - 1 : newYear;
        int nextMonth = (newMonth == 12) ? 1 : newMonth + 1;
        int previousMonth = (newMonth == 1) ? 12 : newMonth - 1;

        String location = tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText().toString();

        if (eventsSnapshot != null) {
            DataSnapshot currentMonthSnapshot = eventsSnapshot.child(location)
                    .child(Integer.toString(newYear)).child(Integer.toString(newMonth));

            for (DataSnapshot child: currentMonthSnapshot.getChildren()) {
                events.add(child.getValue(Event.class).toWeekViewEvent());
            }

            DataSnapshot nextMonthSnapshot = eventsSnapshot.child(location)
                    .child(Integer.toString(nextYear)).child(Integer.toString(nextMonth));

            for (DataSnapshot child: nextMonthSnapshot.getChildren()) {
                events.add(child.getValue(Event.class).toWeekViewEvent());
            }

            DataSnapshot previousMonthSnapshot = eventsSnapshot.child(location)
                    .child(Integer.toString(previousYear)).child(Integer.toString(previousMonth));

            for (DataSnapshot child: previousMonthSnapshot.getChildren()) {
                events.add(child.getValue(Event.class).toWeekViewEvent());
            }
        }

        return events;
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        ReservationDialogFragment dialogFragment = ReservationDialogFragment.newInstance(time);
        dialogFragment.callback = this;
        dialogFragment.show(getSupportFragmentManager(), "Nova rezervacija");
    }

    @Override
    public boolean onReserve(Calendar startTime, Calendar endTime, String name, Integer multiply) {
        progressBar.setVisibility(View.VISIBLE);

        ArrayList<Event> events = new ArrayList<>();

        for (int i = 0; i < multiply; i++) {
            Calendar tempStartTime = (Calendar) startTime.clone();
            Calendar tempEndTime = (Calendar) endTime.clone();
            tempStartTime.add(Calendar.DAY_OF_MONTH, i * 7);
            tempEndTime.add(Calendar.DAY_OF_MONTH, i * 7);

            String year = Integer.toString(tempStartTime.get(Calendar.YEAR));
            String month = Integer.toString(tempStartTime.get(Calendar.MONTH) + 1);
            int day = tempStartTime.get(Calendar.DAY_OF_MONTH);
            int startHour = tempStartTime.get(Calendar.HOUR_OF_DAY);

            Event event = new Event(
                    tempStartTime.get(Calendar.YEAR),
                    tempStartTime.get(Calendar.MONTH) + 1,
                    day,
                    startHour,
                    tempStartTime.get(Calendar.MINUTE),
                    tempEndTime.get(Calendar.HOUR_OF_DAY),
                    tempEndTime.get(Calendar.MINUTE),
                    (String) tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText(),
                    name
            );

            DataSnapshot currentMonthSnapshot = eventsSnapshot.child((String) tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText())
                    .child(year)
                    .child(month);

            for (DataSnapshot child : currentMonthSnapshot.getChildren()) {
                Event firstEvent = child.getValue(Event.class);

                if (firstEvent.doEventsOverlap(event)) {
                    progressBar.setVisibility(View.GONE);

                    return false;
                }
            }

            events.add(event);
        }

        for (Event event: events) {
            DatabaseManager.databaseReference.child(Integer.toString(event.getYear())).child(Integer.toString(event.getMonth())).push().setValue(event);
        }

        progressBar.setVisibility(View.GONE);

        return true;
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String startHour = Integer.toString(event.getStartTime().get(Calendar.HOUR_OF_DAY));
        String startMinute = Integer.toString(event.getStartTime().get(Calendar.MINUTE));
        if (event.getStartTime().get(Calendar.MINUTE) < 10) {
            startMinute = "0" + startMinute;
        }

        String endHour = Integer.toString(event.getEndTime().get(Calendar.HOUR_OF_DAY));
        String endMinute = Integer.toString(event.getEndTime().get(Calendar.MINUTE));
        if (event.getEndTime().get(Calendar.MINUTE) < 10) {
            endMinute = "0" + endMinute;
        }

        builder.setMessage(startHour + ":" + startMinute + " - " + endHour + ":" + endMinute).setTitle(event.getName());
        builder.create().show();
    }

    @Override
    public void onEventLongPress(final WeekViewEvent event, RectF eventRect) {
        if (event.getStartTime().before(Calendar.getInstance())) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(event.getName()).setTitle("Otkazivanje rezervacije");

        builder.setPositiveButton("Otkaži", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                final Integer year = event.getStartTime().get(Calendar.YEAR);
                final Integer month = event.getStartTime().get(Calendar.MONTH) + 1;
                final Integer day = event.getStartTime().get(Calendar.DAY_OF_MONTH);
                final Integer startHour = event.getStartTime().get(Calendar.HOUR_OF_DAY);
                final Integer startMinute = event.getStartTime().get(Calendar.MINUTE);
                final Integer endHour = event.getEndTime().get(Calendar.HOUR_OF_DAY);
                final Integer endMinute = event.getEndTime().get(Calendar.MINUTE);

                DatabaseManager.databaseReference
                        .child(Integer.toString(year))
                        .child(Integer.toString(month)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data: dataSnapshot.getChildren()) {
                            Event fetchedEvent = data.getValue(Event.class);

                            if (fetchedEvent.day == day &&
                                    fetchedEvent.startHour == startHour && fetchedEvent.startMinute == startMinute &&
                                    fetchedEvent.endHour == endHour && fetchedEvent.endMinute == endMinute) {

                                DatabaseManager.databaseReference
                                        .child(Integer.toString(year))
                                        .child(Integer.toString(month))
                                        .child(data.getKey()).removeValue();
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
        });

        builder.setNegativeButton("Otkaži sva ponavljanja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < 15; i++) {
                    Calendar currentEventStartTime = (Calendar) event.getStartTime().clone();
                    currentEventStartTime.add(Calendar.DAY_OF_MONTH, i * 7);

                    final String name = event.getName();
                    final Integer year = currentEventStartTime.get(Calendar.YEAR);
                    final Integer month = currentEventStartTime.get(Calendar.MONTH) + 1;
                    final Integer day = currentEventStartTime.get(Calendar.DAY_OF_MONTH);
                    final Integer startHour = event.getStartTime().get(Calendar.HOUR_OF_DAY);
                    final Integer startMinute = event.getStartTime().get(Calendar.MINUTE);
                    final Integer endHour = event.getEndTime().get(Calendar.HOUR_OF_DAY);
                    final Integer endMinute = event.getEndTime().get(Calendar.MINUTE);

                    DatabaseManager.databaseReference
                            .child(Integer.toString(year))
                            .child(Integer.toString(month)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                Event fetchedEvent = data.getValue(Event.class);

                                if (fetchedEvent.eventName.equals(name) && fetchedEvent.day == day &&
                                        fetchedEvent.startHour == startHour && fetchedEvent.startMinute == startMinute &&
                                        fetchedEvent.endHour == endHour && fetchedEvent.endMinute == endMinute) {

                                    DatabaseManager.databaseReference
                                            .child(Integer.toString(year))
                                            .child(Integer.toString(month))
                                            .child(data.getKey()).removeValue();
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
        });

        builder.setNeutralButton("Izađi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

}