package com.example.hikergram;

import static com.example.hikergram.SignUp.REQUEST_IMAGE_CAPTURE;

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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private EditText editEmail, editPassword, editUsername, editAge;
    private Button takeImage, save;
    private ImageView imageView;
    private StorageReference storageRef;
    private String picLink;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profiel);
        takeImage = findViewById(R.id.cameraBtn);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editUsername = findViewById(R.id.editUsername);
        editAge = findViewById(R.id.editAge);
        imageView = findViewById(R.id.editProfilePic);
        save = findViewById(R.id.start);
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://hikergram.appspot.com/");

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserDetails();
            }
        });
        takeImage.setOnClickListener(new View.OnClickListener() {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (editEmail.getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(editEmail.getText().toString()).matches() || editUsername.getText().toString().isEmpty() || editPassword.getText().toString().isEmpty() || editAge.getText().toString().isEmpty())
            Toast.makeText(EditProfile.this, "Please fill in all details to continue", Toast.LENGTH_SHORT).show();
        else {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageView.setImageBitmap(imageBitmap);


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data1 = baos.toByteArray();
                UploadTask uploadTask = storageRef.child("Profile pictures").child(editEmail.getText().toString()).putBytes(data1);
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
    }

    private void updateUserDetails() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String username = editUsername.getText().toString().trim();
        String age = editAge.getText().toString().trim();
        String photoLink = picLink;

        if (password.length() < 6) {
            editPassword.setError("Password must contain at list 6 digits");
            editPassword.requestFocus();
            return;
        }

        if (username.isEmpty()) {
            editUsername.setError("Name is required!");
            editUsername.requestFocus();
            return;
        }
        if (age.isEmpty()) {
            editAge.setError("Age is required!");
            editAge.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            editEmail.setError("Email is required!");
            editEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editPassword.setError("Password is required!");
            editPassword.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Please provide valid email");
            editEmail.requestFocus();
            return;
        }

        User u = new User();
        u.setUsername(editUsername.getText().toString());
        u.setAge(Integer.parseInt(editAge.getText().toString().trim()));
        u.setEmail(editEmail.getText().toString());
        u.setPassword(editPassword.getText().toString());
        u.setProfilePic(photoLink);

        HashMap<String, Object> newUserValues = new HashMap<>();

        FirebaseDatabase.getInstance().getReferenceFromUrl("https://hikergram-default-rtdb.firebaseio.com/Users").child(user.getUid()).updateChildren(newUserValues).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EditProfile.this, "Data has been updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EditProfile.this, UserProfile.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfile.this, "Failed to update", Toast.LENGTH_SHORT).show();
         }
        });
    }
}
