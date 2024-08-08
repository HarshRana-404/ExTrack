package com.harsh.extrack;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.harsh.extrack.utils.Constants;

public class SplashActivity extends AppCompatActivity {

    TextView tvExpense, tvTrack;
    MediaPlayer mp;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.bg));
        mp = MediaPlayer.create(getApplicationContext(), R.raw.cash);
        mp.start();
        tvExpense = findViewById(R.id.tv_splash_expense);
        tvTrack = findViewById(R.id.tv_splash_track);
        Animation animRightToLeft = new TranslateAnimation(-200, 0, 0, 0);
        animRightToLeft.setDuration(300);
        Animation animLeftToRight = new TranslateAnimation(200, 0, 0, 0);
        animLeftToRight.setDuration(300);
        tvExpense.startAnimation(animLeftToRight);
        tvTrack.startAnimation(animRightToLeft);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Constants.init();
                if(Constants.fbAuth.getCurrentUser() == null){
                    startActivity(new Intent(SplashActivity.this, AuthenticationActivity.class));
                }else{
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
                finish();
            }
        }, 1400);
    }
}