package com.myapp.forest.firebase.database;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myapp.forest.HomeActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Database{
    private Context context;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceScore;
    private DatabaseReference databaseReferenceTask;

    private String[] referenceName;
    private String tmp;

    private String username;

    public Database(Context context){
        this.context = context;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();


        if(firebaseUser!=null) {
            tmp = firebaseUser.getEmail();
            assert tmp != null;
            referenceName = tmp.split("@");

            databaseReferenceScore = firebaseDatabase.getReference("/task_" + referenceName[0] + "/_score/score");
            databaseReferenceTask = firebaseDatabase.getReference("/task_" + referenceName[0] + "/data" + "/" + "title"); //get title from activity
        }else
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

    public boolean getEmialVer(){
        return firebaseUser.isEmailVerified();
    }

    public void resetPassword(){

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

    public void readFromDatabase(){

    }

    public void saveToDatabase(){

    }

    public void signOut(){
        firebaseAuth.signOut();
    }

    public void loadProfile(boolean localProfile){

  }
}