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
    private TextView mRoomName;
    private TextView mDueDate;
    private Button mInviteUser;
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
    private Boolean joinApplication = false;
    private String voteTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_room);
        mHashCode = (TextView) findViewById(R.id.hashCode);
        mRoomName = (TextView) findViewById(R.id.room_name);
        mDueDate = (TextView) findViewById(R.id.due_date);
        mInviteUser = (Button) findViewById(R.id.invite);
        mAuth = FirebaseAuth.getInstance();
        mVoRoomRef = FirebaseDatabase.getInstance().getReference().child("votingRoom");
        mRoom = FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getCurrentUser().getUid()).child("VotingRoom");
        mVoterlist = (RecyclerView) findViewById(R.id.voterlist);
        if(getIntent().getAction()!= null && getIntent().getAction().equals("FromSearchVotingRoomActivity")){
            mInviteUser.setText("join");
            joinApplication = true;
        }
        /*
        else {
            mInviteUser.setEnabled(false);
            mInviteUser.setVisibility(View.INVISIBLE);
        }*/
        //get the detail from firebase
        room_id = getIntent().getStringExtra("room_id");
        mVoRoomRef.child(room_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                VotingRoom votingRoom = dataSnapshot.getValue(VotingRoom.class);
                mHashCode.setText(votingRoom.getHashCode());
                mRoomName.setText(votingRoom.getRoomName());
                mDueDate.setText("Due Date: " + votingRoom.getDueDate());
                voteTicket = votingRoom.getVoteTicket();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        layoutManager = new LinearLayoutManager(VotingRoomActivity.this);
        voterListAdapter = new VoterListAdapter(voters,this, room_id);
        mVoterlist.setLayoutManager(layoutManager);
        mVoterlist.setAdapter(voterListAdapter);
        /*mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Edit = new Intent(VotingRoomActivity.this, VotingRoomEditorActivity.class);
                Edit.putExtra("room_id", room_id);
                startActivity(Edit);
            }
        });*/
            mInviteUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!joinApplication) {
                        Intent addNewVoter = new Intent(VotingRoomActivity.this, AddVoterActivity.class);
                        addNewVoter.putExtra("room_id", mHashCode.getText().toString());
                        startActivity(addNewVoter);
                    }
                    else{
                        //add room to applier room list
                        mRoom.child(room_id).setValue(true);
                        //add applier to room's voter list
                        mVoRoomRef.child(room_id).child("Voter").child(mAuth.getCurrentUser().getUid()).setValue(true);
                        Intent restart = new Intent(VotingRoomActivity.this, VotingRoomActivity.class);
                        restart.putExtra("room_id", room_id);
                        startActivity(restart);
                        finish();
                    }
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

    public String getRoom_id(){
        return room_id;
    }

    public void onPause(){
        super.onPause();
        voters.clear();
    }
}
