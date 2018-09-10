package com.example.apple.votingmachine;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


public class VoterListAdapter extends RecyclerView.Adapter<VoterListAdapter.VoterViewHolder>{

    private List<User> mVotersList;
    Context context;

    //-----GETTING LIST OF ALL MESSAGES FROM CHAT ACTIVITY ----
    public VoterListAdapter(List<User> mVotersList, Context context) {
        this.mVotersList = mVotersList;
        this.context = context;
    }


    //---CREATING SINGLE HOLDER AND RETURNING ITS VIEW---
    @Override
    public VoterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_list_single_user,parent,false);
        return new VoterViewHolder(view);
    }

    //----RETURNING VIEW OF SINGLE HOLDER----
    public class VoterViewHolder extends RecyclerView.ViewHolder {

        public TextView userName;
        public TextView userDescription;
        public String userId;


        public VoterViewHolder(View itemView) {
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.username);
            userDescription = (TextView)itemView.findViewById(R.id.selfDescription);

            context = itemView.getContext();

            //---DELETE FUNCTION---
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profile = new Intent(context, ProfileActivity.class);
                    profile.putExtra("user_id", userId);
                    context.startActivity(profile);
                }
            });
        }


    }

    //----SETTING EACH HOLDER WITH DATA----
    @Override
    public void onBindViewHolder(final VoterViewHolder holder, int position) {


        // String current_user_id = mAuth.getCurrentUser().getUid();
        User voter = mVotersList.get(position);
        String voterName = voter.getName();
        String voterDescription = voter.getDescription();

        //----CHANGING TIMESTAMP TO TIME-----
        holder.userName.setText(voterName);
        holder.userDescription.setText(voterDescription);
        holder.userId = voter.getUid();

    }

    //---NO OF ITEMS TO BE ADDED----
    @Override
    public int getItemCount() {
        return mVotersList.size();
    }
}