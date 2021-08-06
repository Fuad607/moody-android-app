package com.example.moody;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class MenuActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    String USER_ID;
    MessageListener   mMessageListener;
    Message  mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        sharedPreferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        USER_ID = sharedPreferences.getString("USER_ID","");

        if(USER_ID.isEmpty()){
            startActivity(new Intent(MenuActivity.this,LoginActivity.class));;
        }


        BottomNavigationView bottom_nav =findViewById(R.id.bottom_nav);
        bottom_nav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

//        OneTimeWorkRequest request=new OneTimeWorkRequest.Builder(MyWorker.class).build();
//        WorkManager.getInstance().enqueue(request);

        final PeriodicWorkRequest periodicSyncRequest
                = new PeriodicWorkRequest.Builder(SyncWorker.class, 15, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance().enqueue(periodicSyncRequest);


        final PeriodicWorkRequest periodicWorkRequest
                = new PeriodicWorkRequest.Builder(MyWorker.class, 1, TimeUnit.MINUTES)
                .setConstraints(new Constraints.Builder().setRequiresCharging(true).build())
                .build();

        WorkManager.getInstance().enqueueUniquePeriodicWork(
                "sync_data",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest);


            mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                Log.d("mes", "Found message: " + new String(message.getContent()));
            }

            @Override
            public void onLost(Message message) {
                Log.d("mes", "Lost sight of message: " + new String(message.getContent()));
            }
        };

           mMessage = new Message("".getBytes());
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
                        startActivity(new Intent(MenuActivity.this,LoginActivity.class));
                        WorkManager.getInstance().cancelAllWork();
                        finish();
                    }

                    break;
            }

            return true;
        }
    };
    public void onStart() {
        super.onStart();

        Nearby.getMessagesClient(this).publish(mMessage);
        Nearby.getMessagesClient(this).subscribe(mMessageListener);
    }

    @Override
    public void onStop() {
        Nearby.getMessagesClient(this).unpublish(mMessage);
        Nearby.getMessagesClient(this).unsubscribe(mMessageListener);

        super.onStop();
    }


}