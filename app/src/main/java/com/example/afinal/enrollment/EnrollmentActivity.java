package com.example.afinal.enrollment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.afinal.R;
import com.example.afinal.displayActivity;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnrollmentActivity extends AppCompatActivity {

    ListView subjectListView;
    Button submitEnrollmentBtn;
    ArrayList<String> selectedSubjects = new ArrayList<>();
    ArrayList<Integer> selectedCredits = new ArrayList<>(); // To keep track of the selected credits
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment);

        subjectListView = findViewById(R.id.subjectListView);
        submitEnrollmentBtn = findViewById(R.id.submitEnrollmentBtn);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("enrollments");

        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject("CS101", "Introduction to Computer Science", 3, "Computer Science"));
        subjects.add(new Subject("CS102", "Data Structures and Algorithms", 4, "Computer Science"));
        subjects.add(new Subject("CS201", "Operating Systems", 4, "Computer Science"));
        subjects.add(new Subject("CS202", "Database Management Systems", 3, "Computer Science"));
        subjects.add(new Subject("CS301", "Software Engineering", 3, "Computer Science"));
        subjects.add(new Subject("CS302", "Computer Networks", 4, "Computer Science"));
        subjects.add(new Subject("CS401", "Machine Learning", 3, "Computer Science"));
        subjects.add(new Subject("CS402", "Artificial Intelligence", 3, "Computer Science"));

        SubjectAdapter adapter = new SubjectAdapter(this, subjects);
        subjectListView.setAdapter(adapter);
        subjectListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        submitEnrollmentBtn.setOnClickListener(v -> {
            selectedSubjects.clear();
            selectedCredits.clear();

            for (int i = 0; i < subjectListView.getCount(); i++) {
                if (subjectListView.isItemChecked(i)) {
                    Subject selectedSubject = subjects.get(i);
                    selectedSubjects.add(selectedSubject.getCode());  // Store code
                    selectedCredits.add(selectedSubject.getCredit()); // Store credit
                }
            }

            int totalCredits = 0;
            for (int credit : selectedCredits) {
                totalCredits += credit;
            }

            if (totalCredits > 24) {
                Toast.makeText(EnrollmentActivity.this, "Total credits cannot exceed 24.", Toast.LENGTH_SHORT).show();
            } else if (selectedSubjects.isEmpty()) {
                Toast.makeText(EnrollmentActivity.this, "Please select at least one subject!", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String userId = sharedPreferences.getString("email", null);

                if (userId != null) {
                    Map<String, Object> subjectMap = new HashMap<>();

                    for (int i = 0; i < selectedSubjects.size(); i++) {
                        Map<String, Object> subjectDetails = new HashMap<>();
                        subjectDetails.put("code", selectedSubjects.get(i));
                        subjectDetails.put("credit", selectedCredits.get(i));

                        // Store each subject under the userId and its corresponding subject code
                        subjectMap.put("subject" + (i + 1), subjectDetails);
                    }

                    reference.child(userId).child("subjects").setValue(subjectMap)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(EnrollmentActivity.this, "Enrollment Successful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EnrollmentActivity.this, displayActivity.class);
                                startActivity(intent);  // Start DisplayActivity
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(EnrollmentActivity.this, "Enrollment Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(EnrollmentActivity.this, "User not logged in. Please log in again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class SubjectAdapter extends ArrayAdapter<Subject> {
        public SubjectAdapter(EnrollmentActivity context, List<Subject> subjects) {
            super(context, 0, subjects);
        }

        @Override
        public View getView(int position, View convertView, android.view.ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
            }

            Subject subject = getItem(position);

            TextView textView = convertView.findViewById(android.R.id.text1);
            textView.setText(subject.getCode() + " - " + subject.getName() + " (" + subject.getCredit() + " credits)");

            return convertView;
        }
    }
}
