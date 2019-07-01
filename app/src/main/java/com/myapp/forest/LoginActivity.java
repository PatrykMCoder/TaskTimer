package com.myapp.forest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.myapp.forest.firebase.database.DatabaseController;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button registerButton;
    private Button loginButton;
    private TextView resetPasswordTextView;

    private String email;
    private String password;

    private ProgressDialog progressDialog;

    private DatabaseController databaseController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        resetPasswordTextView = findViewById(R.id.resetPasswordTV);
        emailEditText = findViewById(R.id.emailET);
        passwordEditText = findViewById(R.id.passwordET);
        registerButton = findViewById(R.id.registerBtn);
        loginButton = findViewById(R.id.loginBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Please wait...");

        databaseController = new DatabaseController(this);

        resetPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailEditText.getText().toString();
                if (!email.equals("")) {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "E-mail send!", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(LoginActivity.this, "Error with send e-mail. Try again!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    emailEditText.setError("Enter e-mail!");
                    emailEditText.setBackgroundResource(R.drawable.edit_text_error_background);
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();
                progressDialog.show();
                if (!email.equals("") && !password.equals("")) {
                    databaseController.signIn(email, password, progressDialog);
                } else if (email.equals("")) {
                    emailEditText.setError("Enter e-mail!");
                    emailEditText.setBackgroundResource(R.drawable.edit_text_error_background);
                    progressDialog.cancel();
                } else if (password.equals("")) {
                    passwordEditText.setError("Enter password!");
                    passwordEditText.setBackgroundResource(R.drawable.edit_text_error_background);
                    progressDialog.cancel();
                }
            }
        });
    }
}
