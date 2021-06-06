package com.example.moody;

import android.app.NotificationChannel;
import android.app.NotificationManager;
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
//Userspecialsituation usermeeting survey
    private static String URL_POST_SURVEY = "http://192.168.0.16/api/survey";
    private static String URL_POST_USERMEETING = "http://192.168.0.16/api/usermeeting";
    private static String URL_POST_Userspecialsituation = "http://192.168.0.16/api/userspecialsituation";
    String USER_ID;
    DBHelper DB;
    JSONArray jsonArray;
    JSONObject jsonObjectUserData;
    SharedPreferences sharedPreferences;

    public MyWorker(Context context, WorkerParameters workerParameters){
        super(context,workerParameters);
    }
    @Override
    public Result doWork() {
        displayNotification("Hello world","work is finished");
        return Result.success();
    }
    private void displayNotification(String task,String desc){
        NotificationManager manager=(NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel("simplifiedcoding","simplifiedcoding",NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        Intent rIntent= new Intent(getApplicationContext(),MainActivity.class);

        NotificationCompat.Builder builder =new NotificationCompat.Builder(getApplicationContext(),"simplifiedcoding")
                .setContentTitle(task)
                .setContentText(desc)
                .setSmallIcon(R.mipmap.ic_launcher);

        manager.notify(1, builder.build());
    }

    private void sync_data(){
        DB = new DBHelper(getApplicationContext());

        Cursor cursor_user_relationship = DB.getSurvey();
        cursor_user_relationship.moveToFirst();

        while(cursor_user_relationship.isAfterLast() == false){
           // array_list.add(cursor_user_relationship.getString(cursor_user_relationship.getColumnIndex("nickname")));
            //array_list_contacted_user_id.add(cursor_user_relationship.getString(cursor_user_relationship.getColumnIndex("contacted_user_id")));
            cursor_user_relationship.moveToNext();
        }


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_SURVEY ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            jsonArray = new JSONArray(response);

                            for (int i=0; i<jsonArray.length(); i++) {
                                JSONObject user_relationship = jsonArray.getJSONObject(i);

                               // DB.insertUserRelationshipData( user_id,user_relationship.getString("nickname"),user_relationship.getString("contacted_user_id"),user_relationship.getString("type"));
                            }

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
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);



    }
}
