package com.example.moody;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText email,password;
    private Button btn_login;
    private TextView link_regist;
    private ProgressBar loading;
    private  static  String URL_LOGIN="http://192.168.0.231/api/users/checkUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loading= findViewById(R.id.loading);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        btn_login=findViewById(R.id.btn_login);
        link_regist=findViewById(R.id.link_regist);


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

        link_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

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
                            String success=jsonObject.getString("response");
                            if(success.equals("success")){
                                Toast.makeText(LoginActivity.this,"Successly Login",Toast.LENGTH_SHORT).show();

                              startActivity(new Intent(LoginActivity.this,MenuActivity.class));


                            }
                            else if(success.equals("not_exist")){
                                Toast.makeText(LoginActivity.this,"False Email or Password ",Toast.LENGTH_SHORT).show();
                                loading.setVisibility(View.GONE);
                                btn_login.setVisibility(View.VISIBLE);                            }
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
}