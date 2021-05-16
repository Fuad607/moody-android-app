package com.example.moody;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.view.View;

import org.jetbrains.annotations.NotNull;

public class MenuActivity extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        SharedPreferences sharedPreferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String USER_ID = sharedPreferences.getString("USER_ID","");

        BottomNavigationView bottom_nav =findViewById(R.id.bottom_nav);
        bottom_nav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem menuItem) {
            Fragment selectedFragment =null;

            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    selectedFragment= new HomeFragment();
                    break;
                case R.id.nav_history:
                    selectedFragment= new HistoryFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();

            return true;
        }
    };
}