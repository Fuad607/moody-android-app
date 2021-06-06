package com.example.moody;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.moody.ui.main.SectionsPagerAdapter;
import com.example.moody.databinding.ActivityMainBinding;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public static int q1range = 5;
    public static int q2range = 5;
    public static HashMap<String, String> q3 = new HashMap<>();
    public static HashMap<String, String> q4 = new HashMap<String, String>();
    public static String q4question = "", q4answer = "";
    DBHelper DB;
    String USER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        System.out.println(viewPager);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton next_button = binding.surveyNext;
        FloatingActionButton prev_button = binding.surveyPrev;
        FloatingActionButton finish_survey = binding.finishSurvey;
        if (viewPager.getCurrentItem() == 0) {
            prev_button.hide();
        }
        SharedPreferences sharedPreferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        USER_ID = sharedPreferences.getString("USER_ID", "");

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prev_button.show();
                int current_page = viewPager.getCurrentItem();
                if (current_page < 3) {
                    viewPager.setCurrentItem(current_page + 1);
                } else if (current_page == 3) {

                    //Toast.makeText( special_situation.getText(), "Selected: " , Toast.LENGTH_LONG).show();
                    // find the radiobutton by returned id#

                    Log.d("output", q1range + ", " + q2range + ", " + q3.toString() + ", " + q4question + ", " + q4answer+", "+q4);
                    DB = new DBHelper(MainActivity.this);
                    Long survey_id = DB.insertSurvey(USER_ID, q1range, q2range);

                    for (Map.Entry<String,String> entry : q3.entrySet())
                        DB.insertUserMeeting( survey_id.toString(), entry.getKey(),  entry.getValue());

                    for (Map.Entry<String,String> entry : q4.entrySet())
                        DB.insertUserSpecialSituation( survey_id.toString(), entry.getKey(),  entry.getValue());

                    startActivity(new Intent(MainActivity.this, MenuActivity.class));
                    finish();
                }

            }
        });
        prev_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current_page = viewPager.getCurrentItem();
                viewPager.setCurrentItem(current_page - 1);
                if (current_page == 1) {
                    prev_button.hide();
                }
            }
        });
        finish_survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MenuActivity.class));
                finish();
            }
        });

        Q3Fragment q3RecyclerViewFragment = new Q3Fragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.view_pager, q3RecyclerViewFragment);
        fragmentTransaction.commit();
    }
}