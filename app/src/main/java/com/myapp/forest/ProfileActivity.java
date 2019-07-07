package com.myapp.forest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myapp.forest.firebase.database.DatabaseController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private TextView usernameTextView;
    private TextView scoreTextView;
    private TextView dataCreateAccountTextView;
    private Button removeAccountButton;
    private Button updateDataButton;
    private ImageButton homeImageButton;
    private ImageButton settingsImageButton;

    private DatabaseController databaseController;

    private AlertDialog alertDialogRemoveAccount;

    private final static String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        databaseController = new DatabaseController(this);

        profileImageView = findViewById(R.id.profileIB);
        usernameTextView = findViewById(R.id.usernameTV);
        scoreTextView = findViewById(R.id.scoreTV);
        dataCreateAccountTextView = findViewById(R.id.accoundCreateInTV);

        updateDataButton = findViewById(R.id.updateDataOfUserBtn);
        removeAccountButton = findViewById(R.id.removeAccountBtn);

        homeImageButton = findViewById(R.id.homeIB);
        settingsImageButton = findViewById(R.id.settingsIB);

        updateDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "In future version app! :)", Toast.LENGTH_SHORT).show();
            }
        });
        removeAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogRemoveAccount = alertDialogBuilder();
                alertDialogRemoveAccount.show();
            }
        });

        homeImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        settingsImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(settingIntent);
            }
        });

        updateUI();
    }

    private void updateUI(){
        usernameTextView.setText(loadData().get(0));
        scoreTextView.setText(String.format("Score: %s", loadData().get(1)));
        dataCreateAccountTextView.setText(loadData().get(2));
    }

    private AlertDialog alertDialogBuilder(){
        final EditText passwordEditText = new EditText(ProfileActivity.this);
        passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordEditText.setHint("Enter your password!");
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        passwordEditText.setLayoutParams(layoutParams);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Remove Account")
                .setMessage("Are your sure? Your data will remove!")
                .setView(passwordEditText)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!passwordEditText.getText().toString().isEmpty()) {
                            databaseController.removeAccount(passwordEditText.getText().toString());
                            Toast.makeText(ProfileActivity.this, "Account removed", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }else
                            Toast.makeText(ProfileActivity.this, "Enter password!", Toast.LENGTH_SHORT).show();
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

    private ArrayList<String> loadData(){
        return databaseController.loadProfile(true);
    }
}
