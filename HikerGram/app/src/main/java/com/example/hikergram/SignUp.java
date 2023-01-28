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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class SignUp extends AppCompatActivity {
    EditText email, userName, password, age;
    Button submit, cameraBtn;
    private FirebaseAuth mAuth;
    ImageView imageView;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    StorageReference storageRef;
    final int CAMERA_REQUEST = 2;
    final String TAG = "SignUpActivity";
    String nameInput, picLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReferenceFromUrl("https://hikergram-default-rtdb.firebaseio.com/");
        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://hikergram.appspot.com/");
        email = findViewById(R.id.email);
        userName = findViewById(R.id.userName);
        password = findViewById(R.id.password);
        cameraBtn = findViewById(R.id.cameraBtn);
        age = findViewById(R.id.age);
        submit = findViewById(R.id.btn);
        imageView = findViewById(R.id.cameraIV);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
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
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validations()) {
                    registerUser();
                    Intent intent = new Intent(SignUp.this, SignIn.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (email.getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches() || userName.getText().toString().isEmpty() || password.getText().toString().isEmpty() || age.getText().toString().isEmpty())
            Toast.makeText(SignUp.this, "Please fill in all details to continue", Toast.LENGTH_SHORT).show();
        else {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageView.setImageBitmap(imageBitmap);


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data1 = baos.toByteArray();
                UploadTask uploadTask = storageRef.child("Profile pictures").child(email.getText().toString()).putBytes(data1);
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
        private boolean validations () {
            String accountEmail = email.getText().toString().trim();
            String accountName = userName.getText().toString().trim();
            String accountPassword = password.getText().toString().trim();
            int accountAge = Integer.parseInt(age.getText().toString().trim());
            if (email.getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches() || userName.getText().toString().isEmpty() || password.getText().toString().isEmpty() || age.getText().toString().isEmpty())
                Toast.makeText(SignUp.this, "Please fill in all details before doing that", Toast.LENGTH_SHORT).show();
            if (!Patterns.EMAIL_ADDRESS.matcher(accountEmail).matches()) {
                email.setError("Please enter a valid email!");
                email.requestFocus();
                return false;
            }
            if (!accountPassword.matches(".*[a-z].*") || accountPassword.length() < 6) {
                password.setError("Password must contain at least 1 character and be longer than 6 characters");
                password.requestFocus();
                return false;
            }
            return true;
        }


    private void registerUser() {
        String accountEmail = email.getText().toString().trim();
        String accountName = userName.getText().toString().trim();
        String accountPassword = password.getText().toString().trim();
        int accountAge = Integer.parseInt(age.getText().toString().trim());
        mAuth.createUserWithEmailAndPassword(accountEmail, accountPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            User user = new User(accountEmail, accountName, accountPassword, accountAge, picLink);

                            FirebaseDatabase.getInstance().getReferenceFromUrl("https://hikergram-default-rtdb.firebaseio.com/Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Toast.makeText(SignUp.this, "User registered", Toast.LENGTH_SHORT).show();
                                                UserListSingleton.getInstance().getUserList().add(user);
                                            }
                                            else Toast.makeText(SignUp.this, "Failed to register! try again!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
        }
    }



