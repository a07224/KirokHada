package com.example.kirokhada.Board.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
    private FloatingActionButton fabOne;
    private FloatingActionButton fabTwo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.action_list :
                    setFrag(0);
                    break;
                case R.id.action_profile :
                    setFrag(1);
                    break;

                case R.id.action_review :
                    fab();
                    break;
            }

            return true;
        });
        listFragment = new ListFragment();
        profileFragment = new ProfileFragment();
        setFrag(0);     //첫 fragment 화면을 무엇으로 지정할 지 설정함
    }


    //fragment 교체가 일어나는 실행문
    private void setFrag(int n){
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();     //실제적인 fragment 교체

        switch (n){
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

    }

//    public void fabAction() {
//        fab = view.findViewById(R.id.write_text);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(getActivity(), WritePostActivity.class);
//
//                onDestroyView();
//
//                getDataList.clear();
//                bordAdapter.notifyDataSetChanged();
//                recyclerView.removeAllViewsInLayout();
//                recyclerView.removeAllViews();
//                bordAdapter.refresh();
//                bordAdapter.notifyDataSetChanged();
//
//                startActivity(intent);
//            }
//        });
//    }
}