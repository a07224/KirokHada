package com.example.kirokhada;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kirokhada.Board.activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignUpActivity";

    public EditText emailId, passwd, repasswd;
    private Button join_btn;

    // 추가 코드
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailId = findViewById(R.id.emailEditText);
        passwd = findViewById(R.id.passwordEditText);
        repasswd = findViewById(R.id.rePasswordEditText);
        join_btn = findViewById(R.id.join_btn);


        firebaseAuth = FirebaseAuth.getInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        join_btn.setOnClickListener(this);
    }

    //    토스트메세지를 보내는 역할이죠
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        String emailID = emailId.getText().toString();
        String paswd = passwd.getText().toString();
        String repaswd = repasswd.getText().toString();

        if (paswd.equals(repaswd)) {
            if (!emailID.equals("") && !paswd.equals("") && !repaswd.equals("")) {

                firebaseAuth.createUserWithEmailAndPassword(emailID, paswd)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    final FirebaseUser user;
                                    UserProfileChangeRequest profileUpdate;

                                    profileUpdate = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(emailID)
                                            .build();

                                    user = firebaseAuth.getCurrentUser();
                                    user.updateProfile(profileUpdate)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        ManagementData.registerUser(user);
                                                    }
                                                }
                                            });

                                    //myRef.child(Constant.DB_CHILD_USER).child(userID).setValue(userInfor);
                                    toastMessage("회원가입이 완료되었습니다.");

                                    emailId.setText("");
                                    passwd.setText("");
                                    repasswd.setText("");

                                    finish();
                                    onBackPressed();

                                }
                            }
                        });
            } else if (emailID.isEmpty()) {
                emailId.setError("E-Mail을 입력해주세요!");
                emailId.requestFocus();

            } else if (paswd.isEmpty()) {
                passwd.setError("Password를 입력해주세요!");
                passwd.requestFocus();

            } else if (!(emailID.isEmpty() && paswd.isEmpty())) {
                firebaseAuth.createUserWithEmailAndPassword(emailID, paswd).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this.getApplicationContext(),
                                    "SignUp unsuccessful: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        }
                    }
                });
            } else {
                Toast.makeText(SignUpActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }

        } else {
            toastMessage("비밀번호가 서로 일치하지 않습니다.");
            passwd.setText("");
            repasswd.setText("");
        }
    }
}