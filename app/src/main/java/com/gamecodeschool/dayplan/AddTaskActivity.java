package com.gamecodeschool.dayplan;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class AddTaskActivity extends AppCompatActivity {
    private EditText taskTitleInput;
    private EditText taskDescriptionInput;
    private EditText dueDateInput;
    private Spinner courseSpinner;
    private Button saveTaskButton;
    private Storage storage;
    private ArrayList<Course> courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        storage = new Storage(this);

        taskTitleInput = findViewById(R.id.taskTitleInput);
        taskDescriptionInput = findViewById(R.id.taskDescriptionInput);
        dueDateInput = findViewById(R.id.dueDateInput);
        courseSpinner = findViewById(R.id.courseSpinner);
        saveTaskButton = findViewById(R.id.saveTaskButton);

        loadCoursesIntoSpinner();

        saveTaskButton.setOnClickListener(v -> saveTask());
    }

    private void loadCoursesIntoSpinner() {
        courses = storage.getAllCourses();

        ArrayAdapter<Course> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                courses
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(adapter);
    }

    private void saveTask() {
        String title = taskTitleInput.getText().toString().trim();
        String description = taskDescriptionInput.getText().toString().trim();
        String dueDate = dueDateInput.getText().toString().trim();

        if (title.isEmpty()) {
            taskTitleInput.setError("Task title is required");
            return;
        }

        if (dueDate.isEmpty()) {
            dueDateInput.setError("Due date is required");
            return;
        }

        if (!isValidDateFormat(dueDate)) {
            dueDateInput.setError("Use date format YYYY-MM-DD");
            return;
        }

        if (courses.isEmpty()) {
            Toast.makeText(this, "Please create a course first", Toast.LENGTH_SHORT).show();
            return;
        }

        Course selectedCourse = (Course) courseSpinner.getSelectedItem();

        Task task = new Task(
                selectedCourse.getCourseId(),
                title,
                description,
                dueDate,
                false
        );

        boolean saved = storage.createTask(task);

        if (saved) {
            Toast.makeText(this, "Task saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving task", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidDateFormat(String date) {
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }
}