package com.example.apple.votingmachine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.twitter.sdk.android.core.internal.CurrentTimeProvider;

import java.util.ArrayList;
import java.util.List;

public class VotingRoomActivity extends AppCompatActivity {

    private TextView mHashCode;
    private EditText mRoomName;
    private Button mInviteUser;
    private Button mSave;
    private Button mCancel;
    private DatabaseReference mVoRoomRef;
    private DatabaseReference mVoter;
    private DatabaseReference mRoom;
    private ValueEventListener mVoterListener;
    private FirebaseAuth mAuth;
    private RecyclerView mVoterlist;
    private String room_id;
    private VoterListAdapter voterListAdapter;
    private ArrayList<User> voters = new ArrayList<>();
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_room);

        mHashCode = (TextView) findViewById(R.id.hashCode);
        mRoomName = (EditText) findViewById(R.id.room_name);
        mInviteUser = (Button) findViewById(R.id.invite);
        mSave = (Button) findViewById(R.id.save);
        mCancel = (Button) findViewById(R.id.cancel);
        mAuth = FirebaseAuth.getInstance();
        mVoRoomRef = FirebaseDatabase.getInstance().getReference().child("votingRoom");
        mRoom = FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getCurrentUser().getUid()).child("VotingRoom");
        mVoterlist = (RecyclerView) findViewById(R.id.voterlist);
        voterListAdapter = new VoterListAdapter(voters,this);
        layoutManager = new LinearLayoutManager(VotingRoomActivity.this);
        mVoterlist.setLayoutManager(layoutManager);
        mVoterlist.setAdapter(voterListAdapter);

        room_id = getIntent().getStringExtra("room_id");
        if(room_id != null) {
            //edit the room info
            mVoRoomRef.child(room_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    VotingRoom votingRoom = dataSnapshot.getValue(VotingRoom.class);
                    mHashCode.setText(votingRoom.getHashCode());
                    mRoomName.setText(votingRoom.getRoomName());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }

        //set up a new hashCode();
        else{
            String hashcode = hashCode() + "";
            room_id = hashcode.substring(0, 7);
            mHashCode.setText(room_id);
        }

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hashCode = mHashCode.getText().toString();
                String roomName = mRoomName.getText().toString();
                if(roomName.length()>0) {
                    VotingRoom voteRoom = VotingRoom.newVotingRoom(hashCode, roomName);
                    mVoRoomRef.child(hashCode).setValue(voteRoom);
                    mVoRoomRef.child(hashCode).child("Voter").child(mAuth.getCurrentUser().getUid()).setValue(true);
                    mRoom.child(hashCode).setValue(true);
                    Toast.makeText(VotingRoomActivity.this, "successfully created a voting room", Toast.LENGTH_LONG).show();
                    onResume();
                }
                else{
                    Toast.makeText(VotingRoomActivity.this, "please fill in the room name", Toast.LENGTH_LONG).show();
                }
            }
        });

        mInviteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewVoter = new Intent(VotingRoomActivity.this, AddVoterActivity.class);
                addNewVoter.putExtra("room_id", mHashCode.getText().toString());
                startActivity(addNewVoter);
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        mVoter = mVoRoomRef.child(room_id).child("Voter");
        mVoterListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    //Toast.makeText(VotingRoomActivity.this,child.getKey(),Toast.LENGTH_LONG).show();
                    //System.out.println("66666666666666666666666666666" + child.getKey().toString());
                    FirebaseDatabase.getInstance().getReference().child("user").child(child.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User voter = dataSnapshot.getValue(User.class);
                            voters.add(voter);
                            voterListAdapter.notifyDataSetChanged();
                            layoutManager.scrollToPosition(voters.size() - 1);
                            System.out.println("3333333333333" + voter.getName());
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
        mVoter.addListenerForSingleValueEvent(mVoterListener);
    }

    public void onPause(){
        super.onPause();
        voters.clear();
    }
}
