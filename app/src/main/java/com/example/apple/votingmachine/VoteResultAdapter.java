package com.example.apple.votingmachine;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VoteResultAdapter extends RecyclerView.Adapter<VoteResultAdapter.VoterViewHolder> {
    private List<VoteResult> mResultsList;
    Context context;
    //private String room_id;
    //private DatabaseReference mRoomPath;
    //private DatabaseReference mUserPath;

    //-----GETTING LIST OF ALL MESSAGES FROM CHAT ACTIVITY ----
    public VoteResultAdapter(Context context, ArrayList<VoteResult> mResultsList) {
        this.context = context;
        this.mResultsList = mResultsList;
    }


    //---CREATING SINGLE HOLDER AND RETURNING ITS VIEW---
    @Override
    public VoterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_list_single_result,parent,false);
        return new VoteResultAdapter.VoterViewHolder(view);

    }

    //----RETURNING VIEW OF SINGLE HOLDER----
    public class VoterViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView circleImageView;
        public TextView name;
        public TextView status;
        public TextView voteUpView;
        public TextView voteDownView;
        public TextView commentView;
        public String userId;
        public int voteUp = 0;
        public int voteDown = 0;
        public ArrayList<String> goodComments;
        public ArrayList<String> badComments;

        public VoterViewHolder(View itemView) {
            super(itemView);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.circleImageView);
            name = (TextView) itemView.findViewById(R.id.name);
            status = (TextView)itemView.findViewById(R.id.status);
            voteUpView = (TextView)itemView.findViewById(R.id.voteUp);
            voteDownView = (TextView)itemView.findViewById(R.id.voteDown);
            commentView = (TextView)itemView.findViewById(R.id.viewcomment);
            context = itemView.getContext();
            commentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

    //----SETTING EACH HOLDER WITH DATA----
    @Override
    public void onBindViewHolder(final VoteResultAdapter.VoterViewHolder holder, int position) {
        // String current_user_id = mAuth.getCurrentUser().getUid();
        VoteResult voteResult = mResultsList.get(position);
        holder.name.setText(voteResult.getName());
        holder.status.setText(voteResult.getStatus());
        Picasso.with(context).load(voteResult.getThumb_image()).placeholder(R.drawable.default_user_img).into(holder.circleImageView);
        holder.voteUpView.setText(voteResult.getVoteUp() + " Thumb Ups");
        holder.voteDownView.setText(voteResult.getVoteDown() + " Thumb Downs");
        holder.commentView.setText(voteResult.getComments());
    }

    /*public void UpdateUI(final VoteResultAdapter.VoterViewHolder holder, User user){
        holder.name.setText(user.getName());
        holder.status.setText(user.getDescription());
        Picasso.with(context).load(user.getThumb_image()).placeholder(R.drawable.default_user_img).into(holder.circleImageView);
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
        holder.commentView.setText(allcomments);
    }*/

    //---NO OF ITEMS TO BE ADDED----
    @Override
    public int getItemCount() {
        return mResultsList.size();
    }

}
