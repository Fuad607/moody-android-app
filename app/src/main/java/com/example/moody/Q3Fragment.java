package com.example.moody;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Q3Fragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2[];
    RecyclerView recyclerView;
    private static String URL_USER_RELATIONSHIP = "http://192.168.0.16/api/userrelationship/getallbyid/";
    SharedPreferences sharedPreferences;
    ArrayList name;
    JSONObject jsonObject;
    JSONArray jsonArray;
     public String USER_ID;
    DBHelper DB;

    List<String> allNames = new ArrayList<String>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_q3, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_user_relationship);

        sharedPreferences = this.getActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        USER_ID = sharedPreferences.getString("USER_ID", "");
        System.out.println(USER_ID);
        name=DB.getAllCotacts( );
        System.out.println(name);


        name = new ArrayList();
        for (int i = 0; i < UserRelationship.names.length; i++) {
            name.add(UserRelationship.names);
        }

        HelperAdapter helperAdapter = new HelperAdapter(getContext(), name);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(helperAdapter);
        return view;
    }
}