package com.example.moody;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class LogFragment extends Fragment {
    SharedPreferences sharedPreferences;
    String USER_ID;
    DBHelper DB;
    RecyclerView recyclerView;

    public LogFragment() {
        // Required empty public constructor
    }

    public static LogFragment newInstance(String param1, String param2) {
        LogFragment fragment = new LogFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_log, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_log_fragment);

        sharedPreferences = this.getActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        USER_ID = sharedPreferences.getString("USER_ID", "");
        ArrayList<String> array_list_timestamp = new ArrayList<String>();
        ArrayList<String> array_list_mood = new ArrayList<String>();
        ArrayList<String> array_list_relax = new ArrayList<String>();


        DB=new DBHelper(getContext());
        Cursor cursor_user_survey = DB.getAllSurvey();
        cursor_user_survey.moveToFirst();

        while(cursor_user_survey.isAfterLast() == false){

            array_list_timestamp.add(getDate(Long.parseLong(cursor_user_survey.getString(cursor_user_survey.getColumnIndex("ts")))));
            array_list_mood.add(cursor_user_survey.getString(cursor_user_survey.getColumnIndex("mood_level")));
            array_list_relax.add(cursor_user_survey.getString(cursor_user_survey.getColumnIndex("relaxed_level")));
            cursor_user_survey.moveToNext();
        }


        final HelperLogAdapter[] helperAdapter = {new HelperLogAdapter(getContext(),array_list_timestamp, array_list_mood, array_list_relax)};
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(helperAdapter[0]);



        return v;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd-MM-yyyy HH:mm", cal).toString();
        return date;
    }
}