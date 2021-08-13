package com.example.moody;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.text.format.DateFormat;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MenuActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    String USER_ID;
    MessageListener   mMessageListener;
    Message  mMessage;
    JSONArray jsonArray;
    Integer frequency;
    JSONObject jsonObjectExperiment;
    int timefrequency;
    public static  Integer worker_range;
    private static String URL_EXPERIMENT = "https://collectivemoodtracker.herokuapp.com/api/experiment/";

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


        StringRequest stringExperiment = new StringRequest(Request.Method.GET, URL_EXPERIMENT +USER_ID ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            jsonArray = new JSONArray(response);

                            jsonObjectExperiment=jsonArray.getJSONObject(0);

                            frequency = Integer.parseInt(jsonObjectExperiment.getString("frequency"));
                            worker_range = Integer.parseInt(jsonObjectExperiment.getString("range"));

                             timefrequency=420/frequency;
                            final PeriodicWorkRequest periodicWorkRequest
                                    = new PeriodicWorkRequest.Builder(MyWorker.class, 1, TimeUnit.MINUTES)
                                     .build();
                            WorkManager.getInstance().enqueue(periodicWorkRequest);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        RequestQueue requestMeeting= Volley.newRequestQueue(getApplicationContext());
        requestMeeting.add(stringExperiment);

        final PeriodicWorkRequest periodicSyncRequest
                = new PeriodicWorkRequest.Builder(SyncWorker.class, 15, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance().enqueue(periodicSyncRequest);
        WorkManager.getInstance().enqueueUniquePeriodicWork(
                "sync_data",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicSyncRequest);

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