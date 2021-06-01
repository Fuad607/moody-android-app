package com.example.moody;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class UserRelationship  extends AppCompatActivity {

    SharedPreferences sharedPreferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
    String  USER_ID = sharedPreferences.getString("USER_ID","");
    public  static  String URL_LOGIN="http://192.168.0.16/api/userrelationship/getallbyid/";

    public static String[] names =new String[]{
            "test","test2","test3","test4" ,"test","test2","test3","test4", "test","test2","test3","test4","test","test2","test3","test4", "test","test2","test3","test4"
    };
}
