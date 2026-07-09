package com.gamecodeschool.dayplan;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.*;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.*;

public class CourseActivity extends AppCompatActivity {
    private ListView courseListView;
    private Button addCourseButton;
    private Storage storage;
    private ArrayList<Course> courses;
    private ArrayAdapter<Course> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        storage = new Storage(this);
        courseListView = findViewById(R.id.courseListView);
        addCourseButton = findViewById(R.id.addCourseButton);

        addCourseButton.setOnClickListener(v -> startActivity(new Intent(CourseActivity.this, AddCourseActivity.class)));

        courseListView.setOnItemClickListener((parent, view, position, id) -> {
            Course selectedCourse = courses.get(position);
            showCourseDetails(selectedCourse);
        });

        courseListView.setOnItemLongClickListener((parent, view, position, id) -> {
            Course selectedCourse = courses.get(position);
            showCourseOptions(selectedCourse);
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCourses();
    }

    private void loadCourses() {
        courses = storage.getAllCourses();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, courses);
        courseListView.setAdapter(adapter);
    }

    private void showCourseDetails(Course course) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_course_details, null);
        TextView courseTitleTextView = view.findViewById(R.id.courseTitleTextView);
        TextView courseDescriptionTextView = view.findViewById(R.id.courseDescriptionTextView);
        Button closeButton = view.findViewById(R.id.closeButton);

        courseTitleTextView.setText(course.getCourseName());
        String description = course.getCourseDescription();

        if (description == null || description.trim().isEmpty()) {
            courseDescriptionTextView.setText("No description added.");
        } else {
            courseDescriptionTextView.setText(description);
        }

        closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private void showCourseOptions(Course course) {
        String[] options = {"Edit Course", "Delete Course"};

        new AlertDialog.Builder(this).setTitle(course.getCourseName()).setItems(options, (dialog, which) -> {
            if (which == 0) {
                editCourse(course);
            } else if (which == 1) {
                confirmDeleteCourse(course);
            }
        }).show();
    }

    private void editCourse(Course course) {
        Intent intent = new Intent(CourseActivity.this, AddCourseActivity.class);
        intent.putExtra("courseId", course.getCourseId());
        intent.putExtra("courseName", course.getCourseName());
        intent.putExtra("courseDescription", course.getCourseDescription());
        startActivity(intent);
    }

    private void confirmDeleteCourse(Course course) {
        new AlertDialog.Builder(this).setTitle("Delete Course").setMessage("Delete " + course.getCourseName() + "? Tasks assigned to this course will also be deleted.").setPositiveButton("Delete", (dialog, which) -> {
            boolean deleted = storage.deleteCourse(course.getCourseId());

            if (deleted) {
                Toast.makeText(this, "Course deleted", Toast.LENGTH_SHORT).show();
                loadCourses();
            } else {
                Toast.makeText(this, "Could not delete course", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("Cancel", null).show();
    }
}