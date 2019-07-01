package com.myapp.forest.firebase.database;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.forest.HomeActivity;
import com.myapp.forest.adapters.DataForAdapter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DatabaseController {
    private Context context;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceScore;
    private DatabaseReference databaseReferenceTask;

    private boolean databaseError;

    private String[] referenceName;
    private String tmp;

    private String username;

    private String scoreString;
    private int fullScore;

    private String[] dataArray;

    private final static String TAG = "DatabaseController";


    public DatabaseController(Context context) {
        this.context = context;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();


        if (firebaseUser != null) {
            tmp = firebaseUser.getEmail();
            assert tmp != null;
            referenceName = tmp.split("@");

            databaseReferenceScore = firebaseDatabase.getReference("/task_" + referenceName[0] + "/_score/score");
            databaseReferenceTask = firebaseDatabase.getReference("/task_" + referenceName[0] + "/data" + "/" + "title"); //get title from activity
        } else
            Toast.makeText(context, "Please login again", Toast.LENGTH_SHORT).show();
    }


    public void createAccount(String email, String password, final ProgressDialog progressDialog) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.cancel();
                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                context.startActivity(new Intent(context, HomeActivity.class));
                            } else {
                                Toast.makeText(context, "Error with create account. Please later again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progressDialog.cancel();
                    Toast.makeText(context, "Error with create account. Please later again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean getEmailVer() {
        return firebaseUser.isEmailVerified();
    }

    public void resetPassword() {

    }

    public void signIn(String email, String password, final ProgressDialog progressDialog) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.cancel();
                    context.startActivity(new Intent(context, HomeActivity.class));
                } else {
                    progressDialog.cancel();
                    Toast.makeText(context, "Error with login. Please again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void readFromDatabase() {
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
                          /*  String formatData = data.replace("={finish=true}", "");
                            String formatData2 = formatData.replace("{", "");
                            String formatData3 = formatData2.replace("}", "");
                            dataArray = formatData3.split(",");
                           */
                            String formatData = data.replace("={finish=Finish}", "");
                            String formatData2 = formatData.replace("{", "");
                            String formatData3 = formatData2.replace("}", "");

                            SharedPreferences sharedPreferences = context.getSharedPreferences("data_from_db", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("full_score", fullScore);
                            editor.putString("title", formatData3);
                            editor.apply();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(TAG, "onCancelled: " + databaseError);
                        }
                    });
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

    public void saveToDatabase(final HomeActivity homeActivity, final String title) {
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
                databaseReference = firebaseDatabase.getReference("/task_" + referenceName[0] + "/data/" + title);
                Date date = new Date();
                long currentTime = date.getTime();
                databaseReference.child("time finished").setValue(currentTime);
                int newScore = homeActivity.getIntent().getIntExtra("add_score", 0);
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

    public void signOut() {
        firebaseAuth.signOut();
    }

    public void loadProfile(boolean localProfile) {

    }
}