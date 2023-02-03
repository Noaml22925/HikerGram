package com.example.hikergram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class EditProfile extends AppCompatActivity {

    private FirebaseUser user;
    private User currentUser;
    private DatabaseReference reference;
    private EditText editUsernameNE, editAgeNE, editEmailNE;
    private String userID, picLink;
    private ImageView editProfilePicNE;
    private StorageReference storageRef;
    private Button saveButton, takeANewPic;
    private String originalUserName;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://hikergram-default-rtdb.firebaseio.com/Users");
        userID = user.getUid();
        editUsernameNE = findViewById(R.id.editUsername);
        editAgeNE = findViewById(R.id.editAge);
        editEmailNE = findViewById(R.id.editEmail);
        editProfilePicNE = findViewById(R.id.editProfilePic);
        saveButton = findViewById(R.id.save);
        takeANewPic = findViewById(R.id.cameraBtn);

        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://hikergram.appspot.com/");

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                currentUser = userProfile;
                originalUserName = currentUser.getUsername();
                editUsernameNE.setText(currentUser.getUsername());
                editAgeNE.setText(String.valueOf(currentUser.getAge()));
                editEmailNE.setText(currentUser.getEmail());
                picLink = currentUser.getProfilePic();
                Picasso.get().load(picLink).into(editProfilePicNE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        takeANewPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    // display error state to the user
                }
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(updateUserInDatabase(currentUser))
                    startActivity(new Intent(EditProfile.this, UserProfile.class));
                else Toast.makeText(EditProfile.this, "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String userUID = userID;
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            editProfilePicNE.setImageBitmap(imageBitmap);


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data1 = baos.toByteArray();
            UploadTask uploadTask = storageRef.child("Profile pictures").child(userUID).putBytes(data1);
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
                    picLink = url.toString();
                }
            });
        }
    }

    private boolean validations() {
        String accountEmail = editEmailNE.getText().toString().trim();
        String accountName = editUsernameNE.getText().toString().trim();
        int accountAge = Integer.parseInt(editAgeNE.getText().toString().trim());
        ArrayList<User> allUsers = UserListSingleton.getInstance().getUserList();
        if (editEmailNE.getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(editEmailNE.getText().toString()).matches() || editUsernameNE.getText().toString().isEmpty() || editAgeNE.getText().toString().isEmpty())
            Toast.makeText(EditProfile.this, "Please fill in all details before doing that", Toast.LENGTH_SHORT).show();
        if (!Patterns.EMAIL_ADDRESS.matcher(accountEmail).matches()) {
            editEmailNE.setError("Please enter a valid email!");
            editEmailNE.requestFocus();
            return false;
        }
        for (int i = 0; i < allUsers.size(); i++) {
            if (accountName.equals(allUsers.get(i).getUsername())) {
                if (!originalUserName.equals(accountName)) {
                    editUsernameNE.setError("Username is already taken");
                    editUsernameNE.requestFocus();
                    return false;
                }
            }
        }
        currentUser.setProfilePic(picLink);
        currentUser.setEmail(accountEmail);
        currentUser.setUsername(accountName);
        currentUser.setAge(accountAge);
        return true;
    }

    public boolean updateUserInDatabase(User user) {
        if (validations()) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://hikergram-default-rtdb.firebaseio.com/Users");
            databaseReference.child(userID).setValue(user);
            return true;
        }
        else return false;
    }
}
