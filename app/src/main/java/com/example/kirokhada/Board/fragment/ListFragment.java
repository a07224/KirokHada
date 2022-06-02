package com.example.kirokhada.Board.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.kirokhada.R;
import com.example.kirokhada.Board.data.BordAdapter;
import com.example.kirokhada.Board.data.BordInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ListFragment extends Fragment {

    private View view;

    private BordAdapter bordAdapter;

    RecyclerView recyclerView;

    EditText searchView;

    ArrayList<BordInfo> getDataList = new ArrayList<>();


    // DB
    String email = null;
    String userID = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerViewAction();
        refresh();
        filter();
        getUserData();

        return view;
    }

    private void getUserData() {
        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        if (auth != null) {
            userID = auth.getUid();
            email = auth.getEmail();
        }
    }

    private void refreshCycle() {
        getDataList.clear();
        bordAdapter.notifyDataSetChanged();
        recyclerView.removeAllViewsInLayout();
        recyclerView.removeAllViews();
        bordAdapter.refresh();
        bordAdapter.notifyDataSetChanged();
        getData();
    }

    @Override
    public void onResume() {
        super.onResume();

        searchView = view.findViewById(R.id.searchView);
        searchView.setText("");

        refreshCycle();

    }

    private void refresh() {
        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPinkPurple);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            getDataList.clear();
            bordAdapter.notifyDataSetChanged();
            recyclerView.removeAllViewsInLayout();
            recyclerView.removeAllViews();
            bordAdapter.refresh();
            bordAdapter.notifyDataSetChanged();
            getData();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    public void recyclerViewAction() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView = view.findViewById(R.id.Bord_RecyclerView);

        recyclerView.setLayoutManager(layoutManager);

        bordAdapter = new BordAdapter(getActivity());

        bordAdapter.refresh();

        recyclerView.setAdapter(bordAdapter);

    }

    public void getData() {
        // 데이터 받아오는 공간

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("book").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                List<BordInfo> resultData = task.getResult().toObjects(BordInfo.class);

                getDataList.clear();

                for (int i = 0; i < resultData.size(); i++) {
                    if(Objects.equals(resultData.get(i).getStatus(), "private")){
                        if(resultData.get(i).getEmail().equals(email)){
                            getDataList.add(resultData.get(i));
                        }else{
                            Log.d("list-00", "else loop");
                        }
                    }else {
                        getDataList.add(resultData.get(i));
                    }
                }
                // 정렬 해보자
                Collections.sort(getDataList, new Comparator<BordInfo>() {
                    @Override
                    public int compare(BordInfo o1, BordInfo o2) {
                        return o1.getDate().compareTo(o2.getDate());
                    }
                });

                Collections.reverse(getDataList);

                for (int i = 0; i < getDataList.size(); i++) {
                    bordAdapter.addData(getDataList.get(i));
                }

                bordAdapter.setList();
                bordAdapter.notifyDataSetChanged();
            }
        });
    }

    public void filter() {
        searchView = view.findViewById(R.id.searchView);

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = searchView.getText().toString().toLowerCase(Locale.getDefault());
                bordAdapter.filter(text);
            }
        });
    }
}