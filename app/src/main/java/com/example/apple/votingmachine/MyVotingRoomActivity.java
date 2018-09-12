package com.example.apple.votingmachine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyVotingRoomActivity extends AppCompatActivity{

    private TextView mHashCode;
    private TextView mRoomName;
    private Button mInviteUser;
    private Button mEdit;
    private DatabaseReference mVoRoomRef;
    private DatabaseReference mVoter;
    private DatabaseReference mRoom;
    private ValueEventListener mVoterListener;
    private FirebaseAuth mAuth;
    private RecyclerView mVoterlist;
    private String room_id;
    private MyVotingRoomAdapter voterListAdapter;
    private ArrayList<VotingRoom> voters = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private Boolean joinApplication = false;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_room_list);

        mAuth = FirebaseAuth.getInstance();
        //mVoRoomRef = FirebaseDatabase.getInstance().getReference().child("votingRoom");
        try {
            mRoom = FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getCurrentUser().getUid()).child("VotingRoom");
        } catch (Exception e) {
            e.printStackTrace();
        }
        mVoterlist = (RecyclerView) findViewById(R.id.room_list);
        voterListAdapter = new MyVotingRoomAdapter(voters,this);
        layoutManager = new LinearLayoutManager(MyVotingRoomActivity.this);
        mVoterlist.setLayoutManager(layoutManager);
        mVoterlist.setAdapter(voterListAdapter);
        //room_id = getIntent().getStringExtra("room_id");

    }

    @Override
    public void onResume(){
        super.onResume();
        //mVoter = mVoRoomRef.child(room_id).child("Voter");
        mVoterListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    //Toast.makeText(VotingRoomActivity.this,child.getKey(),Toast.LENGTH_LONG).show();
                    System.out.println("66666666666666666666666666666" + mAuth.getCurrentUser().getUid()+ child.getKey().toString());
                    FirebaseDatabase.getInstance().getReference().child("votingRoom").child(child.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            System.out.println("55555555555555555555555555555555555555");
                            VotingRoom votingRoom = dataSnapshot.getValue(VotingRoom.class);
                            voters.add(votingRoom);
                            voterListAdapter.notifyDataSetChanged();
                            layoutManager.scrollToPosition(voters.size() - 1);
                            System.out.println("3333333333333" + votingRoom.toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mRoom.addListenerForSingleValueEvent(mVoterListener);
    }

    public void onPause(){
        super.onPause();
        voters.clear();
    }
}
