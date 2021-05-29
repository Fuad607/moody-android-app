package com.example.moody;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class RegisterActivity extends AppCompatActivity {
    private EditText nickname, email, password,c_password;
    private Button btn_regist;
    private ProgressBar loading;
    private static String URL_REGIST="http://192.168.0.231/api/users";
    DBHelper DB;
    public static final String  SHARED_PREFS="sharedPrefs";
    public static final String  TEXT="text";
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loading= findViewById(R.id.loading);
        nickname=findViewById(R.id.username);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        c_password=findViewById(R.id.c_password);
        btn_regist=findViewById(R.id.btn_regist);

        btn_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String memail=  email.getText().toString().trim();
                String mpassword= password.getText().toString().trim();
                String mcpassword= c_password.getText().toString().trim();
                if(memail.isEmpty()){
                    email.setError("Please insert email address!");
                }else if(!Patterns.EMAIL_ADDRESS.matcher(memail).matches()){
                    email.setError("Please insert valid email address!");
                } else if(mpassword.isEmpty()){
                    password.setError("Please insert password!");
                } else if(mcpassword.isEmpty()){
                    c_password.setError("Please insert password again!");
                } else if(!mcpassword.equals(mpassword)){
                    c_password.setError("Password does not match!");
                }
                else{
                    email.setError(null);
                    password.setError(null);
                    c_password.setError(null);
                    Regist();
                }
            }
        });
    }

    private void Regist(){
        loading.setVisibility(View.VISIBLE);
        btn_regist.setVisibility(View.GONE);

        final String nickname= this.nickname.getText().toString().trim();
        final String email= this.email.getText().toString().trim();
        final String password= this.password.getText().toString().trim();
        DB = new DBHelper(RegisterActivity.this);

        StringRequest stringRequest= new StringRequest(Request.Method.POST, URL_REGIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject=new JSONObject(response);
                            String success=jsonObject.getString("response");

                            if(success.equals("success")){
                                JSONObject body=jsonObject.getJSONObject("body");
                                String user_id = body.getString("id");

                                sharedPreferences= getSharedPreferences("USER_DATA", MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString("USER_ID",user_id);
                                editor.apply();
                                Toast.makeText(RegisterActivity.this,"Register Success",Toast.LENGTH_SHORT).show();
                                DB.insertUserData( user_id,nickname,email,password);
                                startActivity(new Intent(RegisterActivity.this,MenuActivity.class));
                            }
                            else if(success.equals("email_exits")){
                                Toast.makeText(RegisterActivity.this,"Email already used",Toast.LENGTH_SHORT).show();
                                loading.setVisibility(View.GONE);
                                btn_regist.setVisibility(View.VISIBLE);                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this,"Register Error"+e.toString(),Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);
                            btn_regist.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this,"Register Error"+error.toString(),Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        btn_regist.setVisibility(View.VISIBLE);
                    }
                })
        {
          protected Map<String,String> getParams() throws AuthFailureError{
              Map<String,String> params=new HashMap<>();
              params.put("nickname",nickname);
              params.put("email",email);
              params.put("password",password);
              return params;
          }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}