package com.example.apple.votingmachine;

import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.app.ActionBar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText mEditName,mEdimail,mEditpassword, mEditDescription;
    private Button buttonsubmit;
    private DatabaseReference mDatabase;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //this.setTitle("Register");
        mEditName=(EditText)findViewById(R.id.user_name);
        mEdimail=(EditText)findViewById(R.id.email);
        mEditpassword=(EditText)findViewById(R.id.password);
        mEditDescription = (EditText)findViewById(R.id.self);
        buttonsubmit=(Button)findViewById(R.id.submit);

        buttonsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String displayname=mEditName.getText().toString();
                String email=mEdimail.getText().toString();
                String password=mEditpassword.getText().toString();
                String selfDescription = mEditDescription.getText().toString();

                //----CHECKING THE EMPTINESS OF THE EDITTEXT-----
                if(displayname.equals("")){
                    Toast.makeText(RegisterActivity.this, "Please Fill the name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(email.equals("")){
                    Toast.makeText(RegisterActivity.this, "Please Fill the email", Toast.LENGTH_SHORT).show();
                    return ;
                }

                if(email.equals("")){
                    selfDescription = "normal person";
                    return ;
                }

                if(password.length()<6){
                    Toast.makeText(RegisterActivity.this, "Password is too short", Toast.LENGTH_SHORT).show();
                    return;
                }
                register_user(displayname,email,password, selfDescription);
            }
        });



        mAuth=FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance().getReference().child("user");
    }

    /*@Override
    public void onFocusChange(View v, boolean hasFocus) {
        //super.onFocusChange(v, hasFocus);
        switch(v.getId()){
            case R.id.user_name:
                mEditName.setHint("123123123");
                break;
            case R.id.email:
                mEdimail.setHint("123123");
                break;
            case R.id.password:
                mEditpassword.setHint("4312312312");
                break;
        }

    }*/


    //-----REGISTERING THE NEW USER------
    private void register_user(final String displayname, String email, String password, final String selfDescription) {

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegisterActivity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //------IF USER IS SUCCESSFULLY REGISTERED-----
                if(task.isSuccessful()){
                    final Map userMap=new HashMap();
                    mUser = mAuth.getCurrentUser();
                    final String Uid = mUser.getUid();
                    final String Uname = displayname;
                    String Uemail = mUser.getEmail();
                    String token_id= FirebaseInstanceId.getInstance().getToken();
                    userMap.put("device_token",token_id);
                    userMap.put("name",displayname);
                    userMap.put("email", Uemail);
                    userMap.put("uid", Uid);
                    userMap.put("description","normal person");
                    userMap.put("image","default");
                    userMap.put("thumb_image","default");
                    mDatabase.child(Uid).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startMainActivity();
                            try {
                                SharedPreference.saveUserName(RegisterActivity.this, Uname);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        //DatabaseReference mUserReference = FirebaseDatabase.getInstance().getReference().child("user");
                    });
                }
                //---ERROR IN ACCOUNT CREATING OF NEW USER---
                else{
                    Toast.makeText(getApplicationContext(), "ERROR REGISTERING USER....", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startMainActivity(){
        Intent mainActivity = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }
}

