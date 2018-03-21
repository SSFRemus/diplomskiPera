package com.vidovic.petar.diplomski.manager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Pera on 18-Mar-18.
 */

public abstract class DatabaseManager {

    private static DatabaseReference room26DatabaseReference = FirebaseDatabase.getInstance().getReference("events").child("26");
    private static DatabaseReference room25DatabaseReference = FirebaseDatabase.getInstance().getReference("events").child("25");
    private static DatabaseReference room26BDatabaseReference = FirebaseDatabase.getInstance().getReference("events").child("26B");
    private static DatabaseReference room60DatabaseReference = FirebaseDatabase.getInstance().getReference("events").child("60");
    private static DatabaseReference room70DatabaseReference = FirebaseDatabase.getInstance().getReference("events").child("70");

    public static DatabaseReference databaseReference = room26DatabaseReference;

    public static void room26() {
        databaseReference = room26DatabaseReference;
    }

    public static void room25() {
        databaseReference = room25DatabaseReference;
    }

    public static void room26B() {
        databaseReference = room26BDatabaseReference;
    }

    public static void room60() {
        databaseReference = room60DatabaseReference;
    }

    public static void room70() {
        databaseReference = room70DatabaseReference;
    }
}
