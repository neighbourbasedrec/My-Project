package com.example.apple.votingmachine;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class MyProfileActivity extends AppCompatActivity {

    private TextView name;
    private TextView email;
    private TextView description;
    private CircleImageView mCircleImageView;
    private Button changeImage;
    private DatabaseReference mMyRef;
    private FirebaseAuth mAuth;
    private StorageReference mStorageReference;
    private ProgressDialog mProgressDialog;
    private static final int GALLERY_PICK = 1;
    String uid;
    byte[] thumb_bytes = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        name = (TextView) findViewById(R.id.user_name);
        email = (TextView) findViewById(R.id.email);
        description = (TextView) findViewById(R.id.self);
        changeImage = (Button) findViewById(R.id.buttonChangeImage);
        mCircleImageView = (CircleImageView) findViewById(R.id.displayimage);
        mProgressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mMyRef = FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getCurrentUser().getUid());
        mStorageReference = FirebaseStorage.getInstance().getReference();


        mMyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                fillInUI(user);
                final String image = user.getImage();
                if (!image.equals("default"))
                // Picasso.with(SettingActivity.this).load(image).placeholder(R.drawable.user_img).into(mCircleImageView);
                //----OFFLINE FEATURE-----
                {
                    Toast.makeText(MyProfileActivity.this, "image is not null", Toast.LENGTH_LONG).show();
                    Picasso.with(MyProfileActivity.this).load(image).into(mCircleImageView);

                    /*Picasso.with(MyProfileActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).
                            placeholder(R.drawable.default_user_img).into(mCircleImageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(MyProfileActivity.this).load(image).placeholder(R.drawable.default_user_img).into(mCircleImageView);
                        }
                    });*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLERY_PICK);
            }
        });
    }

    public void fillInUI(User user) {
        name.setText(user.getName());
        email.setText(user.getEmail());
        description.setText(user.getDescription());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        //-----STARTING GALLERY----
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri sourceUri = data.getData();

            //-------CROPPING IMAGE AND SETTING MINIMUM SIZE TO 500 , 500------
            CropImage.activity(sourceUri).
                    setAspectRatio(1, 1).
                    setMinCropWindowSize(500, 500).
                    start(MyProfileActivity.this);

        }

        //------START CROP IMAGE ACTIVITY------
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            //------CROP IMAGE RESULT------
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mProgressDialog.setTitle("Uploading Image");
                mProgressDialog.setMessage("Please wait while we process and upload the image...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.show();


                Uri resultUri = result.getUri();
                File thumb_filepath = new File(resultUri.getPath());
                try {

                    //--------COMPRESSING IMAGE--------
                    Bitmap thumb_bitmap = new Compressor(this).
                            setMaxWidth(200).
                            setMaxHeight(200).
                            setQuality(75).
                            compressToBitmap(thumb_filepath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    thumb_bytes = baos.toByteArray();


                } catch (IOException e) {
                    e.printStackTrace();
                }

                final StorageReference filepath = mStorageReference.child("profile_image").child(uid + ".jpg");
                final StorageReference thumb_file_path = mStorageReference.child("profile_image").child("thumbs").child(uid + ".jpg");
                //upload the pic to firebase storage
                filepath.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filepath.getDownloadUrl();
                    }
                    //get the url link to the pic storage
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            final String downloadUrl = task.getResult().toString();
                            UploadTask uploadTask = thumb_file_path.putBytes(thumb_bytes);
                            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    return thumb_file_path.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        final String downloadUrlThumb = task.getResult().toString();
                                        Map update_HashMap = new HashMap();
                                        update_HashMap.put("image", downloadUrl);
                                        update_HashMap.put("thumb_image", downloadUrlThumb);
                                        mMyRef.updateChildren(update_HashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {
                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(MyProfileActivity.this, "Uploaded Successfuly...", Toast.LENGTH_SHORT).show();

                                                } else {
                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), " Image is not uploading...", Toast.LENGTH_SHORT).show();

                                                }

                                            }
                                        });
                                    } else {
                                        finish();
                                    }
                                }
                            });
                        } else {
                            finish();
                        }
                    }
                });


            }


        }
    }
}



