package com.vidovic.petar.diplomski.utils;

import com.vidovic.petar.diplomski.model.Event;

import java.util.ArrayList;
import java.util.Calendar;

public class NetworkUtils {

    public static ArrayList<Event> parseResponse(String responseBody, int currentYear, int currentMonth, String currentLocation) {
        ArrayList<Event> events = new ArrayList<>();
        Event currentEvent = null;

        int numberOfCommasRead = 0;
        String currentValue = "";

        boolean openBracketFound = false;
        boolean closedBracketFound = false;

        for (char character :responseBody.toCharArray()) {
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
            } else if (character != ' ') {
                currentValue += character;
            }
        }

        return events;
    };

}
