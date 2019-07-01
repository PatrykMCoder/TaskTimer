package com.myapp.forest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myapp.forest.firebase.database.DatabaseController;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button createUserButton;

    private String email;
    private String password;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private ProgressDialog progressDialog;

    private DatabaseController databaseController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();

        databaseController = new DatabaseController(this);

        emailEditText = findViewById(R.id.emailRegisterET);
        passwordEditText = findViewById(R.id.passwordRegisterET);
        createUserButton = findViewById(R.id.createAccountBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Create account");
        progressDialog.setMessage("Please wait...");

        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();

                if(!email.equals("") && !password.equals("")) {
                    databaseController.createAccount(email, password, progressDialog);
                    progressDialog.cancel();
                }else if(email.equals("")){
                    emailEditText.setError("Enter e-mail!");
                    emailEditText.setBackgroundResource(R.drawable.edit_text_error_background);
                }else if(password.equals("")){
                    passwordEditText.setError("Enter password!");
                    passwordEditText.setBackgroundResource(R.drawable.edit_text_error_background);
                }
            }
        });
    }
}
