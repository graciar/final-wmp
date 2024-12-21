package com.example.afinal;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class displayActivity extends AppCompatActivity {

    ListView selectedSubjectsListView;
    FirebaseDatabase database;
    DatabaseReference reference;
    ArrayAdapter<String> adapter;
    ArrayList<String> selectedSubjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_display);

        selectedSubjectsListView = findViewById(R.id.selectedSubjectsListView);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("enrollments");
        selectedSubjects = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, selectedSubjects);

        selectedSubjectsListView.setAdapter(adapter);

        // Retrieve user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("email", null); // Assuming "email" is the key

        if (userId != null) {
            // Fetch data from Firebase
            reference.child(userId).child("subjects").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    selectedSubjects.clear();
                    for (DataSnapshot subjectSnapshot : snapshot.getChildren()) {
                        String subject = subjectSnapshot.getValue(String.class);
                        selectedSubjects.add(subject);
                    }
                    adapter.notifyDataSetChanged();

                    if (selectedSubjects.isEmpty()) {
                        Toast.makeText(displayActivity.this, "No subjects found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(displayActivity.this, "Failed to load subjects: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "User not logged in. Please log in again.", Toast.LENGTH_SHORT).show();
        }
    }
}
