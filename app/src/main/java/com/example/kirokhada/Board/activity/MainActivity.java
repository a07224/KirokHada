package com.example.kirokhada.Board.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.kirokhada.R;
import com.example.kirokhada.Board.fragment.ListFragment;
import com.example.kirokhada.Board.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;  //bottom navigation view
    private FragmentManager fm;
    private FragmentTransaction ft;
    private ListFragment listFragment;
    private ProfileFragment profileFragment;
    private LinearLayout Main_FPL, Main_FWL, Main_PV;
    private FrameLayout Main_bg;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_list:
                    setFrag(0);
                    break;
                case R.id.action_profile:
                    setFrag(1);
                    break;

                case R.id.action_review:
                    fab();
                    break;
            }

            return true;
        });
        listFragment = new ListFragment();
        profileFragment = new ProfileFragment();
        setFrag(0);     //첫 fragment 화면을 무엇으로 지정할 지 설정함
    }


    private void init() {
        Main_FWL = findViewById(R.id.floating_writing_layout);
        Main_FPL = findViewById(R.id.floating_photo_layout);
        Main_PV = findViewById(R.id.main_pop_view);
        Main_bg = findViewById(R.id.main_bg_frame);
    }

    //fragment 교체가 일어나는 실행문
    private void setFrag(int n) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();     //실제적인 fragment 교체

        switch (n) {
            case 0:
                ft.replace(R.id.frame_content, listFragment);
                ft.commit();        //저장을 의미합니다.
                break;
            case 1:
                ft.replace(R.id.frame_content, profileFragment);
                ft.commit();
                break;
        }
    }

    private void fab() {
        Main_bg.setVisibility(View.VISIBLE);
        Main_PV.setVisibility(View.VISIBLE);
        Main_FPL.setOnClickListener(view -> {
            Intent photoIntent = new Intent();
            startActivity(photoIntent);
        });
        Main_FWL.setOnClickListener(view -> {
            Intent writeIntent = new Intent(this, WritePostActivity.class);
            startActivity(writeIntent);
        });
        Main_bg.setOnClickListener(view -> {
            Main_PV.setVisibility(View.GONE);
            Main_bg.setVisibility(View.GONE);
        });
    }
}