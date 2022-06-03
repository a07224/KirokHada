package com.example.kirokhada.Board.data;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kirokhada.Board.utils.HangulUtils;
import com.example.kirokhada.Board.utils.Utils;
import com.example.kirokhada.R;
import com.example.kirokhada.Board.activity.SbordActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class BordAdapter extends RecyclerView.Adapter<BordAdapter.itemViewHolder> {

    private final ArrayList<BordInfo> listData = new ArrayList<>();
    private ArrayList<BordInfo> arrayList;
    private final Context contexts;

    public void addData(BordInfo data) {
        listData.add(data);
    }

    public BordAdapter(Context context) {
        contexts = context;
    }

    public void refresh() {
        listData.clear();
    }

    public void setList() {
        arrayList = new ArrayList<>();
        arrayList.addAll(listData);
    }

    public void filter(String searchText) {
        searchText = searchText.toLowerCase(Locale.getDefault());
        listData.clear();
        if (searchText.length() == 0) {
            if (arrayList != null) {
                listData.addAll(arrayList);
            }
        } else {
            if (arrayList != null) {
                for (BordInfo wp : arrayList) {
                    if(Utils.isNumber(searchText)){
                        if(wp.getTitle().contains(searchText)){
                            listData.add(wp);
                        }
                    } else {
                        String iniName = HangulUtils.getHangulInitialSound(wp.getTitle(), searchText);
                        if (iniName.contains(searchText)) {
                            listData.add(wp);
                        } else if (wp.getTitle().toLowerCase(Locale.getDefault()).contains(searchText)) {
                            listData.add(wp);
                        }
                    }
                }
            }

        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bord_recyclerview_item, parent, false);

        return new itemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class itemViewHolder extends RecyclerView.ViewHolder {


        String title, author, rating, keyword, content, time, email, uid, status, sc;

        private final TextView titleTextView;
        private final TextView subjectTextView;
        private final TextView timeTextView;
        private final View itemLayout;
        private final CircleImageView circleImageView;


        itemViewHolder(View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.bord_item_title);
            subjectTextView = itemView.findViewById(R.id.bord_item_subject);
            itemLayout = itemView.findViewById(R.id.bord_item_layout);
            timeTextView = itemView.findViewById(R.id.bord_item_time);
            circleImageView = itemView.findViewById(R.id.bord_item_Image);

            itemClickListener();
        }

        private void itemClickListener() {

            itemLayout.setOnClickListener(view -> {
                Intent intent = new Intent(contexts, SbordActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("time", time);
                intent.putExtra("author", author);
                intent.putExtra("rating", rating);
                intent.putExtra("keyword", keyword);
                intent.putExtra("content", content);
                intent.putExtra("email", email);
                intent.putExtra("uid", uid);
                intent.putExtra("status", status);
                intent.putExtra("sc", sc);
                ContextCompat.startActivity(contexts, intent, new Bundle());
            });
        }

        void onBind(BordInfo data) {

            titleTextView.setText(data.getTitle());
            subjectTextView.setText(data.getAuthor());

            title = data.getTitle();
            author = data.getAuthor();
            rating = data.getRating();
            keyword = data.getKeyword();
            content = data.getContents();
            email = data.getEmail();
            time = data.getDate();
            uid = data.getUid();
            status = data.getStatus();
            sc = data.getSc();
            timeTextView.setText(time);

            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(uid);
            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            if(Objects.requireNonNull(document.getData()).get("photoUrl") != null){

                                Glide.with(contexts).load(document.getData().get("photoUrl")).centerCrop().override(500).into(circleImageView);
                            }
                        }
                    }
                }
            });
        }
    }
}
