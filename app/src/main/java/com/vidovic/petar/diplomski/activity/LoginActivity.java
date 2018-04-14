package com.vidovic.petar.diplomski.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vidovic.petar.diplomski.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Button loginButton = findViewById(R.id.loginButton);
        final EditText usernameEditText = findViewById(R.id.usernameEditText);
        final EditText passwordEditText = findViewById(R.id.passwordEditText);

        findViewById(R.id.login_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    loginButton.setEnabled(false);
                } else {
                    if (!passwordEditText.getText().toString().isEmpty()) {
                        loginButton.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    loginButton.setEnabled(false);
                } else {
                    if (!usernameEditText.getText().toString().isEmpty())
                    loginButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Boolean userFound = false;

                        for (DataSnapshot user : dataSnapshot.getChildren()) {
                            if (username.equals(user.getValue(String.class)) && password.equals(user.getValue(String.class))) {
                                userFound = true;
                                startActivity(new Intent(LoginActivity.this, ReservationsActivity.class));
                                finish();
                            }
                        }

                        if (!userFound) {
                            Toast.makeText(getBaseContext(), "Korisničko ime ili šifra nisu ispravni.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
        });
    }
}
