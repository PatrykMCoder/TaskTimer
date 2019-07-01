package com.myapp.forest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.myapp.forest.adapters.DataForAdapter;
import com.myapp.forest.adapters.ListAdapter;
import com.myapp.forest.firebase.database.DatabaseController;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private boolean databaseError;
    private boolean userVer;

    private FloatingActionButton addNewTaskFBtn;
    private TextView scoreTextView;
    private TextView infoWhereTextView;
    private ImageView arrowImageView;
    private ListView listView;
    private ImageButton profileImageButton;

    private String title;
    private boolean finishTask = false;
    private boolean pasueApk = false;

    private String tmp;
    private String[] referenceName;

    private String scoreString;
    private int fullScore;

    private String[] dataArray;

    private final String TAG = "HomeActivity";

    private DatabaseController databaseController;
    private Intent intentWithData;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseController = new DatabaseController(this);

        lookLoginUser();

        finishTask = getIntent().getBooleanExtra("finish", false);
        pasueApk = getIntent().getBooleanExtra("app_pause", false);
        title = getIntent().getStringExtra("title_f");

        infoWhereTextView = findViewById(R.id.infoWhereTV);
        arrowImageView = findViewById(R.id.arrorwIV);
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

        if (finishTask) {
            databaseController.saveToDatabase(this, title);
            databaseController.readFromDatabase();
            showInfoAboutFinishedTask();
            updateUI();
        }
    }

    private void lookLoginUser() {
        if (firebaseUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            getEmailVer();
            tmp = firebaseUser.getEmail();
            assert tmp != null;
            referenceName = tmp.split("@");
            databaseController.readFromDatabase();
            updateUI();
        }
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
        if (!pasueApk) {
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

    private void updateUI() {
        scoreTextView = findViewById(R.id.scoreTV);
        listView = findViewById(R.id.lastTaskLV);

        if (databaseError)
            scoreTextView.setText(String.format("%s", "none"));

        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("data_from_db", MODE_PRIVATE);
        int score = sharedPreferences.getInt("full_score", 0);
        String titleToFormat = sharedPreferences.getString("title", "");
        String[] dataArray = titleToFormat.split(",");
        scoreTextView.setText(String.format("%s", score));
        listView.setAdapter(new ListAdapter(getApplicationContext(), dataArray));
    }
}