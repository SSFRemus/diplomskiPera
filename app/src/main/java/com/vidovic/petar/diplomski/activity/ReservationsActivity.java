package com.vidovic.petar.diplomski.activity;

import android.content.DialogInterface;
import android.graphics.RectF;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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

import java.net.Inet4Address;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
            public String interpretTime(int hour, int minutes) {
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

        return events;
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        ReservationDialogFragment dialogFragment = ReservationDialogFragment.newInstance(time);
        dialogFragment.callback = this;
        dialogFragment.show(getSupportFragmentManager(), "Nova rezervacija");
    }

    @Override
    public boolean onReserve(Calendar startTime, Calendar endTime, String name) {
        progressBar.setVisibility(View.VISIBLE);

        String year = Integer.toString(startTime.get(Calendar.YEAR));
        String month = Integer.toString(startTime.get(Calendar.MONTH) + 1);
        int day = startTime.get(Calendar.DAY_OF_MONTH);
        int startHour = startTime.get(Calendar.HOUR_OF_DAY);

        Event event = new Event(
                startTime.get(Calendar.YEAR),
                startTime.get(Calendar.MONTH) + 1,
                day,
                startHour,
                startTime.get(Calendar.MINUTE),
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String startHour = Integer.toString(event.getStartTime().get(Calendar.HOUR_OF_DAY));
        String startMinute = Integer.toString(event.getStartTime().get(Calendar.MINUTE));
        if (event.getStartTime().get(Calendar.MINUTE) < 10) {
            startMinute = "0" + startMinute;
        }

        String endHour;
        String endMinute;

        if (event.getEndTime().get(Calendar.MINUTE) == 59) {
            endHour = Integer.toString(event.getEndTime().get(Calendar.HOUR_OF_DAY) + 1);
            endMinute = "00";
        } else {
            endHour = Integer.toString(event.getEndTime().get(Calendar.HOUR_OF_DAY));
            endMinute = Integer.toString(event.getEndTime().get(Calendar.MINUTE) + 1);
            if (event.getEndTime().get(Calendar.MINUTE) < 9) {
                endMinute = "0" + endMinute;
            }
        }

        builder.setMessage(startHour + ":" + startMinute + " - " + endHour + ":" + endMinute).setTitle(event.getName());
        builder.create().show();
    }

    @Override
    public void onEventLongPress(final WeekViewEvent event, RectF eventRect) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(event.getName()).setTitle("Otkazivanje rezervacije");

        builder.setPositiveButton("Otkaži", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                final Integer year = event.getStartTime().get(Calendar.YEAR);
                final Integer month = event.getStartTime().get(Calendar.MONTH) + 1;
                final Integer day = event.getStartTime().get(Calendar.DAY_OF_MONTH);
                final Integer startHour = event.getStartTime().get(Calendar.HOUR_OF_DAY);
                final Integer startMinute = event.getStartTime().get(Calendar.MINUTE);
                final Integer endHour;
                final Integer endMinute;

                if (event.getEndTime().get(Calendar.MINUTE) == 59) {
                    endHour = event.getEndTime().get(Calendar.HOUR_OF_DAY) + 1;
                    endMinute = 0;
                } else {
                    endHour = event.getEndTime().get(Calendar.HOUR_OF_DAY);
                    endMinute = event.getEndTime().get(Calendar.MINUTE) + 1;
                }

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

        builder.setNegativeButton("Izađi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

}
