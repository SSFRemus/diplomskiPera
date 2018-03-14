package com.vidovic.petar.diplomski;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginButton = this.findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PublicReservationsActivity.class);
                startActivity(intent);
            }
        });

        Button testButton = this.findViewById(R.id.testButton);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("message");
                messageRef.child("Pera").setValue("Genije");
                messageRef.child("Djoka").setValue("Genije");
                messageRef.child("Nele").setValue("Genije");

                messageRef.child("Pera").setValue("Malo Veci Genije");
                messageRef.child("Pera").child("da li je mnogo veci genije").setValue("jeste");
                messageRef.child("Pera").child("ili je najveci genije?").setValue("mozda je staklo bilo zaledjeno");
                //myRef.setValue(new ArrayMap<String, String>().put("Pera", "Peric"));

                //myRef.setValue(new ArrayMap<String, String>().put("Pera", "Peric"));
                //myRef.child("PS");
            }
        });
    }
}
