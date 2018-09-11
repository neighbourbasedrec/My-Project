package com.example.apple.votingmachine;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class VotingRoomEditorActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private TextView mHashCode;
    private TextView mDueDate;
    private EditText mRoomName;
    //private Button mInviteUser;
    private Button mSave;
    private Button mCancel;
    private Button mDDButton;
    private NiceSpinner mVoteTickets;
    private DatabaseReference mVoRoomRef;
    private DatabaseReference mVoter;
    private DatabaseReference mRoom;
    private ValueEventListener mVoterListener;
    private FirebaseAuth mAuth;
    private String room_id;
    private VoterListAdapter voterListAdapter;
    private ArrayList<User> voters = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private DatePickerDialog dpd;

    private String dayOfMonth;
    private String MonthOfYear;
    private String Year;
    private String dueDate;
    private String voteTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_room_edit);
        mHashCode = (TextView) findViewById(R.id.hashCode);
        mDueDate = (TextView) findViewById(R.id.date);
        mDDButton = (Button) findViewById(R.id.due_date);
        mRoomName = (EditText) findViewById(R.id.room_name);
        //mInviteUser = (Button) findViewById(R.id.invite);
        //mInviteUser.setEnabled(false);
        mVoteTickets = (NiceSpinner) findViewById(R.id.nice_spinner);
        mSave = (Button) findViewById(R.id.save);
        mCancel = (Button) findViewById(R.id.cancel);
        mAuth = FirebaseAuth.getInstance();
        mVoRoomRef = FirebaseDatabase.getInstance().getReference().child("votingRoom");
        mRoom = FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getCurrentUser().getUid()).child("VotingRoom");
        final List<String> voteChioices = new LinkedList<>(Arrays.asList("3", "4", "5"));
        mVoteTickets.attachDataSource(voteChioices);
        mVoteTickets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                voteTicket = voteChioices.get(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        /*room_id = getIntent().getStringExtra("room_id");
        if(room_id != null) {
            //edit the room info
            mVoRoomRef.child(room_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    VotingRoom votingRoom = dataSnapshot.getValue(VotingRoom.class);
                    mHashCode.setText(votingRoom.getHashCode());
                    mRoomName.setText(votingRoom.getRoomName());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }*/

        //create a new room;
        //else{
        String hashcode = hashCode() + "";
        room_id = hashcode.substring(0, 7);
        mHashCode.setText(room_id);
        //}

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hashCode = mHashCode.getText().toString();
                String roomName = mRoomName.getText().toString();
                VotingRoom votingRoom = VotingRoom.newVotingRoom(hashCode, roomName, dueDate, voteTicket);
                if(roomName.length()>0) {
                    mVoRoomRef.child(hashCode).setValue(votingRoom);
                    mVoRoomRef.child(hashCode).child("Voter").child(mAuth.getCurrentUser().getUid()).setValue(true);
                    mVoRoomRef.child(hashCode).child("Voter").child(mAuth.getCurrentUser().getUid()).child("voteTimes").setValue(voteTicket);
                    mRoom.child(hashCode).setValue(true);
                    //mSave.setEnabled(false);
                    Intent VotingRoom = new Intent(VotingRoomEditorActivity.this, VotingRoomActivity.class);
                    VotingRoom.putExtra("room_id", room_id);
                    startActivity(VotingRoom);
                    finish();
                    //Toast.makeText(VotingRoomActivity.this, "successfully created a voting room", Toast.LENGTH_LONG).show();
                    //onResume();
                }
                else{
                    Toast.makeText(VotingRoomEditorActivity.this, "please fill in the room name", Toast.LENGTH_LONG).show();
                }
            }
        });

        mDDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();


                dpd = new android.app.DatePickerDialog(
                        VotingRoomEditorActivity.this,
                        VotingRoomEditorActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        dayOfMonth = "" + i2;
        MonthOfYear = "" + (++i1);
        Year = "" + i;
        dueDate = dayOfMonth+"/"+(MonthOfYear)+"/"+Year;
        String date = "Due Date Of this Voting: "+ dueDate;
        mDueDate.setText(date);
    }
}
