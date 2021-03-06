package com.example.kirokhada.Board.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.kirokhada.LoadingActivity;
import com.example.kirokhada.LoginActivity;
import com.example.kirokhada.ManagementData;
import com.example.kirokhada.R;
import com.example.kirokhada.Board.activity.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private FirebaseAuth firebaseAuth;
    private View root;
    Button write_btn;
    private ImageView profileImageView;
    private TextView  nameTextView;
    private TextView  ageTextView;
    private TextView genreTextView;
    private TextView  introTextView;
    private TextView  genderTextView;
    private Context context;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profile, container, false);
        initialize();

        write_btn=root.findViewById(R.id.writeButton);
        profileImageView = root.findViewById(R.id.profileImageView);
        nameTextView = root.findViewById(R.id.nameTextView);
        ageTextView = root.findViewById(R.id.ageTextView);
        genreTextView = root.findViewById(R.id.genreTextView);
        introTextView = root.findViewById(R.id.profile_intro_TextView);
        genderTextView=root.findViewById(R.id.genderTextView);
        write_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    private void initialize() {
        Button btnLogOut, btnSignOut;

        btnLogOut = root.findViewById(R.id.btnLogOut);
        btnSignOut = root.findViewById(R.id.btnSignOut);    //btnSignOut??? ???????????????.

        firebaseAuth = FirebaseAuth.getInstance();

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
                Intent intent = new Intent(
                        getContext(), LoginActivity.class);

                // ????????? ????????? ??? ??????
                ManagementData.getInstance().delAllData();

                startActivity(intent);

            }
        });


        //??????????????? ?????? ??????
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick_signOut();
            }
        });
    }

    //???????????? ?????????
    public void onClick_signOut(){
        new AlertDialog.Builder(getContext())
                .setTitle("?????? ??????")
                .setMessage("????????? ?????? ???????????????????")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // ????????? ?????? ??????
                        Intent intent = new Intent(getContext(), LoadingActivity.class);
                        // ????????? ????????? ??? ??????
                        ManagementData.getInstance().delAllData();

                        startActivity(intent);

                        signOut();

                        Toast.makeText(getActivity(),"????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                    }})
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // ????????? ?????? ??????
                        Toast.makeText(getActivity(), "?????????????????????.", Toast.LENGTH_SHORT).show();
                    }})
                .show();
    }

    private void loadData() {

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            if(document.getData().get("photoUrl") != null){
                                Glide.with(getActivity()).load(document.getData().get("photoUrl")).centerCrop().override(500).into(profileImageView);
                            }
                            nameTextView.setText(document.getData().get("name").toString());
                            ageTextView.setText(document.getData().get("age").toString());
                            genreTextView.setText(document.getData().get("genre").toString());
                            introTextView.setText(document.getData().get("intro").toString());
                            genderTextView.setText(document.getData().get("gender").toString());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void logOut() {
        firebaseAuth.getInstance().signOut();
    }

    //?????? ????????????
    private void signOut() {
        firebaseAuth.getCurrentUser().delete(); }


    @Override
    public void onResume() {
        super.onResume();
        // TODO: onResume??? ????????? ?????? ????????? ???????????????. ?????? ?????? ????????? onCreateView ???????????? ???????????????.
        loadData();
    }
}
