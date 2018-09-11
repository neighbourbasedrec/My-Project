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

public class ProfileActivity extends AppCompatActivity {

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
        //inviteButton.setEnabled(false);
        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("user");
        mRoomRef = FirebaseDatabase.getInstance().getReference().child("votingRoom");

        Intent getIntent = getIntent();
        //the profile is opened from the invitation to make a request then the room_id can not be null
        if(getIntent.getAction()!= null && getIntent.getAction().equals(VoterListAdapter.class.toString())){
            System.out.println("openfromVotingRoomList!!!!!!!!!!!!!!!!!!!!!!!!!!");
            Toast.makeText(ProfileActivity.this,"openfromVotingRoomList",Toast.LENGTH_LONG).show();
            user_id = getIntent.getExtras().getString("user_id");
            room_id = getIntent.getExtras().getString("room_id");
            voterPath = mRoomRef.child(room_id).child("Voter").child(user_id).child(mAuth.getCurrentUser().getUid());
            //can not vote up/vote down a user twice
            voterPath.child("voteUp").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        voteUp.setText("Cancel Vote Up");
                        IsVoteUp = false;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            voterPath.child("voteDown").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        voteDown.setText("Cancel Vote Down");
                        IsVoteDown = false;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            inviteButton.setVisibility(View.INVISIBLE);
        }

        else{
            user_id = getIntent.getExtras().getString("user_id");
            room_id = getIntent.getExtras().getString("room_id");
            if(room_id != null){
                invite = true;
            }
            voteUp.setEnabled(false);
            voteDown.setEnabled(false);
            voteUp.setVisibility(View.INVISIBLE);
            voteDown.setVisibility(View.INVISIBLE);
            commentEdit.setVisibility(View.INVISIBLE);
        }
        //final Request request = Request.newRequest(room_id, user_id, mAuth.getCurrentUser().getUid());


        mUserRef.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                fillInUI(user);
                if(invite) {
                    mRoomRef.child(room_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            VotingRoom voteRoom = dataSnapshot.getValue(VotingRoom.class);
                            room_name = voteRoom.getRoomName();
                            Toast.makeText(ProfileActivity.this, "the room name is " + room_name, Toast.LENGTH_LONG).show();
                            try {
                                sender_name = SharedPreference.getUserName(ProfileActivity.this);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(ProfileActivity.this, "sender name is" + sender_name, Toast.LENGTH_LONG).show();
                            request = Request.newRequest(room_name, receiver_name, sender_name);
                            inviteButton.setEnabled(true);
                            inviteButton.setVisibility(View.VISIBLE);
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
        });

        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProfileActivity.this, "you have sent an invitation", Toast.LENGTH_LONG).show();
                FirebaseDatabase.getInstance().getReference().child("request").child(user_id).child(room_id).setValue(request);
            }
        });

        voteUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(IsVoteUp) {
                    voterPath.child("voteUp").setValue("up");
                    voterPath.child("commentUp").setValue(commentEdit.getText().toString());
                    //voteUp.setText("Cancel Vote Up");
                    IsVoteUp = false;
                }
                else{
                    voterPath.child("voteUp").setValue(null);
                    voterPath.child("commentUp").setValue(null);
                    voteUp.setText("Vote Up");
                    IsVoteUp = true;

                }
            }
        });

        voteDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(IsVoteDown) {
                    voterPath.child("voteDown").setValue("down");
                    voterPath.child("commentDown").setValue(commentEdit.getText().toString());
                    //voteDown.setText("Cancel Vote Down");
                    IsVoteDown = false;
                }
                else{
                    voterPath.child("voteDown").setValue(null);
                    voterPath.child("commentDown").setValue(null);
                    voteDown.setText("Vote Down");
                    IsVoteDown = true;
                }
            }
        });
    }

    /*public void decreasVoteTimes(){
        mRoomRef.child(room_id).child("Voter").child(mAuth.getCurrentUser().getUid()).child("voteTimes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String voteTimes = dataSnapshot.getValue().toString();
                int votetime = Integer.parseInt(voteTimes);
                mRoomRef.child(room_id).child("Voter").child(mAuth.getCurrentUser().getUid()).child("voteTimes").setValue(""+(votetime-1));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

    public void fillInUI(User user){
        receiver_name = user.getName();
        name.setText(receiver_name);
        email.setText(user.getEmail());
        description.setText(user.getDescription());
    }

}
