package com.example.moody;

import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;

public class UserRelationship  extends AppCompatActivity {

    SharedPreferences sharedPreferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
    String  USER_ID = sharedPreferences.getString("USER_ID","");
    public  static  String URL_LOGIN="https://collectivemoodtracker.herokuapp.com/api/userrelationship/getallbyid/";

    public static String[] names =new String[]{
            "test","test2","test3","test4" ,"test","test2","test3","test4", "test","test2","test3","test4","test","test2","test3","test4", "test","test2","test3","test4"
    };
}
