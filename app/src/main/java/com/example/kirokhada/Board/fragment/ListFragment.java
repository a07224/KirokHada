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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.Any;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ListFragment extends Fragment {

    private View view;

    private BordAdapter bordAdapter;

    RecyclerView recyclerView;

    EditText searchView;

    ArrayList<BordInfo> getDataList = new ArrayList<>();
    ArrayList<BordInfo> mergeDataList = new ArrayList<>();


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
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataList.clear();
                bordAdapter.notifyDataSetChanged();
                recyclerView.removeAllViewsInLayout();
                recyclerView.removeAllViews();
                bordAdapter.refresh();
                bordAdapter.notifyDataSetChanged();
                getData();
                swipeRefreshLayout.setRefreshing(false);
            }
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

                Log.d("test", "1");
                List<BordInfo> resultData = task.getResult().toObjects(BordInfo.class);

                getDataList.clear();
                getDataList.addAll(resultData);

                for (int i = 0; i < getDataList.size(); i++) {
                    bordAdapter.addData(getDataList.get(i));
                }
            }
        });

        db.collection("book-private").document(userID).collection(email).get().addOnCompleteListener(task_end -> {
            if (task_end.isSuccessful()) {

                Log.d("test", "2");
                List<BordInfo> mergeData = task_end.getResult().toObjects(BordInfo.class);

                getDataList.clear();
                getDataList.addAll(mergeData);

                for (int i = 0; i < getDataList.size(); i++) {
                    bordAdapter.addData(getDataList.get(i));
                }
            }
        });


        bordAdapter.setList();
        bordAdapter.notifyDataSetChanged();
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