package com.example.kirokhada.Board.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kirokhada.R;
import com.example.kirokhada.Board.data.WriteInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class WritePostActivity extends AppCompatActivity {

    EditText titleEditText;
    EditText authorEditText;
    EditText ratingEditText;
    EditText keywordEditText;
    EditText contentEditText;

    // View
    Button upload_btn;
    Switch type_switch;

    // DB
    String email = null;
    String userID = null;

    // EditText에서 가져온 데이터
    String title, author, rating, keyword, content = null;

    // 체크 데이터
    String uploadTimeText = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        init();
        buttonListener();

    }

    private void buttonListener() {
        upload_btn.setOnClickListener(view -> {
            getData();

            if (title != null && author != null && rating != null && keyword != null && content != null) {
                uploadData();
                finish();
            } else {
                Toast showText = Toast.makeText(getApplicationContext(), "내용을 다 채워주세요~", Toast.LENGTH_SHORT);
                showText.show();
            }
        });
    }


    private void init() {

        upload_btn = findViewById(R.id.upload_button);
        type_switch = findViewById(R.id.type_switch);

        titleEditText = findViewById(R.id.bookTitle_editText);
        authorEditText = findViewById(R.id.author_editText);
        ratingEditText = findViewById(R.id.rating_editText);
        keywordEditText = findViewById(R.id.keyword_editText);

        contentEditText = findViewById(R.id.content_Edit);

    }

    private void getData() {

        title = titleEditText.getText().toString();
        author = authorEditText.getText().toString();
        rating = ratingEditText.getText().toString();
        keyword = keywordEditText.getText().toString();
        content = contentEditText.getText().toString();

        Date uploadTime = new Date(System.currentTimeMillis());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat mFormat = new SimpleDateFormat("MM/dd HH:mm:ss");

        uploadTimeText = mFormat.format(uploadTime);
    }

    private void uploadData() {
        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();

        if (auth != null) {
            userID = auth.getUid();
            email = auth.getEmail();
            if(!type_switch.isChecked()){
                Log.d("test", "1");
                dbUploadData();
            }else {
                dbUploadDataPrivate();
                Log.d("test", "2");
            }

        }
    }

    private void dbUploadData() {
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        String sc = getRandomString();
        WriteInfo data = new WriteInfo(title, author, rating, keyword, content, email, uploadTimeText, userID);

        fireStore.collection("book").document(sc).set(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "업로드 성공!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    private void dbUploadDataPrivate(){
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        String sc = getRandomString();
        WriteInfo data = new WriteInfo(title, author, rating, keyword, content, email, uploadTimeText, userID);

        fireStore.collection("book-private").document(userID).collection(email).document(sc).set(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "업로드 성공!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    private String getRandomString() {
        StringBuffer temp = new StringBuffer();
        Random rnd = new Random();
        for (int i = 0; i < 20; i++) {
            int rIndex = rnd.nextInt(3);
            switch (rIndex) {
                case 0:
                    // a-z
                    temp.append((char) ((int) (rnd.nextInt(26)) + 97));
                    break;
                case 1:
                    // A-Z
                    temp.append((char) ((int) (rnd.nextInt(26)) + 65));
                    break;
                case 2:
                    // 0-9
                    temp.append((rnd.nextInt(10)));
                    break;
            }
        }

        return temp.toString();
    }
}