package com.example.moody;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import com.example.moody.ui.main.SectionsPagerAdapter;
import com.example.moody.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public SeekBar q1range;
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

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prev_button.show();
                int current_page=viewPager.getCurrentItem();
                if(current_page<3){
                    viewPager.setCurrentItem(current_page+1);
                }else if(current_page==3){
                    //send data
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


    }
}