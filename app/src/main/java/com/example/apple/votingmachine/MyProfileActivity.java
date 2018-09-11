package com.example.apple.votingmachine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyProfileActivity extends AppCompatActivity {

    private TextView name;
    private TextView email;
    private TextView description;
    private Button inviteButton;
    private Button voteUp;
    private Button voteDown;
    private EditText commentEdit;
    private DatabaseReference mUserRef;
    private DatabaseReference mRoomRef;
    private FirebaseAuth mAuth;
    private String receiver_name;
    private String sender_name;
    private String room_name;
    private Request request;
    private Boolean invite = false;
    private String user_id;
    private String room_id;
    private DatabaseReference voterPath;
    private Boolean IsVoteUp = true;
    private Boolean IsVoteDown = true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = (TextView) findViewById(R.id.user_name);
        email = (TextView) findViewById(R.id.email);
        description = (TextView) findViewById(R.id.self);
        inviteButton = (Button) findViewById(R.id.invite);
        voteUp = (Button) findViewById(R.id.voteUp);
        voteDown = (Button) findViewById(R.id.voteDown);
        commentEdit = (EditText) findViewById(R.id.comment);
        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("user");
        mRoomRef = FirebaseDatabase.getInstance().getReference().child("votingRoom");


        mUserRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                fillInUI(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void fillInUI(User user){
        name.setText(user.getName());
        email.setText(user.getEmail());
        description.setText(user.getDescription());
    }

}
