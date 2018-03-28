package com.vidovic.petar.diplomski.model;

import android.graphics.Shader;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.vidovic.petar.diplomski.R;

import java.util.Calendar;

/**
 * Created by Pera on 17-Mar-18.
 */

public class Event {

    private String id;
    public int year;
    public int month;
    public int day;
    public int startHour;
    public int startMinute;
    public int endHour;
    public int endMinute;
    public String location;
    public String eventName;

    public Event() {}

    public Event(int year, int month, int day, int startHour, int startMinute, int endHour, int endMinute, String location, String eventName) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.location = location;
        this.eventName = eventName;
    }

    public static Event getEventFromWeekViewEvent(WeekViewEvent weekViewEvent) {
        Event event = new Event();

        event.year = weekViewEvent.getStartTime().get(Calendar.YEAR);
        event.month = weekViewEvent.getStartTime().get(Calendar.MONTH);
        event.day = weekViewEvent.getStartTime().get(Calendar.DAY_OF_MONTH);

        event.startHour = weekViewEvent.getStartTime().get(Calendar.HOUR_OF_DAY);
        event.startMinute = weekViewEvent.getStartTime().get(Calendar.MINUTE);
        event.endHour = weekViewEvent.getEndTime().get(Calendar.HOUR_OF_DAY);
        event.endMinute = weekViewEvent.getEndTime().get(Calendar.MINUTE);

        event.location = weekViewEvent.getLocation();
        event.eventName = weekViewEvent.getName();

        return event;
    }

    public WeekViewEvent toWeekViewEvent() {
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.YEAR, year);
        startTime.set(Calendar.MONTH, month - 1);
        startTime.set(Calendar.DAY_OF_MONTH, day);
        startTime.set(Calendar.HOUR_OF_DAY, startHour);
        startTime.set(Calendar.MINUTE, startMinute);
        startTime.set(Calendar.SECOND, 0);

        Calendar endTime = Calendar.getInstance();
        endTime.set(Calendar.YEAR, year);
        endTime.set(Calendar.MONTH, month - 1);
        endTime.set(Calendar.DAY_OF_MONTH, day);
        endTime.set(Calendar.HOUR_OF_DAY, endHour);
        endTime.set(Calendar.MINUTE, endMinute);
        endTime.set(Calendar.SECOND, 0);

        return new WeekViewEvent("0", eventName, location, startTime, endTime);
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public String getLocation() {
        return location;
    }

    public String getEventName() {
        return eventName;
    }

    public boolean doEventsOverlap(Event secondEvent) {
        Event firstEvent = this;

        if (firstEvent.day != secondEvent.day || secondEvent.endHour < firstEvent.startHour || secondEvent.startHour > firstEvent.endHour) {
            return false;
        } else if (firstEvent.endHour == secondEvent.startHour) {
            if (firstEvent.endMinute <= secondEvent.startMinute) {
                return false;
            }
        } else if (firstEvent.startHour == secondEvent.endHour) {
            if (firstEvent.startMinute >= secondEvent.endMinute) {
                return false;
            }
        }

        return true;
    }

}
