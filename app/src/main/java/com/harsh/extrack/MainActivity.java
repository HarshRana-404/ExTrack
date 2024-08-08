package com.harsh.extrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.harsh.extrack.fragments.ContactsFragment;
import com.harsh.extrack.fragments.HomeFragment;
import com.harsh.extrack.fragments.ProfileFragment;
import com.harsh.extrack.utils.Constants;

public class MainActivity extends AppCompatActivity {

    FrameLayout flMain;
    BottomNavigationView bnvMain;
    static MenuItem lastNavigatedMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Constants.fbAuth.getCurrentUser()==null){
            startActivity(new Intent(MainActivity.this, AuthenticationActivity.class));
            finish();
        }

        flMain = findViewById(R.id.fl_main);
        bnvMain = findViewById(R.id.bn_main);
        bnvMain.setSelectedItemId(R.id.mi_home);
        setFragment(new HomeFragment());

        bnvMain.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.mi_home && lastNavigatedMenuItem!=item){
                    setFragment(new HomeFragment());
                    lastNavigatedMenuItem = item;
                }else if(item.getItemId() == R.id.mi_contacts && lastNavigatedMenuItem!=item){
                    setFragment(new ContactsFragment());
                    lastNavigatedMenuItem = item;
                }else if(item.getItemId() == R.id.mi_profile && lastNavigatedMenuItem!=item){
                    setFragment(new ProfileFragment());
                    lastNavigatedMenuItem = item;
                }
                return true;
            }
        });
    }
    void setFragment(Fragment frag){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_main, frag);
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(lastNavigatedMenuItem==null){
        }else if(lastNavigatedMenuItem.getItemId() == R.id.mi_home){
            setFragment(new HomeFragment());
        }else if(lastNavigatedMenuItem.getItemId() == R.id.mi_contacts){
            setFragment(new ContactsFragment());
        }else if(lastNavigatedMenuItem.getItemId() == R.id.mi_profile){
            setFragment(new ProfileFragment());
        }
        try{
            bnvMain.setSelectedItemId(lastNavigatedMenuItem.getItemId());
        } catch (Exception e) {
        }
    }
}