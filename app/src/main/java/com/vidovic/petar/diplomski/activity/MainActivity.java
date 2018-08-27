package com.vidovic.petar.diplomski.activity;

import android.content.Intent;
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
import com.vidovic.petar.diplomski.R;
import com.vidovic.petar.diplomski.model.Event;
import com.vidovic.petar.diplomski.utils.NetworkUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements MonthLoader.MonthChangeListener, WeekView.EventClickListener {

    private WeekView weekView;
    private TabLayout tabLayout;
    private ProgressBar progressBar;
    private HashMap<Integer, ArrayList<Event>> eventMap;

    private HashMap<Integer, ArrayList<Event>> eventMap26;
    private HashMap<Integer, ArrayList<Event>> eventMap25;
    private HashMap<Integer, ArrayList<Event>> eventMap26B;
    private HashMap<Integer, ArrayList<Event>> eventMap60;
    private HashMap<Integer, ArrayList<Event>> eventMap70;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(getResources().getString(R.string.overview));

        weekView = findViewById(R.id.weekView);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        tabLayout = findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch ((String) tab.getText()) {
                    case "26":
                        eventMap = eventMap26;
                        weekView.notifyDatasetChanged();
                        break;
                    case "25":
                        eventMap = eventMap25;
                        weekView.notifyDatasetChanged();
                        break;
                    case "26B":
                        eventMap = eventMap26B;
                        weekView.notifyDatasetChanged();
                        break;
                    case "60":
                        eventMap = eventMap60;
                        weekView.notifyDatasetChanged();
                        break;
                    case "70":
                        eventMap = eventMap70;
                        weekView.notifyDatasetChanged();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        weekView.setMonthChangeListener(this);
        weekView.setOnEventClickListener(this);
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
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMessageEvent(NetworkUtils.MessageEvent event) {
        //progressBar.setVisibility(View.GONE);
        weekView.notifyDatasetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        final int year = Calendar.getInstance().get(Calendar.YEAR);

        progressBar.setVisibility(View.VISIBLE);
        eventMap = new HashMap<>();
        eventMap26 = NetworkUtils.getYearlyEvents(year, "26", eventMap);
//        eventMap25 = NetworkUtils.getYearlyEvents(year, "25", eventMap25);
//        eventMap26B = NetworkUtils.getYearlyEvents(year, "26B", eventMap26B);
//        eventMap60 = NetworkUtils.getYearlyEvents(year, "60", eventMap60);
//        eventMap70 = NetworkUtils.getYearlyEvents(year, "70", eventMap70);
        //progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.standard_menu, menu);
        menu.getItem(0).setTitle("Prijava");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(final int newYear, final int newMonth) {
        final List<WeekViewEvent> events = new ArrayList<>();

        if (eventMap == null || eventMap.get(newMonth) == null) {
            return events;
        }

        int nextYear = (newMonth == 12) ? newYear + 1 : newYear;
        int previousYear = (newMonth == 1) ? newYear - 1 : newYear;
        int nextMonth = (newMonth == 12) ? 1 : newMonth + 1;
        int previousMonth = (newMonth == 1) ? 12 : newMonth - 1;

        String location = tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText().toString();

        for (Event event: eventMap.get(newMonth)) {
            events.add(event.toWeekViewEvent());
        }

        for (Event event: eventMap.get(previousMonth)) {
            events.add(event.toWeekViewEvent());
        }

        for (Event event: eventMap.get(nextMonth)) {
            events.add(event.toWeekViewEvent());
        }

        // OVO NE ZABORAVI DA VRATIS DA BUDE SVE U JEDNOM VISIBILITIJU
        findViewById(R.id.rl_progress_bar).setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        return events;
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

        builder.setMessage(event.getName() + "\n" + startHour + ":" + startMinute + " - " + endHour + ":" + endMinute);
        builder.create().show();
    }

}
