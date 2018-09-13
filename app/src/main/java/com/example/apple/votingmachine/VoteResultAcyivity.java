package com.example.apple.votingmachine;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VoteResultAcyivity extends AppCompatActivity{

    private RecyclerView mResultlist;
    private String room_id;
    private LinearLayoutManager layoutManager;
    private DatabaseReference mRoomPath;
    private DatabaseReference mUserPath;
    private ProgressDialog mProgressDialog;
    private ArrayList<VoteResult> voteResults = new ArrayList<>();
    private Handler handler = new Handler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_room_list);
        //get the room_id from "VotingRoomActivity"
        room_id = getIntent().getStringExtra("room_id");
        mResultlist = (RecyclerView) findViewById(R.id.room_list);
        mProgressDialog = new ProgressDialog(this);

        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 3000);
                VoteResultAdapter voteResultAdapter = new VoteResultAdapter(VoteResultAcyivity.this, voteResults);
                layoutManager = new LinearLayoutManager(VoteResultAcyivity.this);
                mResultlist.setLayoutManager(layoutManager);
                mResultlist.setAdapter(voteResultAdapter);
                mProgressDialog.dismiss();
            }
        };
        r.run();
    }

    public void onStart(){
        super.onStart();
        mProgressDialog.setTitle("Uploading Relevent Info");
        mProgressDialog.setMessage("Please wait while we process and upload the Info...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
        mRoomPath = FirebaseDatabase.getInstance().getReference().child("votingRoom").child(room_id).child("Voter");
        mRoomPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //every member in the room
                for(final DataSnapshot child : dataSnapshot.getChildren()){
                    final VoteResult voteResult = new VoteResult();
                    System.out.println("VOTED USER KEY: " + child.getKey());
                    mUserPath = FirebaseDatabase.getInstance().getReference().child("user").child(child.getKey());
                    mUserPath.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            System.out.println("VOTED USER NAME: " + user.getName());
                            UpdateUI(voteResult, user);
                            for(DataSnapshot grandChild : child.getChildren()){
                                System.out.println("grandChild: " + grandChild.getKey().toString());
                                if(grandChild.child("voteUp").exists()){
                                    System.out.println("There is a vote up!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                                    voteResult.VoteUp();
                                }
                                if(grandChild.child("commentUp").exists()){
                                    System.out.println("There is a comment up"+grandChild.child("commentUp").getValue().toString()+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                                    voteResult.addComments("good: " + grandChild.child("commentUp").getValue().toString());
                                }
                                if(grandChild.child("voteDown").exists()){
                                    System.out.println("There is a vote down!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                                    voteResult.VoteDown();
                                }
                                if(grandChild.child("commentDown").exists()){
                                    System.out.println("There is a comment down"+ grandChild.child("commentDown").getValue().toString()+ "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                                    voteResult.addComments("bad: " + grandChild.child("commentDown").getValue().toString());
                                }
                            }
                            voteResults.add(voteResult);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    //the result of each member
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void UpdateUI(VoteResult voteResult, User user){
        voteResult.setName(user.getName());
        voteResult.setStatus(user.getDescription());
        //Picasso.with(VoteResultAcyivity.this).load(user.getThumb_image()).placeholder(R.drawable.default_user_img).into(holder.circleImageView);
        voteResult.setThumb_image(user.getThumb_image());
        /*
        holder.voteUpView.setText(holder.voteUp + " Thumb Ups");
        holder.voteDownView.setText(holder.voteUp + " Thumb Downs");
        String goodcomments = "GoodComment: ";
        for(String comment: holder.goodComments){
            goodcomments += comment;
            goodcomments += "/n";
        }
        String badcomments = "BadComment: ";
        for(String comment: holder.badComments){
            badcomments += comment;
            badcomments += "/n";
        }
        String allcomments = goodcomments + badcomments;
        holder.commentView.setText(allcomments);*/
    }

}
