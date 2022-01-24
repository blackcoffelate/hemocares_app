package com.example.hemocares.utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.hemocares.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class OnBoard extends AppCompatActivity {

    private ViewPager _onBoardPager;

    TabLayout _tabLayout;
    Animation animBtn;
    TextView skipBtn;
    Button nextBtn, getStartedBtn;

    OnBoardPagerAdapter _onBoardPagerAdapter;

    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (restorePrefData()) {
            Intent onBoardScreen = new Intent(getApplicationContext(), SplashScreen.class);
            startActivity(onBoardScreen);
            finish();
        }

        setContentView(R.layout.activity_on_board);

        nextBtn = findViewById(R.id.nextBtns);
        getStartedBtn = findViewById(R.id.getStartedBtns);
        skipBtn = findViewById(R.id.skipBtns);
        animBtn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_animation);

        _tabLayout = findViewById(R.id.indicatorTabs);
        _onBoardPager = findViewById(R.id.viewPager);

        final List<OnBoardItem> _onBoardItem = new ArrayList<>();
        _onBoardItem.add(new OnBoardItem("Hemocare", "Hemocare hadir dengan tujuan untuk menghubungkan seluruh pahlawan darah dalam satu aplikasi.", R.raw.anima));
        _onBoardItem.add(new OnBoardItem("Temukan Pahlawan", "Cari disini pahlawan darah yang dekat dengan daerahmu untuk mendonorkan darahnya. Kami akan memberikan lokasi terdekat dari lokasi kamu sekarang.", R.raw.animb));
        _onBoardItem.add(new OnBoardItem("Berbuat Kebaikan", "Eh.. kamu juga bisa kok jadi salah satu dari pahlawan darah itu. Bagikan kebaikan kamu sekarang juga.", R.raw.animc));
        _onBoardItem.add(new OnBoardItem("Berdonor Sekarang", "Gabung sekarang dan jadilah bagian dari pahlawan kebaikan, karena darahmu itu sangat berguna untuk orang lain.", R.raw.animd));

        _onBoardPagerAdapter = new OnBoardPagerAdapter(this, _onBoardItem);
        _onBoardPager.setAdapter(_onBoardPagerAdapter);

        _tabLayout.setupWithViewPager(_onBoardPager);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position = _onBoardPager.getCurrentItem();
                if (position < _onBoardItem.size()) {
                    position++;
                    _onBoardPager.setCurrentItem(position);
                }

                if (position == _onBoardItem.size()-1) {
                    loadLastScreen();
                }
            }
        });

        _tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == _onBoardItem.size()-1) {
                    loadLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        getStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent splashScreens = new Intent(getApplicationContext(), SplashScreen.class);
                startActivity(splashScreens);
                savePrefData();
                Animatoo.animateSlideUp(getApplicationContext());
                finish();
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _onBoardPager.setCurrentItem(_onBoardItem.size());
            }
        });
    }

    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpnend", false);
        return isIntroActivityOpnendBefore;
    }

    private void savePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpnend", true);
        editor.commit();
    }

    private void loadLastScreen() {
        nextBtn.setVisibility(View.INVISIBLE);
        getStartedBtn.setVisibility(View.VISIBLE);
        skipBtn.setVisibility(View.INVISIBLE);
        _tabLayout.setVisibility(View.INVISIBLE);
        getStartedBtn.setAnimation(animBtn);
    }
}