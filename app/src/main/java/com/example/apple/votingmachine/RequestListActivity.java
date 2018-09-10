package com.example.apple.votingmachine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RequestListActivity extends AppCompatActivity{

    private DatabaseReference mRequests;
    private RecyclerView mRequestList;
    private FirebaseAuth mAuth;

    public void onCreate(Bundle savedInstanceState){
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);
        mRequestList = (RecyclerView)findViewById(R.id.request_list);
        mRequests = FirebaseDatabase.getInstance().getReference().child("request").child(mAuth.getCurrentUser().getUid());
        mRequestList.setLayoutManager(new LinearLayoutManager(RequestListActivity.this));
    }

    public void onStart() {
        super.onStart();

        //---FETCHING DATABASE FROM mFriendDatabase USING Friends.class AND ADDING TO RECYCLERVIEW----
        FirebaseRecyclerAdapter<Request,RequestViewHolder> RequestRecycleAdapter=new FirebaseRecyclerAdapter<Request, RequestViewHolder>(
                Request.class,
                R.layout.recycle_list_single_request,
                RequestViewHolder.class,
                mRequests
        ) {
            @Override
            protected void populateViewHolder(final RequestViewHolder requestViewHolder,
                                              final Request request, int position) {
                requestViewHolder.setRoomName(request.getRoom_name());
                requestViewHolder.setName(request.getSender_name());
                final String room_id=getRef(position).getKey();
                requestViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(RequestListActivity.this, "the view is clicked", Toast.LENGTH_LONG).show();
                        Intent requestDetail = new Intent(RequestListActivity.this, RequestActivty.class);
                        requestDetail.putExtra("room_id",room_id);
                        startActivity(requestDetail);

                    }
                });
            }
        };
        mRequestList.setAdapter(RequestRecycleAdapter);
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public RequestViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setRoomName(String roomName){

            TextView RoomNmae = (TextView) mView.findViewById(R.id.roomname);
            RoomNmae.setText(roomName);

        }

        public void setName(String name){

            TextView senderName = (TextView) mView.findViewById(R.id.sendername);
            senderName.setText(name);

        }

    }

}
