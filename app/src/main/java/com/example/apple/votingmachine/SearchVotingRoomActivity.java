package com.example.apple.votingmachine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SearchVotingRoomActivity extends AppCompatActivity {


    private RecyclerView mSearchedUser;
    private EditText mSearchBar;
    private Button mButton;
    private String mRoomID;
    private DatabaseReference mRoomDatabase;
    private Query searchedRoom;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_voter);
        mSearchedUser = (RecyclerView)findViewById(R.id.user_list);
        mSearchedUser.setLayoutManager(new LinearLayoutManager(SearchVotingRoomActivity.this));
        mRoomDatabase = FirebaseDatabase.getInstance().getReference().child("votingRoom");
        mSearchBar = findViewById(R.id.searchbar);
        mSearchBar.setHint("Room ID");
        mButton = findViewById(R.id.searchbutton);
        mSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mRoomID = charSequence.toString();
                    mButton.setEnabled(true);
                } else {
                    mButton.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //searchedRoom = mRoomDatabase.child(mRoomID).getParent();
                searchedRoom  = mRoomDatabase.orderByChild("hashCode").equalTo(mRoomID);
                searchedRoom.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        System.out.println(dataSnapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                FirebaseRecyclerAdapter<VotingRoom, SearchVotingRoomActivity.AddVotingViewHolder> UserRecycleAdapter=new FirebaseRecyclerAdapter<VotingRoom, SearchVotingRoomActivity.AddVotingViewHolder>(
                        VotingRoom.class,
                        R.layout.recycle_list_single_voting,
                        SearchVotingRoomActivity.AddVotingViewHolder.class,
                        searchedRoom
                ) {
                    @Override
                    protected void populateViewHolder(final SearchVotingRoomActivity.AddVotingViewHolder addVoterViewHolder,
                                                      final VotingRoom votingRoom, int position) {
                        System.out.println("the room name is" + votingRoom.getRoomName());
                        addVoterViewHolder.setName(votingRoom.getRoomName());
                        addVoterViewHolder.setRoomID(votingRoom.getHashCode());
                        final String room_id=getRef(position).getKey();
                        addVoterViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent votingRoomDetal = new Intent(SearchVotingRoomActivity.this, VotingRoomActivity.class);
                                votingRoomDetal.putExtra("room_id", room_id);
                                votingRoomDetal.setAction("FromSearchVotingRoomActivity");
                                startActivity(votingRoomDetal);
                            }
                        });
                    }
                };
                mSearchedUser.setAdapter(UserRecycleAdapter);
            }
        });
    }

    public void onStart() {
        super.onStart();

        //---FETCHING DATABASE FROM mFriendDatabase USING Friends.class AND ADDING TO RECYCLERVIEW----
    }

    public static class AddVotingViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public AddVotingViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setName(String name){

            TextView userName = (TextView) mView.findViewById(R.id.roomname);
            userName.setText(name);

        }

        public void setRoomID(String roomID){

            TextView RoomID = (TextView) mView.findViewById(R.id.roomid);
            RoomID.setText(roomID);

        }

    }
}
