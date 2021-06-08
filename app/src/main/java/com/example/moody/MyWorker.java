package com.example.moody;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyWorker extends Worker {

    public MyWorker(Context context, WorkerParameters workerParameters){
        super(context,workerParameters);
    }
    @Override
    public Result doWork() {
        displayNotification("How are you now?","Please complete survey!");

        return Result.success();
    }
    private void displayNotification(String task,String desc){
        NotificationManager manager=(NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel("simplifiedcoding","simplifiedcoding",NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(getApplicationContext(), MenuActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1,
                notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder =new NotificationCompat.Builder(getApplicationContext(),"simplifiedcoding")
                .setContentTitle(task)
                .setContentText(desc)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        manager.notify(1, builder.build());

    }
}
