package com.myapp.forest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private TextView usernameTextView;
    private TextView dataCreateAccountTextView;
    private Button removeAccountButton;

    private String email;
    private String[] username;
    private long createData;

    private AlertDialog alertDialogRemoveAccount;

    private final static String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        usernameTextView = findViewById(R.id.usernameTV);
        dataCreateAccountTextView = findViewById(R.id.dataCreateTV);
        removeAccountButton = findViewById(R.id.removeAccountBtn);

        usernameTextView.setText(String.format("Profile\n%s", readUsername()));

        createData = ((firebaseUser.getMetadata())).getCreationTimestamp();
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:SS");
        dataCreateAccountTextView.setText(String.format("Account create: %s", dateFormat.format(createData)));

        removeAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogRemoveAccount = alertDialogBuilder();
                alertDialogRemoveAccount.show();
            }
        });

    }

    private String readUsername() {
        email = firebaseUser.getEmail();
        assert email != null;
        username = email.split("@");
        return username[0];
    }

    private AlertDialog alertDialogBuilder(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Remove Account")
                .setMessage("Are your sure? Your data will remove!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialogRemoveAccount.cancel();
                    }
                });

        return builder.create();
    }
}
