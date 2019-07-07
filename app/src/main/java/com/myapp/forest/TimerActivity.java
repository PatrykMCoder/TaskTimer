package com.myapp.forest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimerActivity extends AppCompatActivity implements View.OnClickListener {

    private long h;
    private long m;
    private long s;
    private long full;
    private boolean runningTask;
    private String title;
    private boolean stopTask;

    private CountDownTimer countDownTimer;

    private ProgressBar timeProgressBar;
    private TextView titleTextView;
    private TextView timeLeftTextView;
    private TextView fullTimeTextView;
    private ImageButton defaultImageButton;
    private ImageButton forestImageButton;
    private ImageButton summerNightImageButton;
    private ImageButton beachImageButton;
    private ImageView modeImageView;
    private FloatingActionButton stopTaskFloatingActionButton;

    private int[] images = {0, R.drawable.forest, R.drawable.summer_night, R.drawable.beach};
    private int[] sounds = {R.raw.test, R.raw.test2, R.raw.test, R.raw.test2};

    private MediaPlayer modeMediaPlayer;

    private boolean appIsPause = false;
    private boolean minusScore = false;

    private int score;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        titleTextView = findViewById(R.id.titleTV);
        timeLeftTextView = findViewById(R.id.timeLeftTV);
        fullTimeTextView = findViewById(R.id.infoTimeTV);
        timeProgressBar = findViewById(R.id.progressBarForTime);
        defaultImageButton = findViewById(R.id.defaultIB);
        forestImageButton = findViewById(R.id.forestIB);
        summerNightImageButton = findViewById(R.id.summerNightIB);
        beachImageButton = findViewById(R.id.beachIB);
        modeImageView = findViewById(R.id.modeIV);
        stopTaskFloatingActionButton = findViewById(R.id.stopTaskFB);

        defaultImageButton.setOnClickListener(this);
        forestImageButton.setOnClickListener(this);
        summerNightImageButton.setOnClickListener(this);
        beachImageButton.setOnClickListener(this);
        stopTaskFloatingActionButton.setOnClickListener(this);

        modeImageView.setImageLevel(0);
        timeProgressBar.setProgress(100);

        getDataFromIntent();
        runTask();
        playSound(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.defaultIB:
                Toast.makeText(this, "default mode", Toast.LENGTH_SHORT).show();
                modeImageView.setImageResource(images[0]);
                defaultImageButton.setBackgroundResource(R.drawable.background_image_button);
                forestImageButton.setBackgroundResource(R.drawable.background_image_button);
                summerNightImageButton.setBackgroundResource(R.drawable.background_image_button);
                beachImageButton.setBackgroundResource(R.drawable.background_image_button);
                stopSound();
                playSound(0);
                break;
            case R.id.forestIB:
                Toast.makeText(this, "fores mode", Toast.LENGTH_SHORT).show();
                modeImageView.setImageResource(images[1]);
                defaultImageButton.setBackgroundColor(Color.TRANSPARENT);
                forestImageButton.setBackgroundColor(Color.TRANSPARENT);
                summerNightImageButton.setBackgroundColor(Color.TRANSPARENT);
                beachImageButton.setBackgroundColor(Color.TRANSPARENT);
                stopSound();
                playSound(1);
                break;
            case R.id.summerNightIB:
                Toast.makeText(this, "summer night mode", Toast.LENGTH_SHORT).show();
                modeImageView.setImageResource(images[2]);
                defaultImageButton.setBackgroundColor(Color.TRANSPARENT);
                forestImageButton.setBackgroundColor(Color.TRANSPARENT);
                summerNightImageButton.setBackgroundColor(Color.TRANSPARENT);
                beachImageButton.setBackgroundColor(Color.TRANSPARENT);
                stopSound();
                playSound(2);
                break;
            case R.id.beachIB:
                Toast.makeText(this, "beach mode", Toast.LENGTH_SHORT).show();
                modeImageView.setImageResource(images[3]);
                defaultImageButton.setBackgroundColor(Color.TRANSPARENT);
                forestImageButton.setBackgroundColor(Color.TRANSPARENT);
                summerNightImageButton.setBackgroundColor(Color.TRANSPARENT);
                beachImageButton.setBackgroundColor(Color.TRANSPARENT);
                stopSound();
                playSound(3);
                break;
            case R.id.stopTaskFB:
                stopTask = true;
                Intent stopTaskIntent = new Intent(this, HomeActivity.class);
                score = 0;
                countDownTimer.cancel();
                minusScore = Boolean.parseBoolean(null);
                stopTaskIntent.putExtra("add_score", score);
                stopTaskIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(stopTaskIntent);
                break;
            default:
                break;
        }
    }

    private void getDataFromIntent() {
        h = getIntent().getLongExtra("hours", 0);
        m = getIntent().getLongExtra("minutes", 0);
        s = getIntent().getLongExtra("seconds", 0);
        title = getIntent().getStringExtra("title");
        runningTask = getIntent().getBooleanExtra("status", false);

        if (title == null || title.equals("")) {
            title = "Title task";
        }

        full = h + m + s;

        String TAG = "TimerActivity";
        Log.d(TAG, "getDataFromIntent: h: " + h + " m: " + m + " s: " + s + " status: " + runningTask);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return runningTask;
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void runTask() {
        if (runningTask) {
            countDownTimer = new CountDownTimer(full, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    full = millisUntilFinished;
                    updateUI();
                }

                @Override
                public void onFinish() {
                    runningTask = false;
                    timeProgressBar.setProgress(0);
                    Intent finishIntent = new Intent(TimerActivity.this, HomeActivity.class);
                    finishIntent.putExtra("title_f", title);
                    finishIntent.putExtra("finish", true);
                    finishIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    if(!minusScore){
                        score = 5;
                        finishIntent.putExtra("add_score", score);
                    }else{
                        score = 1;
                        finishIntent.putExtra("add_score", score);
                        finishIntent.putExtra("app_pause", appIsPause);
                    }
                    stopSound();
                    startActivity(finishIntent);
                }
            }.start();
        }
        updateUI();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateUI() {
        String time = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(full),
                TimeUnit.MILLISECONDS.toMinutes(full) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(full)),
                TimeUnit.MILLISECONDS.toSeconds(full) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(full)));  //this is for display info about time left
        String fullTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", h / 3600000, m / 60000, s / 1000); //this is for display info selected full time

        int value = (int) (full) / 1000;

        timeProgressBar.setProgress(value, true);
        titleTextView.setText(title);
        timeLeftTextView.setText(time);
        fullTimeTextView.setText(String.format("Your full time: \n%s", fullTime));
    }

    private void playSound(int source){
        modeMediaPlayer = MediaPlayer.create(this, sounds[source]);
        modeMediaPlayer.setLooping(true);
        modeMediaPlayer.start();
    }

    private void stopSound(){
        if(modeMediaPlayer!=null)
            modeMediaPlayer.stop();
    }

    @Override
    protected void onPause() {
        appIsPause = true;
        minusScore = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(appIsPause && runningTask){
            Toast.makeText(this, "You leave app! You lose  4 pkt for your score! :C", Toast.LENGTH_SHORT).show();
        }
    }
}