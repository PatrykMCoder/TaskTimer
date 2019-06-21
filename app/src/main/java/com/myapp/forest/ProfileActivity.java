package com.myapp.forest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private TextView usernameTextView;

    private String email;
    private String[] username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        usernameTextView = findViewById(R.id.usernameTV);
        usernameTextView.setText(readUsername());
    }

    private String readUsername() {
        email = firebaseUser.getEmail();
        username = email.split("@");
        return username[0];
    }
}
