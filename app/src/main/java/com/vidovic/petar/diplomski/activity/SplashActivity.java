package com.vidovic.petar.diplomski.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.vidovic.petar.diplomski.R;
import com.vidovic.petar.diplomski.manager.DatabaseManager;
import com.vidovic.petar.diplomski.model.Event;

import java.util.Calendar;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final int year = Calendar.getInstance().get(Calendar.YEAR);
        final int month = Calendar.getInstance().get(Calendar.MONTH) + 1;

        DatabaseManager.databaseReference.child(Integer.toString(year)).child(Integer.toString(month)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    DatabaseManager.fetchedEvents.add(child.getValue(Event.class).toWeekViewEvent());
                }

                DatabaseManager.databaseReference.child(Integer.toString(year)).child(Integer.toString(month - 1)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            DatabaseManager.fetchedEvents.add(child.getValue(Event.class).toWeekViewEvent());
                        }

                        DatabaseManager.databaseReference.child(Integer.toString(year)).child(Integer.toString(month + 1)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    DatabaseManager.fetchedEvents.add(child.getValue(Event.class).toWeekViewEvent());
                                }

                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }
}
