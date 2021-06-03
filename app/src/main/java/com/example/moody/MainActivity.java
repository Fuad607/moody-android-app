package com.example.moody;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.moody.ui.main.SectionsPagerAdapter;
import com.example.moody.databinding.ActivityMainBinding;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public static int q1range = 5;
    public static int q2range = 5;
    public static HashMap<String, String> q3 = new HashMap<>();
    public static String q4question = "", q4answer = "";
    DBHelper DB;
    String USER_ID;

    RadioGroup radioGroup;
    RadioButton selectedRadioButton;

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
        if(viewPager.getCurrentItem()==0){
            prev_button.hide();
        }
        SharedPreferences sharedPreferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        USER_ID = sharedPreferences.getString("USER_ID","");

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prev_button.show();
                int current_page=viewPager.getCurrentItem();
                if(current_page<3){
                    viewPager.setCurrentItem(current_page+1);
                }else if(current_page==3){
                    //send data

                    Spinner  special_situation = (Spinner)view.findViewById(R.id.special_situation);
                     //Toast.makeText( special_situation.getText(), "Selected: " , Toast.LENGTH_LONG).show();
                    // find the radiobutton by returned id#
                      radioGroup = findViewById(R.id.radioGroup);
                    int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                    if (selectedRadioButtonId != -1) {
                        selectedRadioButton = findViewById(selectedRadioButtonId);
                        String selectedRbText = selectedRadioButton.getText().toString();
                        // System.out.println(selectedRbText+" is selected");
                         MainActivity.q4answer = selectedRbText;
                     }

                    Log.d("output", q1range + ", " + q2range + ", " + q3.toString() + ", " + q4question + ", " + q4answer);
                    DB = new DBHelper(MainActivity.this);
                    DB.inserSurvey(USER_ID,q1range,q2range);

                }

            }
        });
        prev_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current_page=viewPager.getCurrentItem();
                viewPager.setCurrentItem(current_page-1);
                if(current_page==1){
                    prev_button.hide();
                }
            }
        });
        finish_survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,MenuActivity.class));
                finish();
            }
        });

        Q3Fragment q3RecyclerViewFragment= new Q3Fragment();
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.view_pager,q3RecyclerViewFragment);
        fragmentTransaction.commit();
    }
}