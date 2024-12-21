package com.example.afinal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class loginActivity extends AppCompatActivity {

    EditText loginEmail, loginPass;
    Button loginBtn;
    TextView signupRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.email_login); // Changed to loginEmail
        loginPass = findViewById(R.id.pass_login);
        loginBtn = findViewById(R.id.login_Btn);
        signupRedirect = findViewById(R.id.signup_redirect);

        loginBtn.setOnClickListener(view -> {
            if (!validateEmail() | !validatePassword()) {
                // Validation failed
            } else {
                checkUser();
            }
        });

        signupRedirect.setOnClickListener(view -> {
            Intent intent = new Intent(loginActivity.this, signupActivity.class);
            startActivity(intent);
        });
    }

    public boolean validateEmail() {
        String val = loginEmail.getText().toString().trim();

        if (val.isEmpty()) {
            loginEmail.setError("Email cannot be empty");
            return false;
        } else {
            loginEmail.setError(null);
            return true;
        }
    }

    public boolean validatePassword() {
        String val = loginPass.getText().toString().trim();

        if (val.isEmpty()) {
            loginPass.setError("Password cannot be empty");
            return false;
        } else {
            loginPass.setError(null);
            return true;
        }
    }

    public void checkUser() {
        String userEmail = loginEmail.getText().toString().trim().replace(".", ","); // Sanitize email for Firebase
        String userPass = loginPass.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.child(userEmail);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String passwordFromDB = snapshot.child("password").getValue(String.class);

                    if (passwordFromDB != null && passwordFromDB.equals(userPass)) {
                        loginEmail.setError(null);

                        // Store email in SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", userEmail);
                        editor.apply();

                        Intent intent = new Intent(loginActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        loginPass.setError("Invalid credentials");
                        loginPass.requestFocus();
                    }
                } else {
                    loginEmail.setError("Email does not exist.");
                    loginEmail.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(loginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

