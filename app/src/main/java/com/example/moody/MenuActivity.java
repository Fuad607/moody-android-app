package com.example.moody;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import org.jetbrains.annotations.NotNull;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

public class MenuActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    String USER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        sharedPreferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        USER_ID = sharedPreferences.getString("USER_ID","");

//        if(USER_ID.isEmpty()){
//            startActivity(new Intent(MenuActivity.this,LoginActivity.class));;
//        }
        sharedPreferences= getSharedPreferences("USER_DATA", MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("USER_ID","6");
        editor.apply();


        ///////////////////


        BottomNavigationView bottom_nav =findViewById(R.id.bottom_nav);
        bottom_nav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

//        OneTimeWorkRequest request=new OneTimeWorkRequest.Builder(MyWorker.class).build();
//        WorkManager.getInstance().enqueue(request);

        final PeriodicWorkRequest periodicWorkRequest
                = new PeriodicWorkRequest.Builder(MyWorker.class, 15, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance().enqueue(periodicWorkRequest);

/*
        final PeriodicWorkRequest periodicSyncRequest
                = new PeriodicWorkRequest.Builder(SyncWorker.class, 15, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance().enqueue(periodicSyncRequest);
*/

        Constraints constraint = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest PeriodicWorkRequest = new PeriodicWorkRequest.Builder(SyncWorker.class, 1, TimeUnit.MINUTES)
                .setConstraints(constraint)
                .build();

        WorkManager workManager = WorkManager.getInstance();
        workManager.enqueueUniquePeriodicWork("sync_data", ExistingPeriodicWorkPolicy.KEEP, PeriodicWorkRequest);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem menuItem) {
            Fragment selectedFragment =null;

            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    selectedFragment= new HomeFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                    break;
                case R.id.nav_history:
                    selectedFragment= new HistoryFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                    break;
                case R.id.nav_log:
                    selectedFragment= new LogFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                    break;
                case R.id.nav_log_out:
                    sharedPreferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("USER_ID","");
                    if (editor.commit()) {
                        startActivity(new Intent(MenuActivity.this,LoginActivity.class));;
                        finish();
                    }


                    break;
            }



            return true;
        }
    };

}