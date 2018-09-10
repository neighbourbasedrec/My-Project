package com.example.apple.votingmachine;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

public class VotingRoomListActivity extends AppCompatActivity {

    private DatabaseReference mVotingRooms;
    private RecyclerView mVotingRoomList;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_room_list);
        mVotingRoomList = (RecyclerView)findViewById(R.id.room_list);
        mVotingRooms = FirebaseDatabase.getInstance().getReference().child("votingRoom");
        mVotingRoomList.setLayoutManager(new LinearLayoutManager(VotingRoomListActivity.this));
    }

    public void onStart() {
        super.onStart();

        //---FETCHING DATABASE FROM mFriendDatabase USING Friends.class AND ADDING TO RECYCLERVIEW----
        FirebaseRecyclerAdapter<VotingRoom,VotingRoomViewHolder> RecycleAdapter=new FirebaseRecyclerAdapter<VotingRoom, VotingRoomViewHolder>(
                VotingRoom.class,
                R.layout.recycle_list_single_voting,
                VotingRoomViewHolder.class,
                mVotingRooms
        ) {
            @Override
            protected void populateViewHolder(final VotingRoomViewHolder votingRoomViewHolder,
                                              final VotingRoom votingRoom, int position) {
                votingRoomViewHolder.setRoomId(votingRoom.getHashCode());
                votingRoomViewHolder.setName(votingRoom.getRoomName());
                final String room_id=getRef(position).getKey();
                votingRoomViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                                Toast.makeText(VotingRoomListActivity.this, "the view is clicked", Toast.LENGTH_LONG).show();
                                Intent votingRoomDetail = new Intent(VotingRoomListActivity.this, VotingRoomActivity.class);
                                votingRoomDetail.putExtra("room_id",room_id);
                                startActivity(votingRoomDetail);
                            }
                        });
                    }
                };
        mVotingRoomList.setAdapter(RecycleAdapter);
    }

    public static class VotingRoomViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public VotingRoomViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }
        public void setRoomId(String id){

            TextView roomid = (TextView) mView.findViewById(R.id.roomid);
            roomid.setText(id);

        }
        public void setName(String name){

            TextView roomName = (TextView) mView.findViewById(R.id.roomname);
            roomName.setText(name);

        }

    }

}
