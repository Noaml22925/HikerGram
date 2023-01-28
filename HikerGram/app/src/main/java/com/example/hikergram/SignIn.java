package com.example.hikergram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignIn extends AppCompatActivity {
    private Button login;
    private TextView signUp, forgotPassword;
    private EditText email, password;
    private SharedPreferences sp;
    private FirebaseAuth mAuth;
    private CheckBox checkbox;
    private String str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        checkbox = findViewById(R.id.checkbox);
        forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignIn.this, ForgotPassword.class));
            }
        });
        sp = getSharedPreferences("details", MODE_PRIVATE);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("email", email.getText().toString());
                editor.putString("password", password.getText().toString());
                editor.putBoolean("stayConnected", b);
                editor.commit();


            }
        });

        if (sp.getBoolean("stayConnected", false)) {
            email.setText(sp.getString("email", " "));
            password.setText(sp.getString("password", " "));
            checkbox.setChecked(true);
        }
        login = findViewById(R.id.login);
        signUp = findViewById(R.id.signUp);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validations()) {
                    userLogin();
                }
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    private void userLogin() {
        String emailString = email.getText().toString().trim();
        String passwordString = password.getText().toString().trim();

        if (emailString.isEmpty() || passwordString.isEmpty())
            Toast.makeText(SignIn.this, "Please fill out all the fields", Toast.LENGTH_SHORT).show();
        if (!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            email.setError("Please enter a valid email!");
            email.requestFocus();
            return;
        }
        if (passwordString.length() < 6)
            password.setError("Password must contain 6 characters!");
        if (!emailString.isEmpty() && !passwordString.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailString).matches() && passwordString.length() >= 6) {
            mAuth.signInWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(SignIn.this, UserProfile.class);
                        startActivity(intent);
                    } else
                        Toast.makeText(SignIn.this, "Email and Password don't match", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private boolean validations() {
        String emailString = email.getText().toString().trim();
        String passwordString = password.getText().toString().trim();
        if (emailString.isEmpty() || passwordString.isEmpty())
            Toast.makeText(SignIn.this, "Please fill out all the fields", Toast.LENGTH_SHORT).show();
        if (!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            email.setError("Please enter a valid email!");
            email.requestFocus();
        }
        if (passwordString.length() < 6)
            password.setError("Password must contain 6 characters!");
        if (!emailString.isEmpty() && !passwordString.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailString).matches() && passwordString.length() >= 6)
            return true;
        return false;
    }

}