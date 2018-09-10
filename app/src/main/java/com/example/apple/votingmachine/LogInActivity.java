package com.example.apple.votingmachine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class LogInActivity extends AppCompatActivity{

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
        setContentView(R.layout.activity_login);
        mUserReference = FirebaseDatabase.getInstance().getReference().child("user");
        mAuth = FirebaseAuth.getInstance();
        mEmailBar =(EditText) findViewById(R.id.email);
        mPasswordBar = (EditText) findViewById(R.id.password);
        mLogIn = (Button) findViewById(R.id.login);
        mRegister = (Button) findViewById(R.id.register);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //updateUI(account);
        mGoogleSignIn = findViewById(R.id.sign_in_button);
        mGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                }
            }
        });

        mLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmailBar.getText().toString();
                String password = mPasswordBar.getText().toString();
                login_user(email,password);
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LogInActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }

    //log in by email and password
    public void login_user(String email,String password){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            mUser = mAuth.getCurrentUser();
                            String user_id=mUser.getUid();
                            String token_id= FirebaseInstanceId.getInstance().getToken();
                            Map addValue = new HashMap();
                            addValue.put("device_token",token_id);

                            //---IF UPDATE IS SUCCESSFULL , THEN OPEN MAIN ACTIVITY---
                            mUserReference.child(user_id).updateChildren(addValue, new DatabaseReference.CompletionListener(){

                                @SuppressLint("RestrictedApi")
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                    if(databaseError==null){

                                        //---OPENING MAIN ACTIVITY---
                                        Log.e("Login : ","Logged in Successfully" );
                                        Toast.makeText(getApplicationContext(), "Logged in Successfully", Toast.LENGTH_SHORT).show();
                                        //Intent intent=new Intent(MainActivity.this,MainActivity.class);
                                        //startActivity(intent);
                                        //finish();
                                    }
                                    else{
                                        Toast.makeText(LogInActivity.this, databaseError.toString()  , Toast.LENGTH_SHORT).show();
                                        Log.e("Error is : ",databaseError.toString());

                                    }
                                }
                            });
                            //Toast.makeText(MainActivity.this, "Successfully log in!!!!!", Toast.LENGTH_LONG);
                        }
                    }
                });
    }
    //sign in by google
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    //sign in by google
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "firebaseAuthWithGoogle:7777777777777");
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + "666666666666666666666666666666666666666666666666666666666");
                firebaseAuthWithGoogle(account);
                Toast.makeText(LogInActivity.this, "successfully log in", Toast.LENGTH_LONG);
            } catch (ApiException e) {
                Log.d(TAG, "firebaseAuthWithGoogle:" + "888888888888888888888888888888888888");
                Toast.makeText(LogInActivity.this, "Account does not exist", Toast.LENGTH_LONG);
                // Google Sign In failed, update UI appropriately
                //Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }
    //sign in by google
    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        Toast.makeText(LogInActivity.this, "firebaseAuthWithGoogle:" + acct.getId(), Toast.LENGTH_LONG);
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mUser = mAuth.getCurrentUser();
                            String Uid = mUser.getUid();
                            String Uname = mUser.getDisplayName();
                            String Uemail = mUser.getEmail();
                            String token_id= FirebaseInstanceId.getInstance().getToken();
                            User newUser = User.newUser(Uid, Uname, Uemail, token_id, "normal person");
                            mUserReference.child(Uid).setValue(newUser);
                            startMainActivity();
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LogInActivity.this, "signInWithCredential:success", Toast.LENGTH_LONG);

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                        // ...
                    }
                });
    }

    private void startMainActivity(){
        Intent mainActivity = new Intent(LogInActivity.this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }

}
