package com.vidovic.petar.diplomski.model;

import com.alamkanak.weekview.WeekViewEvent;
import java.util.Calendar;

public class Event {

    // prema definiciji POJO objekta sva polja moraju biti javno vidljiva

    public String id;           // ID termina
    public int year;            // godina pocetka termina
    public int month;           // mesec pocetka termina
    public int day;             // dan pocetka termina
    public int startHour;       // sat pocetka termina
    public int startMinute;     // minute pocetka termina
    public int endHour;         // sat kraja termina
    public int endMinute;       // minute kraja termina
    public String location;     // sala u kojoj je rezervisan termin
    public String eventName;    // naziv termina

    public Event() {}           // prazan konstruktor, potreban prema definiciji POJO objekta

    // konstruktor koji postavlja sva polja klase
    public Event(String id, int year, int month, int day,
                 int startHour, int startMinute, int endHour,
                 int endMinute, String location, String eventName) {
        this.id = id;
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

    // metoda koja pravi objekat klase WeekViewEvent na osnovu objekta klase Event
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

        return new WeekViewEvent(id, eventName, startTime, endTime);
    }

    //metode koje dohvataju svako od polja klase (tzv. Getter metode), potrebne prema definiciji POJO objekta

    public String getId() {
        return id;
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

    // metoda koja ispituje da li se dva termina preklapaju u vremenu
    public boolean doEventsOverlap(Event secondEvent) {
        Event firstEvent = this;

        if (firstEvent.day != secondEvent.day
                || secondEvent.endHour < firstEvent.startHour
                || secondEvent.startHour > firstEvent.endHour) {
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