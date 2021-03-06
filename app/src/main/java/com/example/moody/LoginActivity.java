package com.example.moody;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText email,password;
    private Button btn_login;
    private TextView link_regist;
    private ProgressBar loading;
    private static String URL_USER_RELATIONSHIP = "https://collectivemoodtracker.herokuapp.com/api/userrelationship/getallbyid/";
    private static String URL_SURVEY = "https://collectivemoodtracker.herokuapp.com/api/survey/";
    private static String URL_USER_MEETING = "https://collectivemoodtracker.herokuapp.com/api/usermeeting/";
    private static String URL_USER_SPECIALSITUATION = "https://collectivemoodtracker.herokuapp.com/api/userspecialsituation/";
    String USER_ID;
    DBHelper DB;
    JSONArray jsonArray;
    JSONObject jsonObjectUserData;
    private  static  String URL_LOGIN="https://collectivemoodtracker.herokuapp.com/api/users/checkUser";
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        SharedPreferences sharedPreferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        USER_ID = sharedPreferences.getString("USER_ID","");
        if(!USER_ID.isEmpty()){
            startActivity(new Intent(LoginActivity.this,MenuActivity.class));
            finish();
        }

        loading= findViewById(R.id.loading);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        btn_login=findViewById(R.id.btn_login);
        //link_regist=findViewById(R.id.link_regist);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  String memail=  email.getText().toString().trim();
                  String mpassword= password.getText().toString().trim();
                if(memail.isEmpty()){
                    email.setError("Please insert email address!");
                }else if(!Patterns.EMAIL_ADDRESS.matcher(memail).matches()){
                    email.setError("Please insert valid email address!");
                } else if(mpassword.isEmpty()){
                    password.setError("Please insert password!");
                }else{
                    email.setError(null);
                    password.setError(null);
                    Login();
                }
            }
        });

   /*     link_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
          //change
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });*/
    }

    private void Login(){
        loading.setVisibility(View.VISIBLE);
        btn_login.setVisibility(View.GONE);

        final String email= this.email.getText().toString().trim();
        final String password= this.password.getText().toString().trim();

        StringRequest stringRequest= new StringRequest(Request.Method.PUT, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject=new JSONObject(response);
                            String respond_message=jsonObject.getString("response");
                            System.out.println(respond_message);
                            if(respond_message.equals("success")){
                                jsonObjectUserData=jsonObject.getJSONObject("body");
                                String user_id = jsonObjectUserData.getString("id");

                                sharedPreferences= getSharedPreferences("USER_DATA", MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString("USER_ID",user_id);
                                editor.apply();

                                sync_data(user_id);
                                Toast.makeText(LoginActivity.this,"Successly Login",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this,MenuActivity.class));
                            }
                            else if(respond_message.equals("wrong_credential")){
                                Toast.makeText(LoginActivity.this,"False Email or Password ",Toast.LENGTH_SHORT).show();
                                loading.setVisibility(View.GONE);
                                btn_login.setVisibility(View.VISIBLE);
                            }
                            else if(respond_message.equals("not_exist")){
                                Toast.makeText(LoginActivity.this,"This email not registered",Toast.LENGTH_SHORT).show();
                                loading.setVisibility(View.GONE);
                                btn_login.setVisibility(View.VISIBLE);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this,"Error"+e.toString(),Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);
                            btn_login.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this,"Error"+error.toString(),Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        btn_login.setVisibility(View.VISIBLE);
                    }
                })
        {
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("email",email);
                params.put("password",password);
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void sync_data(String user_id){
        DB = new DBHelper(LoginActivity.this);
        DB.deleteAlUserRelationship();
        DB.deleteAllSurvey();
        DB.deleteAllUserMeeting();
        DB.deleteAllUserSpecialSituation();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_USER_RELATIONSHIP + user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            jsonArray = new JSONArray(response);

                            for (int i=0; i<jsonArray.length(); i++) {
                                JSONObject user_relationship = jsonArray.getJSONObject(i);

                                DB.insertUserRelationshipData( user_id,user_relationship.getString("nickname"),user_relationship.getString("contacted_user_id"),user_relationship.getString("type"));
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        StringRequest stringSurvey = new StringRequest(Request.Method.GET, URL_SURVEY+ user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArrayApi = new JSONArray(response);

                            for (int i=0; i<jsonArrayApi.length(); i++) {
                                JSONObject api_survey_result = jsonArrayApi.getJSONObject(i);

                                Long db_survey_id = DB.insertSurvey( user_id,api_survey_result.getInt("mood_level"),api_survey_result.getInt("relaxed_level"),api_survey_result.getInt("sync"), String.valueOf(api_survey_result.getInt("timestamp")));

                                StringRequest stringUserMeeting = new StringRequest(Request.Method.GET, URL_USER_MEETING + api_survey_result.getInt("id"),
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    jsonArray = new JSONArray(response);

                                                    for (int i=0; i<jsonArray.length(); i++) {
                                                        JSONObject user_meeting = jsonArray.getJSONObject(i);

                                                        DB.insertUserMeeting(db_survey_id.toString(),user_meeting.getString("contacted_user_id"),user_meeting.getString("meeting_type"));
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
                                RequestQueue requestMeeting= Volley.newRequestQueue(getApplicationContext());
                                requestMeeting.add(stringUserMeeting);


                                StringRequest stringUserSpecialSituation = new StringRequest(Request.Method.GET, URL_USER_SPECIALSITUATION + api_survey_result.getInt("id"),
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    jsonArray = new JSONArray(response);

                                                    for (int i=0; i<jsonArray.length(); i++) {
                                                        JSONObject user_special_situation = jsonArray.getJSONObject(i);

                                                        DB.insertUserSpecialSituation(db_survey_id.toString(),user_special_situation.getString("special_situation"),user_special_situation.getString("special_situation_type"));
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
                                RequestQueue requestUserSpecialSituation= Volley.newRequestQueue(getApplicationContext());
                                requestUserSpecialSituation.add(stringUserSpecialSituation);

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
        RequestQueue requestSurvey= Volley.newRequestQueue(this);
        requestSurvey.add(stringSurvey);
    }
}