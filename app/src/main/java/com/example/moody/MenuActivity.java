package com.example.moody;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import org.jetbrains.annotations.NotNull;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

public class MenuActivity extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    String USER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        SharedPreferences sharedPreferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        USER_ID = sharedPreferences.getString("USER_ID","");

        BottomNavigationView bottom_nav =findViewById(R.id.bottom_nav);
        bottom_nav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

        OneTimeWorkRequest request=new OneTimeWorkRequest.Builder(MyWorker.class).build();
        WorkManager.getInstance().enqueue(request);

        final PeriodicWorkRequest periodicWorkRequest
                = new PeriodicWorkRequest.Builder(MyWorker.class, 15, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance().enqueue(periodicWorkRequest);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem menuItem) {
            Fragment selectedFragment =null;

            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    selectedFragment= new HomeFragment();
                    break;
                case R.id.nav_history:
                    selectedFragment= new HistoryFragment();
                    break;
                case R.id.nav_log:
                    selectedFragment= new LogFragment();
                    break;
                case R.id.nav_log_out:
                    startActivity(new Intent(MenuActivity.this,LoginActivity.class));;
                    finish();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();

            return true;
        }
    };
}