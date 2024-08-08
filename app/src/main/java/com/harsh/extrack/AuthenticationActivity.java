package com.harsh.extrack;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.harsh.extrack.fragments.LoginFragment;
import com.harsh.extrack.fragments.RegistrationFragment;

public class AuthenticationActivity extends AppCompatActivity {

    FrameLayout flAuthentication;
    static FragmentManager fm;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        flAuthentication = findViewById(R.id.fl_authentication);
        fm = getSupportFragmentManager();
        flAuthentication.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Animation animTranslate = new TranslateAnimation(-200, 0, 0, 0);
                animTranslate.setDuration(200);
                flAuthentication.startAnimation(animTranslate);
            }
        });
        setFragment(0);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.bg));
    }

    public static void setFragment(int fragIndex){
        FragmentTransaction ft = fm.beginTransaction();
        if(fragIndex==0){
            Fragment fragLogin = new LoginFragment();
            ft.replace(R.id.fl_authentication, fragLogin);
        }else if(fragIndex==1){
            Fragment fragRegistration = new RegistrationFragment();
            ft.replace(R.id.fl_authentication, fragRegistration);
        }
        ft.commit();
    }

}