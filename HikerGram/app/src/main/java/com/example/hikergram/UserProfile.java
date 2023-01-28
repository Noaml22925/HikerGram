package com.example.hikergram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserProfile extends AppCompatActivity {
    private BottomNavigationView navBar;
    private FirebaseUser user;
    private DatabaseReference reference;
    private TextView username;
    private String userID;
    private FirebaseStorage storage;
    private StorageReference mStorageRef;
    private ImageView profilePic;
    private Button editProfileBtn;
    private BottomNavigationView bottomNavigationView;
    private ArrayList<Post> posts = new ArrayList<>();
    private ListView listView;
    private CustomArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        listView = findViewById(R.id.listView);
        profilePic = findViewById(R.id.profilePic);
        username = findViewById(R.id.usernameDisplay);
        bottomNavigationView = findViewById(R.id.navBar);
        user = FirebaseAuth.getInstance().getCurrentUser();
        editProfileBtn = findViewById(R.id.editProfileBtn);
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserProfile.this, EditProfile.class));
            }
        });
        reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://hikergram-default-rtdb.firebaseio.com/Users");
        userID = user.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://hikergram-default-rtdb.firebaseio.com/");

        databaseReference.child("Posts").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    posts.add(post);
                    System.out.println(posts.size());
                }
                showList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        // Open the HomeScreen activity
                        Intent homeIntent = new Intent(UserProfile.this, HomeScreen.class);
                        startActivity(homeIntent);


                        break;

                    case R.id.explore:
                        // Open the Explore activity
                        Intent exploreIntent = new Intent(UserProfile.this, Explore.class);
                        startActivity(exploreIntent);


                        break;

                    case R.id.post:
                        // Open the CreateNewPost activity
                        Intent createNewPostIntent = new Intent(UserProfile.this, CreateNewPost.class);
                        startActivity(createNewPostIntent);


                        break;

                    case R.id.myProfile:
                        // Open the UserProfile activity


                        break;
                }
                return true;
            }
        });

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                username.setText(userProfile.getUsername());
                Picasso.get().load(userProfile.getProfilePic()).into(profilePic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Get a reference to the ListView

        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Post currentPost = posts.get(position);
                if (currentPost != null) {
                    String postUUID = String.valueOf(currentPost.getUuid());
                    Intent intent = new Intent(UserProfile.this, DisplayPostActivity.class);
                    intent.putExtra("uuid", postUUID);
                    startActivity(intent);
                }
            }
        });


    }

    public void showList() {

// Create a new CustomArrayAdapter with the list of Post objects
        adapter = new CustomArrayAdapter(this, R.layout.view_post, posts);
// Set the adapter on the ListView
        listView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(UserProfile.this, SignIn.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
