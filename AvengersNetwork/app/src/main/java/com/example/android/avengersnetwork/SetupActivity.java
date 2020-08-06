package com.example.android.avengersnetwork;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText UserName,FullName,CountryName;
   private Button saveInformationButton;
    private CircleImageView profileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private StorageReference userProfileImageRef;

    private ProgressDialog loadingBar;
    String currentUserId;
    final static  int galleryPic=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

         mAuth=FirebaseAuth.getInstance();
         currentUserId=mAuth.getCurrentUser().getUid();

         UsersRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
         userProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile Images");

        UserName    =  (EditText)findViewById(R.id.setup_username);
        FullName    =   (EditText)findViewById(R.id.setup_fullname);
        CountryName =  (EditText)findViewById(R.id.setup_countryname);
        saveInformationButton= (Button)findViewById(R.id.setup_information_button);
        profileImage  = (CircleImageView)findViewById(R.id.setup_profileimage);
        loadingBar =new ProgressDialog(this);


        saveInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveAccountSetupInformation();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,galleryPic);
            }
        });

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {  if(dataSnapshot.hasChild("Profileimage"))
                {
                    String image = dataSnapshot.child("Profileimage").getValue().toString();
                    Picasso.with(SetupActivity.this).load(image).placeholder(R.drawable.profile).into(profileImage);
                }
              else
                {
                    Toast.makeText(SetupActivity.this, "Please select Profile Image first.", Toast.LENGTH_SHORT).show();
                }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                  StorageReference filePath =userProfileImageRef.child(currentUserId+ ".jpg");

                  filePath.putFile(resultUri)
                          .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                              @Override
                              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                  final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                                  firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                      @Override
                                      public void onSuccess(Uri uri) {
                                          final String downloadUrl = uri.toString();
                                         UsersRef.child("Profileimage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                             @Override
                                             public void onComplete(@NonNull Task<Void> task) {
                                                 if(task.isSuccessful())
                                                 {  Intent selfIntent=new Intent(SetupActivity.this,SetupActivity.class);
                                                 startActivity(selfIntent);
                                                     Toast.makeText(SetupActivity.this,"Profile Image Stored to firebase databse Successfully",Toast.LENGTH_SHORT).show();
                                                     loadingBar.dismiss();
                                                 }
                                                 else
                                                 {   String message=task.getException().getMessage();
                                                     Toast.makeText(SetupActivity.this,"Error Occured!"+message,Toast.LENGTH_SHORT).show();
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
                  Toast.makeText(SetupActivity.this,"Error Occured! Image Can't be cropped.",Toast.LENGTH_SHORT).show();
                  loadingBar.dismiss();
              }

                  }
              }


    private void SaveAccountSetupInformation()
    {
        String username=UserName.getText().toString();
        String fullname=FullName.getText().toString();
        String country=CountryName.getText().toString();

        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this, "Please write username", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(fullname))
        {
            Toast.makeText(this, "Please write fullname", Toast.LENGTH_SHORT).show();
        }
       else if(TextUtils.isEmpty(country))
        {
            Toast.makeText(this, "Please write country", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Saving Information");
            loadingBar.setMessage("Please wait ! while we are creating your New Account.");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap=new HashMap();
            userMap.put("username",username);
            userMap.put("fullname",fullname);
            userMap.put("Country",country);
            userMap.put("status","Hey there i am using Avengers social network developed by abhishek.");
            userMap.put("Gender","none");
            userMap.put("dob","none");
            userMap.put("Relationship Status","none");
            UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if(task.isSuccessful())
                    {  SendUserToMainActivity();
                        Toast.makeText(SetupActivity.this, "Your Account is created Successfully", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                    else
                    {
                        String message=task.getException().getMessage();
                        Toast.makeText(SetupActivity.this,"Error!"+message,Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }

                }
            });


        }

    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent =new Intent(SetupActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}

