package com.example.hikergram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText resetEmailET;
    Button sendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mAuth = FirebaseAuth.getInstance();
        resetEmailET = findViewById(R.id.resetEmail);
        sendEmail = findViewById(R.id.sendEmail);
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = resetEmailET.getText().toString();

                        // Check if the email address is valid
                        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                resetEmailET.setError("Please enter a valid email!");
                                resetEmailET.requestFocus();
                                return;
                        }
                        mAuth.sendPasswordResetEmail(email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ForgotPassword.this, "Password reset email sent.", Toast.LENGTH_SHORT).show();
                                            // show a toast to the user to check their email
                                        } else {
                                            Toast.makeText(ForgotPassword.this, "Failed to send password reset email.", Toast.LENGTH_SHORT).show();
                                            // show an error message to the user
                                        }
                                    }
                                });
                    }
                });

            }
        });


    }



}
