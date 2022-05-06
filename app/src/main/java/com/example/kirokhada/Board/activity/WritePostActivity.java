package com.example.kirokhada.Board.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kirokhada.R;
import com.example.kirokhada.Board.data.UserInfo;
import com.example.kirokhada.Board.data.WriteInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class WritePostActivity extends AppCompatActivity {
    private static final String Tag = "WritePostActivity";

    String itemRandomString;

    TextView titleTextView;
    TextView authorTextView;
    TextView ratingTextView;
    TextView keywordTextView;

    EditText titleEditText;
    EditText authorEditText;
    EditText ratingEditText;
    EditText keywordEditText;
    EditText contentEditText;

    Button upload_btn;

    // EditText에서 가져온 데이터
    String title, author, rating, keyword, content = null;

    // 업로드 시간을 체크하는 데이터
    String uploadTimeText = "";

    String name, userProfileUrl = "";

    private FirebaseUser user;
    private FirebaseFirestore fireStore;
    private FirebaseUser auth;

    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = firebaseDatabase.getReference("users");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solo);

        init();
        setting();
        getUserProfile();
        getRandomString();

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

        View titleLayout = findViewById(R.id.title_Layer);
        View timeLayout = findViewById(R.id.time_Layer);
        View placeLayout = findViewById(R.id.place_Layer);
        View memberLayout = findViewById(R.id.memberCount_Layer);

        titleTextView = titleLayout.findViewById(R.id.textView_Bord);
        authorTextView = timeLayout.findViewById(R.id.textView_Bord);
        ratingTextView = placeLayout.findViewById(R.id.textView_Bord);
        keywordTextView = memberLayout.findViewById(R.id.textView_Bord);

        titleEditText = titleLayout.findViewById(R.id.editText_Bord);
        authorEditText = timeLayout.findViewById(R.id.editText_Bord);
        ratingEditText = placeLayout.findViewById(R.id.editText_Bord);
        keywordEditText = memberLayout.findViewById(R.id.editText_Bord);
        contentEditText = findViewById(R.id.content_Edit);

    }


    private void setting() {
        titleTextView.setText("책 제목 :");
        authorTextView.setText("작가 :");
        ratingTextView.setText("평점 :");
        keywordTextView.setText("키워드 :");
    }

    private void getData() {

        title = titleEditText.getText().toString();
        author = authorEditText.getText().toString();
        rating = ratingEditText.getText().toString();
        keywordEditText.getText().toString();
        content = contentEditText.getText().toString();

        Date uploadTime = new Date(System.currentTimeMillis());

        SimpleDateFormat mFormat = new SimpleDateFormat("MM/dd HH:mm:ss");

        uploadTimeText = mFormat.format(uploadTime);
    }

    private void uploadData() {
        fireStore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance().getCurrentUser();
        String email = null;
        if (auth != null) {
            email = auth.getEmail();
        }

        String sc = itemRandomString;

        Log.d("asd", sc);


        WriteInfo data = new WriteInfo(title, author, rating, keyword, content, email, uploadTimeText, sc, name, userProfileUrl);

        fireStore.collection("solo_runch").document(sc).set(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "업로드 성공!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    private void getUserProfile() {

        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        String uid = auth.getUid();


        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserInfo userInfo = snapshot.getValue(UserInfo.class);

                Log.d("userInfo", userInfo.toString());
                name = userInfo.getUser_Name();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.getData() != null) {
                    userProfileUrl = (String) document.getData().get("photoUrl");
                }
            } else {
                Log.d("tag", "get failed with ", task.getException());
            }
        });

    }

    private void getRandomString() {
        StringBuffer temp = new StringBuffer();
        Random rnd = new Random();
        for (int i = 0; i < 20; i++) {
            int rIndex = rnd.nextInt(3);
            switch (rIndex) {
                case 0:
                    // a-z
                    temp.append((char) (rnd.nextInt(26) + 97));
                    break;
                case 1:
                    // A-Z
                    temp.append((char) (rnd.nextInt(26) + 65));
                    break;
                case 2:
                    // 0-9
                    temp.append((rnd.nextInt(10)));
                    break;
            }
        }

        itemRandomString = temp.toString();
    }
}

