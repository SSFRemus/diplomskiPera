package com.vidovic.petar.diplomski.utils;

import android.os.Message;

import com.vidovic.petar.diplomski.model.Event;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkUtils {

    public static class MessageEvent {

    }

    public static ArrayList<Event> parseResponse(String responseBody, int currentYear, int currentMonth, String currentLocation) {
        ArrayList<Event> events = new ArrayList<>();
        Event currentEvent = null;

        int numberOfCommasRead = 0;
        String currentValue = "";
        boolean insideEvent = false;

        if (!responseBody.contains("{")) {
            return events;
        }

        String body = responseBody.substring(responseBody.indexOf("{"));
        body = body.substring(0, body.lastIndexOf("}"));

        for (char character :body.toCharArray()) {
            if (character == '{') {
                currentEvent = new Event();
                currentEvent.year = currentYear;
                currentEvent.month = currentMonth;
                currentEvent.location = currentLocation;
            } else if (character == '}') {
                currentEvent.eventName = currentValue;
                events.add(currentEvent);
                currentValue = "";
                currentEvent = null;
            } else if (character == ',') {
                switch (++numberOfCommasRead) {
                    case 1:
                        currentEvent.day = Integer.parseInt(currentValue);
                        currentValue = "";
                        break;
                    case 2:
                        currentEvent.startHour = Integer.parseInt(currentValue);
                        currentValue = "";
                        break;
                    case 3:
                        currentEvent.startMinute = Integer.parseInt(currentValue);
                        currentValue = "";
                        break;
                    case 4:
                        int duration = Integer.parseInt(currentValue);

                        Calendar endTime = Calendar.getInstance();
                        endTime.set(Calendar.HOUR_OF_DAY, currentEvent.startHour);
                        endTime.set(Calendar.MINUTE, currentEvent.startMinute);
                        endTime.add(Calendar.MINUTE, duration);

                        currentEvent.endHour = endTime.get(Calendar.HOUR_OF_DAY);
                        currentEvent.endMinute = endTime.get(Calendar.MINUTE);

                        currentValue = "";

                        numberOfCommasRead = 0;

                        break;
                }
            } else if (numberOfCommasRead == 0) {
                currentValue += character;
            } else if (character != ' ') {
                currentValue += character;
            }
        }

        return events;
    };

    public static HashMap<Integer, ArrayList<Event>> getYearlyEvents(final int year, final String room, HashMap<Integer, ArrayList<Event>> map) {
        final AtomicInteger counter = new AtomicInteger(0);

        for (int i = 1; i < 13; i++ ) {
            ArrayList<Event> events = getEventsFor(i, year, room, map, counter);
            //map.put(i, events);
        }

        //EventBus.getDefault().post(new MessageEvent());
        return map;
    };

    public static ArrayList<Event> getEventsFor(final int month, final int year, final String room, final HashMap<Integer, ArrayList<Event>> map, final AtomicInteger counter) {
        OkHttpClient client = new OkHttpClient();
        final ArrayList<Event> events = new ArrayList<>();
        final Object lock = new Object();

        Request request = new Request.Builder()
                .url("https://rti.etf.bg.ac.rs/sale/apiView_bezBr.php?sala=" + room + "&mesec=" + month + "&godina=" + year)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    String body = response.body().string();

                    ArrayList<Event> currentEvents = NetworkUtils.parseResponse(body, year, month, room);

                    map.put(month, currentEvents);

                    if (counter.incrementAndGet() == 12) {
                        EventBus.getDefault().post(new MessageEvent()); // ne moram nista da prosledim, map ce se promeniti jer je referenca
                    }

//                    synchronized (lock) {
//                        events.addAll(currentEvents);
//                        lock.notify();
//                    }
                }
            }
        });

//        synchronized (lock) {
//            try {
//                lock.wait();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            return events;
//        }
        return events;
    };

}
