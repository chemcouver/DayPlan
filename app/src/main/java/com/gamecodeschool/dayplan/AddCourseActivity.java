package com.gamecodeschool.dayplan;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

/*
 * Hasan Awada, Student ID 3637019
 *
 * AddCourseActivity
 *
 * Description:
 * - Provides the form used to create or edit a course
 * - Validates that a course name has been entered
 * - Saves new courses or updates existing courses through Storage
 * - Displays confirmation or error feedback after saving
 */
public class AddCourseActivity extends AppCompatActivity {
    private EditText courseNameInput;
    private EditText courseDescriptionInput;
    private Button saveCourseButton;
    private Storage storage;
    private boolean editMode = false;
    private int courseId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        storage = new Storage(this);

        courseNameInput = findViewById(R.id.courseNameInput);
        courseDescriptionInput = findViewById(R.id.courseDescriptionInput);
        saveCourseButton = findViewById(R.id.saveCourseButton);

        checkForEditMode();
        saveCourseButton.setOnClickListener(v -> saveCourse());
    }

    private void checkForEditMode() {
        if (getIntent().hasExtra("courseId")) {
            editMode = true;

            courseId = getIntent().getIntExtra("courseId", -1);
            String courseName = getIntent().getStringExtra("courseName");
            String courseDescription = getIntent().getStringExtra("courseDescription");

            courseNameInput.setText(courseName);
            courseDescriptionInput.setText(courseDescription);
            saveCourseButton.setText("Update Course");
        }
    }

    private void saveCourse() {
        String name = courseNameInput.getText().toString().trim();
        String description = courseDescriptionInput.getText().toString().trim();

        if (name.isEmpty()) {
            courseNameInput.setError("Course name is required");
            return;
        }

        boolean saved;

        if (editMode) {
            saved = storage.updateCourse(courseId, name, description);
        } else {
            Course course = new Course(name, description);
            saved = storage.createCourse(course);
        }

        if (saved) {
            String message = editMode ? "Course updated successfully" : "Course saved successfully";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving course", Toast.LENGTH_SHORT).show();
        }
    }
}