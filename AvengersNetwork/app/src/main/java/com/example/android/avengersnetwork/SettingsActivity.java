package com.example.android.avengersnetwork;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText username, userProfName, userStatus, userCountry, userGender, userRelation, userDob;
    private Button updateAccountSettingsButton;
    private CircleImageView userProfImage;
    private DatabaseReference settingUserRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private StorageReference userProfileImageRef;
    final static int galleryPic = 1;

    String current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
            current_user_id = mAuth.getCurrentUser().getUid();

                   settingUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);

                     userProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile Images");


        mToolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        username = (EditText) findViewById(R.id.setting_username);
        userCountry = (EditText) findViewById(R.id.setting_country);
        userStatus = (EditText) findViewById(R.id.setting_status);
        userProfName = (EditText) findViewById(R.id.setting_profile_full_name);
        userGender = (EditText) findViewById(R.id.setting_gender);
        userRelation = (EditText) findViewById(R.id.setting_relationshipStatus);
        userDob = (EditText) findViewById(R.id.setting_dob);
        userProfImage = (CircleImageView) findViewById(R.id.setting_profile_image);

        loadingBar = new ProgressDialog(this);

        updateAccountSettingsButton = (Button) findViewById(R.id.update_account_setting_button);


      //////////////Here 1Crashed

            settingUserRef.addValueEventListener(new ValueEventListener() {
                @Override

                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String myProfileImage = dataSnapshot.child("Profileimage").getValue().toString();
                        String myUsername = dataSnapshot.child("username").getValue().toString();
                       String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                        String myProfileStatus = dataSnapshot.child("status").getValue().toString();
                        String myDOB = dataSnapshot.child("dob").getValue().toString();
                       String myCountry = dataSnapshot.child("Country").getValue().toString();
                        String myGender = dataSnapshot.child("Gender").getValue().toString();
                        String myRelationshipStatus = dataSnapshot.child("Relationship Status").getValue().toString();

                        Picasso.with(SettingsActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfImage);

                       username.setText(myUsername);
                       userProfName.setText(myProfileName);
                        userStatus.setText(myProfileStatus);
                        userDob.setText(myDOB);
                       userCountry.setText(myCountry);
                        userGender.setText(myGender);
                        userRelation.setText(myRelationshipStatus);
                        Log.d("TAG", "IN add Value Event");

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            updateAccountSettingsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ValidateAccountInfo();

                }
            });
            userProfImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, galleryPic);
                }
            });
        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==galleryPic && resultCode==RESULT_OK && data!=null)
        {
            Uri imageUri=data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK)
            {    loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait ! while we are updating your Profile Image.");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();


                Uri resultUri=result.getUri();
                StorageReference filePath =userProfileImageRef.child(current_user_id+ ".jpg");

                filePath.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String downloadUrl = uri.toString();
                                        settingUserRef.child("Profileimage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {  Intent selfIntent=new Intent(SettingsActivity.this,SettingsActivity.class);
                                                    startActivity(selfIntent);
                                                    Toast.makeText(SettingsActivity.this,"Profile Image Stored to firebase databse Successfully",Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }
                                                else
                                                {   String message=task.getException().getMessage();
                                                    Toast.makeText(SettingsActivity.this,"Error Occured!"+message,Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }

                                            }
                                        });
                                    }
                                });

                            }
                        });
            }
            else
            {
                Toast.makeText(SettingsActivity.this,"Error Occured! Image Can't be cropped.",Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }

        }
    }

    private void ValidateAccountInfo()
    {

        String myusername =username.getText().toString();
        String myprofilename =userProfName.getText().toString();
        String mystatus =userStatus.getText().toString();
        String mydob =userDob.getText().toString();
        String mygender =userGender.getText().toString();
        String myrelationshipstatus =userRelation.getText().toString();

        if(TextUtils.isEmpty(myusername))
        {
            Toast.makeText(this, "Please Write your username...", Toast.LENGTH_SHORT).show();
        }
        else  if(TextUtils.isEmpty(myprofilename))
        {
            Toast.makeText(this, "Please Write your Full Name...", Toast.LENGTH_SHORT).show();
        }
        else  if(TextUtils.isEmpty(mystatus))
        {
            Toast.makeText(this, "Please Write your status...", Toast.LENGTH_SHORT).show();
        }
        else  if(TextUtils.isEmpty(mygender))
        {
            Toast.makeText(this, "Please Write your Gender..", Toast.LENGTH_SHORT).show();
        }
        else  if(TextUtils.isEmpty(myrelationshipstatus))
        {
            Toast.makeText(this, "Please Write your Relationship Status...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(mydob))
        {
            Toast.makeText(this, "Please Write your Date of Birth...", Toast.LENGTH_SHORT).show();
        }
        else
        {

            loadingBar.setTitle("Profile Image");
            loadingBar.setMessage("Please wait ! while we are updating your Profile Image.");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
           UpdateAccountInfo(username,userCountry, userStatus , userProfName , userGender, userRelation,userDob);
        }
        Log.d("TAG","IN Validate Account Info");

    }

    private void UpdateAccountInfo(EditText username, EditText userCountry, EditText userStatus, EditText userProfName, EditText userGender, EditText userRelation, EditText userDob)
    {
        HashMap userMap  =new HashMap();
        userMap.put("username",username);
        userMap.put("Country",userCountry);
        userMap.put("status",userStatus);
        userMap.put("dob",userDob);
        userMap.put("fullname",userProfName);
        userMap.put("Gender",userGender);
        userMap.put("Relationship Status",userRelation);


         settingUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                 if(task.isSuccessful())
                 {    SendUserToMainActivity();
                     Toast.makeText(SettingsActivity.this,"Account Updated Successfully",Toast.LENGTH_SHORT).show();
                     loadingBar.dismiss();
                 }
                 else
                 {
                     String message=task.getException().getMessage();
                     Toast.makeText(SettingsActivity.this,"Error!"+message,Toast.LENGTH_SHORT).show();
                     loadingBar.dismiss();

                 }
            }
        });


    }
    private void SendUserToMainActivity() {
        Intent mainIntent =new Intent(SettingsActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
        Log.d("TAG","Send user to main ");
    }
    }

