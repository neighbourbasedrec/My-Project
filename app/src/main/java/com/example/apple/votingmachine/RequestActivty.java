package com.example.apple.votingmachine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RequestActivty extends AppCompatActivity {

    private TextView senderName;
    private TextView roomName;
    private Button accept;
    private Button decline;
    private DatabaseReference mMyRequest;
    private DatabaseReference mVotingRoom;
    private DatabaseReference mMyRoomList;
    private FirebaseAuth mAuth;
    private String room_id;

    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        mAuth = FirebaseAuth.getInstance();

        senderName = (TextView) findViewById(R.id.sendername);
        roomName = (TextView)findViewById(R.id.roomname);
        accept = (Button)findViewById(R.id.accept);
        decline = (Button)findViewById(R.id.decline);

        Intent getIntent = getIntent();
        room_id = getIntent.getExtras().getString("room_id");

        mMyRequest = FirebaseDatabase.getInstance().getReference().child("request").child(mAuth.getCurrentUser().getUid()).child(room_id);
        mVotingRoom = FirebaseDatabase.getInstance().getReference().child("votingRoom").child(room_id);
        mMyRoomList = FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getCurrentUser().getUid()).child("VotingRoom");

        final ValueEventListener MyRequestListender = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Toast.makeText(RequestActivty.this, "%%%%%%%%%%%%%%%%%%%%%%%%%", Toast.LENGTH_LONG).show();
                Request myRequest = dataSnapshot.getValue(Request.class);
                String aaa = dataSnapshot.getValue().toString();

                System.out.println(aaa + "666666666666666");

                roomName.setText(myRequest.getRoom_name());
                senderName.setText(myRequest.getSender_name());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mMyRequest.addValueEventListener(MyRequestListender);


        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mMyRequest.setValue(null);
                //mVotingRoom.child("Voter").push().setValue(mAuth.getCurrentUser().getUid());
                mVotingRoom.child("Voter").child(mAuth.getCurrentUser().getUid()).setValue(true);
                mMyRequest.removeEventListener(MyRequestListender);
                mMyRoomList.child(room_id).setValue(true);
                finish();
                mMyRequest.setValue(null);
            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMyRequest.removeEventListener(MyRequestListender);
                finish();
                mMyRequest.setValue(null);
            }
        });
    }



}
