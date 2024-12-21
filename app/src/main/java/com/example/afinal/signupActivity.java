package com.example.afinal;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signupActivity extends AppCompatActivity {

    EditText emailSignup, passSignup;
    Button signupBtn;
    TextView redirectText;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        emailSignup = findViewById(R.id.email_signup);
        passSignup = findViewById(R.id.pass_signup);
        signupBtn = findViewById(R.id.signup_Btn);
        redirectText = findViewById(R.id.direct_text);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                String email = emailSignup.getText().toString().trim();
                String password = passSignup.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(signupActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidEmail(email)) {
                    Toast.makeText(signupActivity.this, "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String sanitizedEmail = email.replace(".", ",");

                HelperClass helperClass = new HelperClass(email, password);

                reference.child(sanitizedEmail).setValue(helperClass)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(signupActivity.this, "You have signed up successfully!", Toast.LENGTH_SHORT).show();

                            // Redirect to login activity
                            Intent intent = new Intent(signupActivity.this, loginActivity.class);
                            startActivity(intent);
                        })
                        .addOnFailureListener(error -> {
                            Toast.makeText(signupActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        redirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(signupActivity.this, loginActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

