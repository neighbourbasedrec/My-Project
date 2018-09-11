package com.example.apple.votingmachine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class AddVoterActivity extends AppCompatActivity {

    private RecyclerView mSearchedUser;
    private EditText mSearchBar;
    private Button mButton;
    private String mUserName;
    private DatabaseReference mUserDatabase;
    private Query searchedUsers;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_voter);
        Intent getIntent = getIntent();
        final String roomID = getIntent.getExtras().getString("room_id");
        if(roomID == null){finish();}
        mSearchedUser = (RecyclerView)findViewById(R.id.user_list);
        mSearchedUser.setLayoutManager(new LinearLayoutManager(AddVoterActivity.this));
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("user");
        mSearchBar = findViewById(R.id.searchbar);
        mButton = findViewById(R.id.searchbutton);
        mSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mUserName = charSequence.toString();
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
                searchedUsers = mUserDatabase.orderByChild("name").equalTo(mUserName);
                FirebaseRecyclerAdapter<User, AddUserViewHolder> UserRecycleAdapter=new FirebaseRecyclerAdapter<User, AddUserViewHolder>(
                        User.class,
                        R.layout.recycle_list_single_user,
                        AddUserViewHolder.class,
                        searchedUsers
                ) {
                    @Override
                    protected void populateViewHolder(final AddVoterActivity.AddUserViewHolder addVoterViewHolder,
                                                      final User user, int position) {
                        addVoterViewHolder.setName(user.getName());
                        addVoterViewHolder.setDescreption(user.getDescription());
                        final String user_id=getRef(position).getKey();
                        addVoterViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent inviteVoter = new Intent(AddVoterActivity.this, ProfileActivity.class);
                                inviteVoter.putExtra("user_id",user_id);
                                inviteVoter.putExtra("room_id",roomID);
                                startActivity(inviteVoter);

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

    public static class AddUserViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public AddUserViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }
        public void setName(String name){

            TextView userName = (TextView) mView.findViewById(R.id.username);
            userName.setText(name);

        }

        public void setDescreption(String name){

            TextView userDescription = (TextView) mView.findViewById(R.id.selfDescription);
            userDescription.setText(name);

        }

    }
}
