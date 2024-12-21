package com.example.afinal;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class displayActivity extends AppCompatActivity {

    ListView selectedSubjectsListView;
    FirebaseDatabase database;
    DatabaseReference reference;
    ArrayAdapter<String> adapter;
    ArrayList<String> selectedSubjects;
    TextView totalCreditsTextView;  // TextView to display total credits

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        selectedSubjectsListView = findViewById(R.id.selectedSubjectsListView);
        totalCreditsTextView = findViewById(R.id.totalCreditsTextView); // Initialize the TextView
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("enrollments");
        selectedSubjects = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, selectedSubjects);

        selectedSubjectsListView.setAdapter(adapter);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("email", null); // Assuming "email" is the key

        if (userId != null) {
            reference.child(userId).child("subjects").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    selectedSubjects.clear();
                    int totalCredits = 0;

                    for (DataSnapshot subjectSnapshot : snapshot.getChildren()) {
                        // Extract subject details
                        Map<String, Object> subjectDetails = (Map<String, Object>) subjectSnapshot.getValue();
                        String code = (String) subjectDetails.get("code");
                        Long creditLong = (Long) subjectDetails.get("credit");

                        int credit = creditLong.intValue();

                        String subjectInfo = code + " - " + credit + " credits";
                        selectedSubjects.add(subjectInfo);

                        totalCredits += credit;
                    }

                    adapter.notifyDataSetChanged();

                    if (selectedSubjects.isEmpty()) {
                        Toast.makeText(displayActivity.this, "No subjects found.", Toast.LENGTH_SHORT).show();
                    }

                    // Display the total credits in the TextView
                    totalCreditsTextView.setText("Total Credits: " + totalCredits);
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
