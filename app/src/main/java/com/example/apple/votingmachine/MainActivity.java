package com.example.apple.votingmachine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton mGoogleSignIn;
    private Button mLogIn;
    private Button mRegister;
    private EditText mEmailBar;
    private EditText mPasswordBar;
    private static int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mUserReference;
    private Boolean userExist = false;
    private static String LOG_WITH_GOOGLE = "google log in";
    private static String LOG_DEFAULT = "default log in";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
    }

    protected void onStart() {

        super.onStart();
        FirebaseUser user= mAuth.getCurrentUser();
            if(user==null){
                LogIn();
            }
            else{
                Toast.makeText(MainActivity.this, user.getEmail() + "", Toast.LENGTH_LONG);
            }
    }

    public void LogIn() {
        Intent intent = new Intent(MainActivity.this,LogInActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out:
                //Toast.makeText(MainActivity.this, "" + sp.getInt(SIGN_IN, 0) + "!!!!!!!!!!!!!!!!!!!!!!", Toast.LENGTH_SHORT).show();
                //AuthUI.getInstance().signOut(this);
                FirebaseAuth.getInstance().signOut();
                onStart();
                //Toast.makeText(MainActivity.this, "" + sp.getInt(SIGN_IN, 0) + "!!!!!!!!!!!!!!!!!!!!!!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.voting_room:
                startVotingRoom();
                return true;
            case R.id.voting_room_list:
                VotingRoomList();
                return true;
            case R.id.add_voter:
                addVoter();
                return true;
            case R.id.request_list:
                requestList();
                return true;
            case R.id.search_room:
                SearchRoom();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startVotingRoom(){
        Intent startVotingRoom = new Intent(MainActivity.this, VotingRoomActivity.class);
        startActivity(startVotingRoom);
    }

    public void VotingRoomList(){
        Intent startVotingRoomList = new Intent(MainActivity.this, VotingRoomListActivity.class);
        startActivity(startVotingRoomList);
    }

    public void addVoter(){
        Intent addVoter = new Intent(MainActivity.this, AddVoterActivity.class);
        startActivity(addVoter);
    }

    public void requestList(){
        Intent requestList = new Intent(MainActivity.this, RequestListActivity.class);
        startActivity(requestList);
    }

    public void SearchRoom(){
        Intent searchRoom = new Intent(MainActivity.this, SearchVotingRoomActivity.class);
        startActivity(searchRoom);
    }

}
//<string name="default_web_client_id">808351059990-ksvc1n6dq3t3ddkarthuo1uja4735djf.apps.googleusercontent.com</string>