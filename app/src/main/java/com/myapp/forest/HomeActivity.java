package com.myapp.forest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myapp.forest.adapters.ListAdapter;
import com.myapp.forest.firebase.database.DatabaseController;

//todo > change song type

public class HomeActivity extends AppCompatActivity {

    private FloatingActionButton addNewTaskFBtn;
    private TextView scoreTextView;
    private ListView listView;
    private ImageButton profileImageButton;

    private String title;
    private String titleToFormat;
    private String[] dataArray;
    private int score;
    private boolean finishTask = false;
    private boolean pauseApp = false;
    private boolean stopTask = false;

    private DatabaseController databaseController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        databaseController = new DatabaseController(this);

        lookLoginUser();

        finishTask = getIntent().getBooleanExtra("finish", false);
        pauseApp = getIntent().getBooleanExtra("app_pause", false);
        title = getIntent().getStringExtra("title_f");
        stopTask  = getIntent().getBooleanExtra("stop_task", false);

        profileImageButton = findViewById(R.id.profileIB);

        addNewTaskFBtn = findViewById(R.id.addNewTaskFloatingActionButton);
        addNewTaskFBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, CreateTaskActivity.class));
            }
        });

        addNewTaskFBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                databaseController.signOut();
                lookLoginUser();
                return true;
            }
        });

        profileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseController.loadProfile(true);
                Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
            }
        });

        if (finishTask || stopTask) {
            databaseController.saveToDatabase(this, title);
            databaseController.readFromDatabase();
            showInfoAboutFinishedTask();
            updateUI();
        }

        updateUI();
    }

    private void lookLoginUser() {
        if (databaseController.lookingUserLogin() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            getEmailVer();
            databaseController.readFromDatabase();
        }
        updateUI();
    }

    private void getEmailVer(){
        if(!databaseController.getEmailVer()){
            Toast.makeText(this, "Please verified your email!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }


    private AlertDialog dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Super!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        if (!pauseApp) {
            builder.setMessage("You are finished your task! Congratulations! :) You got 5 score!");
        } else {
            builder.setMessage("You are finished your task! Congratulations! :) But, you leave application, so you got 1 score!");
        }
        return builder.create();
    }

    private void showInfoAboutFinishedTask() {
        AlertDialog dialog = dialog();
        dialog.show();
    }

    private void readDataFromPreference(){
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("data_from_db", MODE_PRIVATE);

        score = sharedPreferences.getInt("full_score", 0);

        titleToFormat = sharedPreferences.getString("title", "");
        dataArray = titleToFormat.split(",");
    }

    private void updateUI() {
        readDataFromPreference();
        scoreTextView = findViewById(R.id.scoreTV);
        listView = findViewById(R.id.lastTaskLV);

        scoreTextView.setText(String.format("%s", score));
        listView.setAdapter(new ListAdapter(getApplicationContext(), dataArray));
    }
}