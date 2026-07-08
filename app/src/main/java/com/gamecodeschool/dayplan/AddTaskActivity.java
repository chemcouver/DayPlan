package com.gamecodeschool.dayplan;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import android.app.DatePickerDialog;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {
    private EditText taskTitleInput;
    private EditText taskDescriptionInput;
    private EditText dueDateInput;
    private Spinner courseSpinner;
    private Storage storage;
    private ArrayList<Course> courses;
    private Button selectDateButton;
    private TextView selectedDateTextView;
    private String selectedDueDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        storage = new Storage(this);

        taskTitleInput = findViewById(R.id.taskTitleInput);
        taskDescriptionInput = findViewById(R.id.taskDescriptionInput);
        courseSpinner = findViewById(R.id.courseSpinner);
        Button saveTaskButton = findViewById(R.id.saveTaskButton);
        selectDateButton = findViewById(R.id.selectDateButton);
        selectedDateTextView = findViewById(R.id.selectedDateTextView);

        loadCoursesIntoSpinner();

        selectDateButton.setOnClickListener(v -> showDatePicker());
        saveTaskButton.setOnClickListener(v -> saveTask());
    }

    private void loadCoursesIntoSpinner() {
        courses = storage.getAllCourses();

        ArrayAdapter<Course> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(adapter);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year, month, dayOfMonth);

            SimpleDateFormat databaseFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            selectedDueDate = databaseFormat.format(selectedCalendar.getTime());
            selectedDateTextView.setText("Due: " + getCountdownText(selectedCalendar));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private String getCountdownText(Calendar selectedCalendar) {
        Calendar today = Calendar.getInstance();

        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        selectedCalendar.set(Calendar.HOUR_OF_DAY, 0);
        selectedCalendar.set(Calendar.MINUTE, 0);
        selectedCalendar.set(Calendar.SECOND, 0);
        selectedCalendar.set(Calendar.MILLISECOND, 0);

        long difference = selectedCalendar.getTimeInMillis() - today.getTimeInMillis();
        long days = difference / (1000 * 60 * 60 * 24);

        if (days == 0) {
            return "Today";
        } else if (days == 1) {
            return "Tomorrow";
        } else {
            return "In " + days + " days";
        }
    }

    private void saveTask() {
        String title = taskTitleInput.getText().toString().trim();
        String description = taskDescriptionInput.getText().toString().trim();

        if (title.isEmpty()) {
            taskTitleInput.setError("Task title is required");
            return;
        }

        if (selectedDueDate.isEmpty()) {
            Toast.makeText(this, "Please select a due date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (courses.isEmpty()) {
            Toast.makeText(this, "Please create a course first", Toast.LENGTH_SHORT).show();
            return;
        }

        Course selectedCourse = (Course) courseSpinner.getSelectedItem();

        Task task = new Task(selectedCourse.getCourseId(), title, description, selectedDueDate, false);
        boolean saved = storage.createTask(task);

        if (saved) {
            Toast.makeText(this, "Task saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving task", Toast.LENGTH_SHORT).show();
        }
    }


}