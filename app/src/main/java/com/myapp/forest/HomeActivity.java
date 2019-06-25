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
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.myapp.forest.adapters.ListAdapter;
import com.myapp.forest.firebase.database.Database;

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

    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = new Database(this);

        lookLoginUser();

        finishTask = getIntent().getBooleanExtra("finish", false);
        pasueApk = getIntent().getBooleanExtra("app_pause", false);
        title = getIntent().getStringExtra("title_f");

        infoWhereTextView = findViewById(R.id.infoWhereTV);
        arrowImageView = findViewById(R.id.arrorwIV);
        profileImageButton = findViewById(R.id.profileIB);

        listView = findViewById(R.id.lastTaskLV);
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
                database.signOut();
                lookLoginUser();
                return true;
            }
        });

        profileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.loadProfile(true);
                Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
            }
        });

        if (finishTask) {
            saveToDatabase();
            showInfoAboutFinishedTask();
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
            readFromDatabase();
        }
    }

    private void getEmailVer(){
        if(!database.getEmialVer()){
            Toast.makeText(this, "Please verified your email!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void saveToDatabase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("/task_" + referenceName[0] + "/_score/score");
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
                databaseReference = firebaseDatabase.getReference("/task_" + referenceName[0] + "/data" + "/" + title);
                databaseReference.child("finish").setValue(finishTask);

                int newScore = getIntent().getIntExtra("add_score", 0);
                fullScore += newScore;
                databaseReference = firebaseDatabase.getReference("/task_" + referenceName[0] + "/_score");
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
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference().child("/task_" + referenceName[0] + "/_score/score");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    scoreString = String.valueOf(dataSnapshot.getValue());
                    if (scoreString.equals("") || scoreString.equals("null")) {
                        scoreString = "0";
                        fullScore = Integer.parseInt(scoreString);
                    } else {
                        fullScore = Integer.parseInt(scoreString);
                    }
                    databaseReference = firebaseDatabase.getReference().child("/task_" + referenceName[0] + "/data/");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String data = String.valueOf(dataSnapshot.getValue());
                            String formatData = data.replace("={finish=true}", "");
                            String formatData2 = formatData.replace("{", "");
                            String formatData3 = formatData2.replace("}", "");
                            dataArray = formatData3.split(",");

                            if(dataArray.length > 0){
                                arrowImageView.setVisibility(View.INVISIBLE);
                                infoWhereTextView.setVisibility(View.INVISIBLE);
                            }else{
                                arrowImageView.setVisibility(View.VISIBLE);
                                infoWhereTextView.setVisibility(View.VISIBLE);
                            }

                            listView.setAdapter(new ListAdapter(HomeActivity.this, dataArray));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(TAG, "onCancelled: " + databaseError);
                        }
                    });

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