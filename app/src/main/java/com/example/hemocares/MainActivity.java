package com.example.hemocares;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.hemocares.view.dailyreport.DailyReport;
import com.example.hemocares.view.dailyreport.DailyReportAdd;
import com.example.hemocares.view.dashboard.Dashboard;
import com.example.hemocares.view.findpeople.FindPeople;
import com.example.hemocares.view.profile.Profile;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    boolean backPress = false;

    BottomAppBar bottomAppBars;
    Fragment fragment;
    FragmentManager fragmentManager;
    FloatingActionButton reportDailyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomAppBars = findViewById(R.id.bottomAppBar);
        reportDailyButton = findViewById(R.id.reportButton);

        loadFragment(new Dashboard());
        bottomAppBars.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                fragment = null;
                switch (item.getItemId()) {
                    case R.id.dashboard:
                        fragment = new Dashboard();
                        break;
                    case R.id.search:
                        fragment = new FindPeople();
                        break;
                    case R.id.daily:
                        fragment = new DailyReport();
                        break;
                    case R.id.me:
                        fragment = new Profile();
                        break;
                }

                if (fragment != null) {
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                } else {
                    Log.e(TAG, "Error Creating Fragment");
                }

                return false;
            }
        });

        reportDailyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DailyReportAdd.class));
                Animatoo.animateSlideDown(MainActivity.this);
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager f = getSupportFragmentManager();
        FragmentTransaction t = f.beginTransaction();
        t.replace(R.id.fragment_container, fragment).commit();
        t.addToBackStack(null);
    }

    @Override
    public void onBackPressed() {
        if (backPress){
            super.onBackPressed();
            return;
        }

        this.backPress = true;
        Toasty.info(this, "Tekan sekali lagi untuk keluar...", Toasty.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backPress = false;
            }
        }, 2000);
    }
}