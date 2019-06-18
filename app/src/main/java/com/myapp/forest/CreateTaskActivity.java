package com.myapp.forest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateTaskActivity extends AppCompatActivity {

    private EditText titleTaskEditText;
    private EditText timeForTaskEditText;
    private Button createTaskButton;

    private String title;
    private String time;

    private boolean runningTask;

    private long h;
    private long m;
    private long s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        titleTaskEditText = findViewById(R.id.titleET);
        timeForTaskEditText = findViewById(R.id.timeET);
        createTaskButton = findViewById(R.id.startTaskBtn);


        createTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time = timeForTaskEditText.getText().toString();
                title = titleTaskEditText.getText().toString();

                if(title.equals("")){
                    Toast.makeText(CreateTaskActivity.this, "Please enter title for your task", Toast.LENGTH_LONG).show();
                }

                if(time.length() > 8){
                    Toast.makeText(CreateTaskActivity.this, "Format for time is: 'hh:mm:ss'! Please change it!" , Toast.LENGTH_LONG).show();
                }else if(time.length() < 5){
                    Toast.makeText(CreateTaskActivity.this, "Format for time is: 'hh:mm:ss'! Please change it!" , Toast.LENGTH_LONG).show();
                }else{
                    formatTime(time);
                    runningTask = true;
                    Intent timerIntent = new Intent(CreateTaskActivity.this, TimerActivity.class);
                    timerIntent.putExtra("hours", h);
                    timerIntent.putExtra("minutes", m);
                    timerIntent.putExtra("seconds", s);
                    timerIntent.putExtra("status", runningTask);
                    timerIntent.putExtra("title", title);
                    timerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(timerIntent);
                }
            }
        });
    }

    private void formatTime(String t){
        String[] tmp = t.split(":");
        h = Long.parseLong(tmp[0]) * 3600000;
        m = Long.parseLong(tmp[1]) * 60000;
        s = Long.parseLong(tmp[2]) * 1000;

        String TAG = "CreateTaskActivity";
        Log.d(TAG, "formatTime: h: " + h + " m: " + m + " s: " + s);
    }
}
