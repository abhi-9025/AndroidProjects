package com.example.android.avengersnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity {
    private ImageView post_image;
    private TextView postDescription;
    private Button DeletePostButton,EditPostButton;
    private String postKey,current_user_id,databaseUserId,description,Image;
    private DatabaseReference clickPostRef;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        postKey=getIntent().getExtras().get("PostKey").toString();

        clickPostRef= FirebaseDatabase.getInstance().getReference().child("PostNode").child(postKey);

        mAuth=FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();

        post_image    =   (ImageView)findViewById(R.id.click_post_image);
        postDescription  = (TextView)findViewById(R.id.click_post_description);
        DeletePostButton = (Button)findViewById(R.id.delete_post_button);
        EditPostButton  =(Button)findViewById(R.id.edit_post_button);

        DeletePostButton.setVisibility(View.INVISIBLE);
        EditPostButton.setVisibility(View.INVISIBLE);

        clickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if(dataSnapshot.exists()) {
                     description = dataSnapshot.child("description").getValue().toString();
                     Image = dataSnapshot.child("postImage").getValue().toString();
                     databaseUserId = dataSnapshot.child("uid").getValue().toString();

                     postDescription.setText(description);
                     Picasso.with(ClickPostActivity.this).load(Image).into(post_image);
                     if (current_user_id.equals(databaseUserId)) {
                         DeletePostButton.setVisibility(View.VISIBLE);
                         EditPostButton.setVisibility(View.VISIBLE);
                     }
                     EditPostButton.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             EditCurrentPost(description);
                         }
                     });
                 }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DeletePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteCurrentPost();
            }
        });




    }

    private void EditCurrentPost(String description)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Edit Post");
        final EditText inputField = new EditText(ClickPostActivity.this);
        inputField.setText(description);
        builder.setView(inputField);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {   Log.d("TAG","crashed here again");

                clickPostRef.child("description").setValue(inputField.getText().toString());
                Toast.makeText(ClickPostActivity.this, "Post Updated Successfully", Toast.LENGTH_SHORT).show();
                SendUserToMainActivity();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        Dialog dialog=builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_green_dark);
    }

    private void DeleteCurrentPost()
    {  AlertDialog.Builder builder=new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Do you Want to Delete this Post?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                clickPostRef.removeValue();
                SendUserToMainActivity();
                Toast.makeText(ClickPostActivity.this,"Post Is Deleted",Toast.LENGTH_SHORT).show();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        Dialog dialog=builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_green_dark);


    }
    private void SendUserToMainActivity() {
        Intent mainIntent =new Intent(ClickPostActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
