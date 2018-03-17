package com.vidovic.petar.diplomski.model;

import com.alamkanak.weekview.WeekViewEvent;
import com.vidovic.petar.diplomski.R;

import java.util.Calendar;

/**
 * Created by Pera on 17-Mar-18.
 */

public class Event {

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
        WeekViewEvent weekViewEvent = new WeekViewEvent(0, eventName, year, month, day, startHour, startMinute, year, month, day, endHour, endMinute);
        weekViewEvent.setLocation(location);
        weekViewEvent.setColor(R.color.colorPrimary);

        return weekViewEvent;
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

}
