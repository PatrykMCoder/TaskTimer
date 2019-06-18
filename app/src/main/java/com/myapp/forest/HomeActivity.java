package com.myapp.forest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.components.Component;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Comment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private boolean databaseError;

    private FloatingActionButton addNewTaskFBtn;
    private TextView scoreTextView;

    private String title;
    private boolean finishTask = false;
    private boolean pasueApk = false;

    private String tmp;
    private String referenceName;

    private String scoreString;
    private int fullScore;

    private final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        lookLoginUser();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        assert firebaseUser != null;
        tmp = firebaseUser.getEmail();
        assert tmp != null;
        referenceName = tmp.replace("@gmail.com", "");

        finishTask = getIntent().getBooleanExtra("finish", false);
        pasueApk = getIntent().getBooleanExtra("app_pause", false);
        title = getIntent().getStringExtra("title_f");

        firebaseDatabase = FirebaseDatabase.getInstance();

        addNewTaskFBtn = findViewById(R.id.addNewTaskFloatingActionButton);
        addNewTaskFBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, CreateTaskActivity.class));
            }
        });

        readFromDatabase();

        if (finishTask) {
            saveToDatabase();
            showInfoAboutFinishedTask();
        }
    }

    private void lookLoginUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null)
            startActivity(new Intent(this, LoginActivity.class));
    }

    private void saveToDatabase() {
        databaseReference = firebaseDatabase.getReference("/task_" + referenceName + "/data/_score/score");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                scoreString = String.valueOf(dataSnapshot.getValue());
                if (scoreString.equals("") || scoreString.equals("null")) {
                    scoreString = "0";
                    fullScore = Integer.parseInt(scoreString);
                } else {
                    fullScore = Integer.parseInt(scoreString);
                }
                databaseReference = firebaseDatabase.getReference("/task_" + referenceName + "/data" + "/" + title);
                databaseReference.child("finish").setValue(finishTask);

                //save score to db;
                Log.d(TAG, "saveToDatabase: fullscore before read from data: " + fullScore);
                int newScore = getIntent().getIntExtra("add_score", 0);
                fullScore += newScore;
                databaseReference = firebaseDatabase.getReference("/task_" + referenceName + "/data/_score");
                Map<String, Object> newScoreMap = new HashMap<>();
                newScoreMap.put("score", fullScore);
                databaseReference.updateChildren(newScoreMap);
                readFromDatabase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: error: " + databaseError);
            }
        });
    }

    private void readFromDatabase() {
        try {
            databaseReference = firebaseDatabase.getReference().child("/task_" + referenceName + "/data/_score/score");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    scoreString = String.valueOf(dataSnapshot.getValue());
                    if (scoreString.equals("") || scoreString.equals("null")) {
                        scoreString = "0";
                        fullScore = Integer.parseInt(scoreString);
                    } else {
                        fullScore = Integer.parseInt(scoreString);
                    }
                    updateUI();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: Score error: " + databaseError);
                }
            });
        } catch (DatabaseException error) {
            databaseError = true;
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

        if (databaseError)
            scoreTextView.setText(String.format("%s", "none"));

        scoreTextView.setText(String.format("%s", fullScore));
    }
}