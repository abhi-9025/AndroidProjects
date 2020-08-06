package com.example.android.avengersnetwork;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
{   private NavigationView navigationView;
     private DrawerLayout drawerLayout;
     private RecyclerView postList;
     private Toolbar mToolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CircleImageView NavProfileImage;
    private TextView NavProfileUserName;
    private ImageButton addNewPostButton;

    private FirebaseAuth mAuth;
    private DatabaseReference UserRef,PostsRef;
    String currentUserId;
    private FirebaseRecyclerAdapter<Posts, PostsHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        //TODO here crashed;
        Log.d("Tag", "app Crashed");
        //if (mAuth.getCurrentUser() != null) {
            currentUserId =  mAuth.getCurrentUser().getUid();
       // }
        Log.d("Tag", "app Crashed");


        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef= FirebaseDatabase.getInstance().getReference().child("PostNode");

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        addNewPostButton=(ImageButton)findViewById(R.id.add_new_post_button);

        postList=(RecyclerView)findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);
       LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        postList.setLayoutManager(linearLayoutManager);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawablelayout_id);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View navView=navigationView.inflateHeaderView(R.layout.navigation_header);
        NavProfileImage=(CircleImageView)navView.findViewById(R.id.nav_profile_image);
        NavProfileUserName=(TextView)navView.findViewById(R.id.nav_user_full_name);



       // if (mAuth.getCurrentUser() != null) {
            UserRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        if(dataSnapshot.hasChild("username"))
                        {
                        String userName = dataSnapshot.child("username").getValue().toString();
                            NavProfileUserName.setText(userName);
                        }
                        if(dataSnapshot.hasChild("Profileimage"))
                        {
                            String image = dataSnapshot.child("Profileimage").getValue().toString();
                            Picasso.with(MainActivity.this).load(image).placeholder(R.drawable.profile).into(NavProfileImage);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,"profile name do not exists...",Toast.LENGTH_SHORT).show();
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    UserMenuSelector(menuItem);
                    return false;
                }
            });

        addNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendUserToPostActivity();
            }
        });
        DisplayAllUsersPost();

    }

    private void DisplayAllUsersPost()
    {
        FirebaseRecyclerOptions<Posts>firebaseRecyclerOptions=new FirebaseRecyclerOptions.Builder<Posts>().setQuery(PostsRef,Posts.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, PostsHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull PostsHolder postsHolder, int i, @NonNull Posts posts) {
                final String postKey=getRef(i).getKey();
                postsHolder.setPosts(posts);
                //////////////////////////
                postsHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent clickPostIntent =new Intent(MainActivity.this,ClickPostActivity.class);
                         clickPostIntent.putExtra("PostKey",postKey);
                        startActivity(clickPostIntent);

                    }
                });
                /////////////////
            }

            @NonNull
            @Override
            public PostsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_post_layout, parent, false);
                return new PostsHolder(view);
            }
        };
        postList.setAdapter(firebaseRecyclerAdapter);

    }
    public static class PostsHolder extends RecyclerView.ViewHolder
    {  private CircleImageView profile_image;
       private ImageView post_image;
       private TextView Description;
       private TextView Date,Time,Has_updated_posts,Has_Username;

        public PostsHolder(@NonNull View itemView)
        {
            super(itemView);
            Date = itemView.findViewById(R.id.post_date);
            Time= itemView.findViewById(R.id.post_time);
            Has_Username=itemView.findViewById(R.id.post_user_name);
            Description=itemView.findViewById(R.id.post_description);
            profile_image=itemView.findViewById(R.id.post_profile_image);
            post_image=itemView.findViewById(R.id.post_image);


        }
        public void setPosts(Posts posts)
        {
            String user_name= posts.getFullname();
            Has_Username.setText(user_name);
            String users_description=posts.getDescription();
              Description.setText(users_description);
              String users_date=posts.getDate();
              Date.setText("  " +users_date);
              String users_time= posts.getTime();
              Time.setText("  "+users_time);
              String users_image=posts.getProfileimage();
              Log.d("Tag","profile image"+users_image);
              Picasso.with(itemView.getContext()).load(users_image).into(profile_image);
              String users_posts_image=posts.getPostImage();
              Log.d("TAG","Post image"+users_posts_image);
           Picasso.with(itemView.getContext()).load(users_posts_image).into(post_image);


        }
    }


    private void SendUserToPostActivity()
    {
        Intent addNewPostIntent=new Intent(MainActivity.this,PostActivity.class);
        startActivity(addNewPostIntent);
    }


     @Override

    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser==null)
        {
            SendUserToLoginActivity();
        }
        else
        {
            CheckUserExistence();
        }
         firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseRecyclerAdapter!=null)
        {
            firebaseRecyclerAdapter.stopListening();
        }
    }

    private void CheckUserExistence()
    {
        final String current_user_id=mAuth.getCurrentUser().getUid();

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(current_user_id))
                {
                    SendUserToSetupActivity();
                    //SendUserToLoginActivity();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void SendUserToSetupActivity() {
        Intent SetupIntent=new Intent(MainActivity.this,SetupActivity.class);
        SetupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(SetupIntent);
        finish();
    }

    private void SendUserToLoginActivity() {
        Intent LoginIntent=new Intent(MainActivity.this,LoginActivity.class);
        LoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
          if(actionBarDrawerToggle.onOptionsItemSelected(item))
          {
              return true;
          }

        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {   case R.id.nav_post:
                                SendUserToPostActivity();
                                break;
            case  R.id.nav_profile:
                                   Toast.makeText(this,"Profile", Toast.LENGTH_SHORT).show();
                                    break;
            case  R.id.nav_home:
                                   Toast.makeText(this,"Home", Toast.LENGTH_SHORT).show();
                                    break;
            case  R.id.nav_friends:
                                   Toast.makeText(this,"Friends", Toast.LENGTH_SHORT).show();
                                    break;
            case  R.id.nav_find_friends:
                                   Toast.makeText(this,"Find Friends", Toast.LENGTH_SHORT).show();
                                    break;
            case  R.id.nav_messages:
                                    Toast.makeText(this,"Messages", Toast.LENGTH_SHORT).show();
                                     break;
            case  R.id.nav_setting: SendUserToSettingActivity();

                                      break;
            case  R.id.nav_logout:
                                       mAuth.signOut();
                                         SendUserToLoginActivity();
                                          break;
        }

    }
    private void SendUserToSettingActivity() {
        Intent LoginIntent=new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(LoginIntent);
    }


}