package com.vidovic.petar.diplomski.manager;

import com.alamkanak.weekview.WeekViewEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pera on 18-Mar-18.
 */

public abstract class DatabaseManager {

    public static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("events");

    public static List<WeekViewEvent> fetchedEvents = new ArrayList<>();

}
