package com.example.kirokhada.Board.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.kirokhada.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SbordActivity extends AppCompatActivity {

    Button modify_btn, del_btn, update_btn;

    TextView name, date, title, author, rating, keyword, content;

    EditText titleEdit, authorEdit, ratingEdit, keywordEdit, contentEdit;

    String titleText, timeText, authorText, ratingText, keywordText, contentText, emailText, sc, uid, status;

    String titleUpdate, uploadTimeUpdate, authorUpdate, ratingUpdate, keywordUpdate, contentUpdate, emailUpdate = "";

    String currentTimeText;

    CircleImageView circleImageView;

    private FirebaseFirestore fireStore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_board);

        init();

        getData();

        transparency();

        checkUser();

        userSerVice();

    }

    private void transparency() {
        Drawable alpha = ((ImageView) findViewById(R.id.cute)).getDrawable();
        alpha.setAlpha(100);
    }

    private void init() {

        circleImageView = findViewById(R.id.userProfile_s);

        title = findViewById(R.id.s_title_textView);
        name = findViewById(R.id.userName_s);
        date = findViewById(R.id.dateCount);

        author = findViewById(R.id.authorText);
        rating = findViewById(R.id.ratingText);
        keyword = findViewById(R.id.keywordText);
        content = findViewById(R.id.contentText);

        titleEdit = findViewById(R.id.titleEditText);
        authorEdit = findViewById(R.id.authorEditText);
        ratingEdit = findViewById(R.id.ratingEditText);
        keywordEdit = findViewById(R.id.keywordEditText);
        contentEdit = findViewById(R.id.contentEditText);

        modify_btn = findViewById(R.id.modify_btn);
        del_btn = findViewById(R.id.del_btn);
        update_btn = findViewById(R.id.update_btn);
    }

    private void checkUser() {
        String nowUser;
        FirebaseUser firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseAuth != null) {
            nowUser = firebaseAuth.getEmail();
            Log.d("email", emailText);
            Log.d("email", nowUser + " = nowUSer");

            if (nowUser != null && nowUser.equals(emailText)) {
                Log.d("email", "ok");
                modify_btn.setVisibility(View.VISIBLE);
                del_btn.setVisibility(View.VISIBLE);
            }
        }
    }

    private void userSerVice() {

        modify_btn.setOnClickListener(view -> {
            updateUI();
            updateData();
        });

        del_btn.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(SbordActivity.this);
            builder.setTitle("게시물 삭제")
                    .setMessage("정말로 게시한 글을 삭제 하시겠습니까?");
            builder.setPositiveButton("네",
                    (dialog, which) -> {
                        fireStore = FirebaseFirestore.getInstance();

                        fireStore.collection("book").document(sc).delete()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "삭제 완료!", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                        finish();
                                        onBackPressed();
                                    }
                                });
                    });
            builder.setNegativeButton("아니요", (dialog, which) -> dialog.dismiss());

            builder.show();

        });

    }

    private void updateUI() {
        modify_btn.setVisibility(View.GONE);
        del_btn.setVisibility(View.GONE);
        title.setVisibility(View.GONE);

        update_btn.setVisibility(View.VISIBLE);

        titleEdit.setVisibility(View.VISIBLE);
        authorEdit.setVisibility(View.VISIBLE);
        ratingEdit.setVisibility(View.VISIBLE);
        keywordEdit.setVisibility(View.VISIBLE);
        contentEdit.setVisibility(View.VISIBLE);

        titleEdit.setText(titleText);
        authorEdit.setText(authorText);
        ratingEdit.setText(ratingText);
        keywordEdit.setText(keywordText);
        contentEdit.setText(contentText);

        author.setText("작가 : ");
        rating.setText("평점 : ");
        keyword.setText("키워드 : ");
        content.setText("내용 : \n\n");
    }

    private void finishUI() {
        modify_btn.setVisibility(View.VISIBLE);
        del_btn.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);

        update_btn.setVisibility(View.GONE);

        titleEdit.setVisibility(View.INVISIBLE);
        authorEdit.setVisibility(View.INVISIBLE);
        ratingEdit.setVisibility(View.INVISIBLE);
        keywordEdit.setVisibility(View.INVISIBLE);
        contentEdit.setVisibility(View.INVISIBLE);

    }

    private void updateData() {

        update_btn.setOnClickListener(v -> {

            titleUpdate = titleEdit.getText().toString();
            authorUpdate = authorEdit.getText().toString();
            ratingUpdate = ratingEdit.getText().toString();
            keywordUpdate = keywordEdit.getText().toString();
            contentUpdate = contentEdit.getText().toString();

            Date uploadTime = new Date(System.currentTimeMillis());

            SimpleDateFormat mFormat = new SimpleDateFormat("MM/dd HH:mm:ss");

            uploadTimeUpdate = mFormat.format(uploadTime);

            emailUpdate = emailText;

            if (!titleUpdate.equals("") && !authorUpdate.equals("") && !ratingUpdate.equals("")
                    && !keywordUpdate.equals("") && !contentUpdate.equals("") && emailUpdate != null) {

                Map<String, Object> upDateMap = new HashMap<>();
                upDateMap.put("title", titleUpdate);
                upDateMap.put("author", authorUpdate);
                upDateMap.put("rating", ratingUpdate);
                upDateMap.put("keyword", keywordUpdate);
                upDateMap.put("contents", contentUpdate);
                upDateMap.put("email", emailUpdate);
                upDateMap.put("date", uploadTimeUpdate);
                upDateMap.put("uid", uid);
                upDateMap.put("status", status);

                fireStore = FirebaseFirestore.getInstance();

                fireStore.collection("book").document(sc).update(upDateMap)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "수정 완료!", Toast.LENGTH_SHORT).show();
                            }
                        });

                title.setText(titleUpdate);
                author.setText("작가 : " + authorUpdate);
                rating.setText("평점 : " + ratingUpdate);
                keyword.setText("키워드 : " + keywordUpdate);
                content.setText("내용 : \n\n" + contentUpdate);

                date.setText(uploadTimeUpdate);

                Date currentTime = new Date(System.currentTimeMillis());

                @SuppressLint("SimpleDateFormat") SimpleDateFormat cuFormat = new SimpleDateFormat("MM/dd HH:mm:ss");

                currentTimeText = cuFormat.format(uploadTime);

                titleText = titleUpdate;
                timeText = currentTimeText;
                authorText = authorUpdate;
                ratingText = ratingUpdate;
                keywordText = keywordUpdate;
                contentText = contentUpdate;

                finishUI();
            } else {
                Toast.makeText(getApplicationContext(), "빈 칸 없이 입력해 주세요.", Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void getData() {

        Intent intent = getIntent();

        titleText = intent.getStringExtra("title");
        timeText = intent.getStringExtra("time");
        authorText = intent.getStringExtra("author");
        ratingText = intent.getStringExtra("rating");
        keywordText = intent.getStringExtra("keyword");
        contentText = intent.getStringExtra("content");

        emailText = intent.getStringExtra("email");
        sc = intent.getStringExtra("sc");
        uid = intent.getStringExtra("uid");
        status = intent.getStringExtra("status");

        setData();
    }

    @SuppressLint("SetTextI18n")
    private void setData() {

        // 프로필 받아오는 코드
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(uid);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    if (document.exists()) {
                        if(document.getData().get("photoUrl") != null){
                            name.setText(document.getData().get("name").toString());
                            Glide.with(getApplicationContext()).load(document.getData().get("photoUrl")).centerCrop().override(500).into(circleImageView);
                        }
                    }
                }
            }
        });

        // 정상 데이터
        title.setText(titleText);
        date.setText(timeText);
        author.setText("작가 : " + authorText);
        rating.setText("평점 : " + ratingText);
        keyword.setText("키워드 : " + keywordText);
        content.setText("내용 : \n\n" + contentText);
    }
}
