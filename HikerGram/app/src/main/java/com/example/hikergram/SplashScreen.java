package com.example.hikergram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity {
    private DatabaseReference userReference, postReference;
    private ArrayList<User> users;
    private ArrayList<Post> posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        users = new ArrayList<>();
        posts = new ArrayList<>();

        userReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://hikergram-default-rtdb.firebaseio.com/Users");
        postReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://hikergram-default-rtdb.firebaseio.com/Posts");
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    users.add(user);
                }
                UserListSingleton.getInstance().setUserList(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        postReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Post post = postSnapshot.getValue(Post.class);

                        posts.add(post);
                    }
                }
                PostListSingleton.getInstance().setPostList(posts);
                startActivity(new Intent(SplashScreen.this, SignIn.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}