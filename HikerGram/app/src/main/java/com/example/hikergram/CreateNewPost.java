package com.example.hikergram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class CreateNewPost extends AppCompatActivity {
    private EditText hikeName, hikeDescription;
    private TextView difficultyDisplay;
    private SeekBar difficultyBar;
    private Button takePicture, publish;
    private String hikePicLink, userID;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    StorageReference storageRef;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    final int CAMERA_REQUEST = 2;
    ImageView hikePicture;
    private BottomNavigationView bottomNavigationView;
    private User author;
    private DatabaseReference reference;
    private int difficultyLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_post);
        hikeName = findViewById(R.id.displayNameOfHike);
        difficultyBar = findViewById(R.id.displayDifficultySeekBar);
        hikePicture = findViewById(R.id.displayHikePicture);
        takePicture = findViewById(R.id.showLocationInGoogleMaps);
        hikeDescription = findViewById(R.id.hikeDescription);
        publish = findViewById(R.id.publish);
        difficultyDisplay = findViewById(R.id.difficultyDisplay);
        mAuth = FirebaseAuth.getInstance();
        difficultyBar = findViewById(R.id.displayDifficultySeekBar);
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://hikergram.appspot.com/");


        difficultyBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                difficultyLevel = i;
                difficultyDisplay.setText(String.valueOf(i));

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    // display error state to the user
                }
            }
        });
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://hikergram-default-rtdb.firebaseio.com/Users");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                author = userProfile;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        bottomNavigationView = findViewById(R.id.navBar);
        // Set a listener for navigation item clicks
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        // Open the HomeScreen activity
                        Intent homeIntent = new Intent(CreateNewPost.this, HomeScreen.class);
                        startActivity(homeIntent);



                        break;
                    case R.id.explore:
                        // Open the Explore activity
                        Intent exploreIntent = new Intent(CreateNewPost.this, Explore.class);
                        startActivity(exploreIntent);


                        break;
                    case R.id.post:
                        // Handle the post button press



                        break;
                    case R.id.myProfile:
                        // Open the UserProfile activity
                        Intent userProfileIntent = new Intent(CreateNewPost.this, UserProfile.class);
                        startActivity(userProfileIntent);



                        break;
                }
                return true;
            }
        });
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validations()) {
                    createPostInDatabase();
                    startActivity(new Intent(CreateNewPost.this, UserProfile.class));
                }
                else Toast.makeText(CreateNewPost.this, "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
            }
        });
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
                Intent intent = new Intent(CreateNewPost.this, SignIn.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String nameOfHike = hikeName.getText().toString();
        if (nameOfHike.isEmpty()) {
            Toast.makeText(CreateNewPost.this, "Please give the hike a name first please", Toast.LENGTH_SHORT).show();
        }

        else {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                hikePicture.setImageBitmap(imageBitmap);


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data1 = baos.toByteArray();
                UploadTask uploadTask = storageRef.child("Posts").child(nameOfHike).putBytes(data1);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                        while (!((Task<?>) uri).isComplete()) ;
                        Uri url = uri.getResult();
                        hikePicLink = url.toString();
                    }
                });
            }
        }
    }
    private void createPostInDatabase(){
        String hikeNameCt = hikeName.getText().toString();
        String hikeDescriptionCt = hikeDescription.getText().toString();
        difficultyLevel = difficultyBar.getProgress();

        Post post = new Post(author, hikeNameCt, hikeDescriptionCt, hikePicLink, difficultyLevel, String.valueOf(UUID.randomUUID()));

        FirebaseDatabase.getInstance().getReferenceFromUrl("https://hikergram-default-rtdb.firebaseio.com/Posts")
                .child(user.getUid()).child(hikeNameCt).setValue(post)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            PostListSingleton.getInstance().getPostList().add(post);
                            Toast.makeText(CreateNewPost.this, "Post uploaded", Toast.LENGTH_SHORT).show();
                        }
                        else Toast.makeText(CreateNewPost.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private boolean validations(){
        String hikeNameCt = hikeName.getText().toString();
        String hikeDescriptionCt = hikeDescription.getText().toString();
        difficultyLevel = difficultyBar.getProgress();
        if (hikeNameCt.isEmpty() || hikeDescriptionCt.isEmpty() || hikePicLink.isEmpty()) {
            Toast.makeText(CreateNewPost.this, "Please fill out all the fields please", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(hikeNameCt.isEmpty()) {
            hikeName.setError("Please enter a name for the hike");
            hikeName.requestFocus();
            return false;
        }
        if(!hikeNameCt.matches(".*[a-z].*")) {
            hikeName.setError("Hike name must include at least one letter");
            hikeName.requestFocus();
            return false;
        }
        if(hikeDescriptionCt.isEmpty()) {
            hikeDescription.setError("Please enter a description for the hike");
            hikeName.requestFocus();
            return false;
        }
        if(!hikeDescriptionCt.matches(".*[a-z].*")) {
            hikeDescription.setError("Hike description name must include at least one letter");
            hikeDescription.requestFocus();
            return false;
        }
        if (hikePicLink.isEmpty()) {
            Toast.makeText(this, "Please add a picture for the hike", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
