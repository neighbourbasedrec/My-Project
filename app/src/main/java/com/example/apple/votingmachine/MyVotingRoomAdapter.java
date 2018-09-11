package com.example.apple.votingmachine;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyVotingRoomAdapter extends RecyclerView.Adapter<MyVotingRoomAdapter.VoterViewHolder> {
    private List<VotingRoom> mVotersList;
    Context context;

    //-----GETTING LIST OF ALL MESSAGES FROM CHAT ACTIVITY ----
    public MyVotingRoomAdapter(List<VotingRoom> mVotersList, Context context) {
        this.mVotersList = mVotersList;
        this.context = context;
    }


    //---CREATING SINGLE HOLDER AND RETURNING ITS VIEW---
    @Override
    public MyVotingRoomAdapter.VoterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_list_single_voting,parent,false);
        return new MyVotingRoomAdapter.VoterViewHolder(view);
    }

    //----RETURNING VIEW OF SINGLE HOLDER----
    public class VoterViewHolder extends RecyclerView.ViewHolder {

        public TextView userName;
        public TextView userDescription;

        public VoterViewHolder(View itemView) {
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.roomid);
            userDescription = (TextView)itemView.findViewById(R.id.roomname);

            context = itemView.getContext();

            //---DELETE FUNCTION---
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profile = new Intent(context, VotingRoomActivity.class);
                    profile.putExtra("room_id", userName.getText().toString());
                    context.startActivity(profile);
                }
            });
        }


    }

    //----SETTING EACH HOLDER WITH DATA----
    @Override
    public void onBindViewHolder(final MyVotingRoomAdapter.VoterViewHolder holder, int position) {


        // String current_user_id = mAuth.getCurrentUser().getUid();
        VotingRoom voter = mVotersList.get(position);
        String voterName = voter.getHashCode();
        String voterDescription = voter.getRoomName();

        //----CHANGING TIMESTAMP TO TIME-----
        holder.userName.setText(voterName);
        holder.userDescription.setText(voterDescription);

    }

    //---NO OF ITEMS TO BE ADDED----
    @Override
    public int getItemCount() {
        return mVotersList.size();
    }
}
