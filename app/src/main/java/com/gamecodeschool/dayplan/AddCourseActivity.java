package com.gamecodeschool.dayplan;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddCourseActivity extends AppCompatActivity {

    private EditText courseNameInput;
    private EditText courseDescriptionInput;
    private Button saveCourseButton;
    private Storage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        storage = new Storage(this);

        courseNameInput = findViewById(R.id.courseNameInput);
        courseDescriptionInput = findViewById(R.id.courseDescriptionInput);
        saveCourseButton = findViewById(R.id.saveCourseButton);

        saveCourseButton.setOnClickListener(v -> saveCourse());
    }

    private void saveCourse() {
        String name = courseNameInput.getText().toString().trim();
        String description = courseDescriptionInput.getText().toString().trim();

        if (name.isEmpty()) {
            courseNameInput.setError("Course name is required");
            return;
        }

        Course course = new Course(name, description);
        boolean saved = storage.createCourse(course);

        if (saved) {
            Toast.makeText(this, "Course saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving course", Toast.LENGTH_SHORT).show();
        }
    }
}