package com.example.moody;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    JSONObject jsonObject;
    Integer frequency;
    JSONObject jsonObjectExperimentCurrent;
    JSONObject jsonObjectExperimentFuture;
    JSONObject jsonObjectExperimentOld;
    int timefrequency=5;
    public static boolean show_old=false;
    public static  Integer worker_range=1;
    public static  Integer experiment_end_time=0;
    public static  Long future_experiment_start_time;
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
        bottom_nav.setVisibility(View.INVISIBLE);
//        OneTimeWorkRequest request=new OneTimeWorkRequest.Builder(MyWorker.class).build();
//        WorkManager.getInstance().enqueue(request);

        StringRequest stringExperiment = new StringRequest(Request.Method.GET, URL_EXPERIMENT +USER_ID ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            jsonObject = new JSONObject(response);

                            jsonObjectExperimentCurrent=jsonObject.getJSONObject("current");
                            jsonObjectExperimentFuture=jsonObject.getJSONObject("future");
                            jsonObjectExperimentOld=jsonObject.getJSONObject("old");

                             if(jsonObjectExperimentCurrent.length()!=0){
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
                                bottom_nav.setVisibility(View.VISIBLE);
                                frequency = Integer.parseInt(jsonObjectExperimentCurrent.getString("frequency"));

                                worker_range = Integer.parseInt(jsonObjectExperimentCurrent.getString("range"));
                                experiment_end_time = Integer.parseInt(jsonObjectExperimentCurrent.getString("end_timestamp"));
                                timefrequency=420/frequency;

                                final PeriodicWorkRequest periodicWorkRequest
                                        = new PeriodicWorkRequest.Builder(MyWorker.class, timefrequency, TimeUnit.MINUTES)
                                        .build();
                                WorkManager.getInstance().enqueue(periodicWorkRequest);

                            }
                            else if(jsonObjectExperimentFuture.length()!=0){
                                bottom_nav.setVisibility(View.INVISIBLE);
                                future_experiment_start_time = Long.parseLong(jsonObjectExperimentFuture.getString("start_timestamp"));
                                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                                cal.setTimeInMillis(future_experiment_start_time * 1000);
                                String experiment_start_time = DateFormat.format("dd-MM-yyyy", cal).toString();

                                NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    NotificationChannel channel = new NotificationChannel("simplifiedcoding", "simplifiedcoding", NotificationManager.IMPORTANCE_DEFAULT);
                                    manager.createNotificationChannel(channel);
                                }

                                new AlertDialog.Builder(MenuActivity.this)
                                        .setTitle("Please be patient")
                                        .setMessage("The experiment will be the start on "+experiment_start_time)
                                        .setCancelable(false)
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                sharedPreferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
                                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                                editor.putString("USER_ID","");
                                                if (editor.commit()) {
                                                    startActivity(new Intent(MenuActivity.this,LoginActivity.class));
                                                    WorkManager.getInstance().cancelAllWork();
                                                    finish();
                                                }
                                            }
                                        }).show();

                                System.out.println(experiment_start_time);
                            }

                            else if(jsonObjectExperimentOld.length()!=0){
                                 getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
                                 bottom_nav.setVisibility(View.VISIBLE);
                                 experiment_end_time = Integer.parseInt(jsonObjectExperimentOld.getString("end_timestamp"));

                                 if(!show_old){
                                     new AlertDialog.Builder(MenuActivity.this)
                                             .setTitle("Please be patient")
                                             .setMessage("The experiment is finished you can see other users data!")
                                             .setCancelable(false)
                                             .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                 @Override
                                                 public void onClick(DialogInterface dialog, int which) {
                                                     WorkManager.getInstance().cancelAllWork();
                                                 }
                                             }).show();
                                     show_old=true;
                                 }


                            }   else if(jsonObjectExperimentFuture.length()==0 && jsonObjectExperimentCurrent.length()==0 && jsonObjectExperimentOld.length()==0){

                                bottom_nav.setVisibility(View.INVISIBLE);

                                new AlertDialog.Builder(MenuActivity.this)
                                        .setTitle("Please try later again")
                                        .setMessage("Currently, there is no experiment available!")
                                        .setCancelable(false)
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                sharedPreferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
                                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                                editor.putString("USER_ID","");
                                                if (editor.commit()) {
                                                    startActivity(new Intent(MenuActivity.this,LoginActivity.class));
                                                    WorkManager.getInstance().cancelAllWork();
                                                    finish();
                                                }
                                            }
                                        }).show();

                            }

                        } catch (JSONException e) {
                            System.out.println("e message"+e);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                         if(error.toString()!=""){
                            finish();
                            startActivity(getIntent());
                        }
                    }
                });

        RequestQueue requestMeeting= Volley.newRequestQueue(getApplicationContext());
        requestMeeting.add(stringExperiment);

        final PeriodicWorkRequest periodicSyncRequest
                = new PeriodicWorkRequest.Builder(SyncWorker.class, 10, TimeUnit.HOURS)
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