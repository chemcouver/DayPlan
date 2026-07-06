package com.gamecodeschool.dayplan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class CourseActivity extends AppCompatActivity {

    private ListView courseListView;
    private Button addCourseButton;
    private Storage storage;
    private ArrayList<Course> courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        storage = new Storage(this);

        courseListView = findViewById(R.id.courseListView);
        addCourseButton = findViewById(R.id.addCourseButton);

        addCourseButton.setOnClickListener(v ->
                startActivity(new Intent(CourseActivity.this, AddCourseActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCourses();
    }

    private void loadCourses() {
        courses = storage.getAllCourses();

        ArrayAdapter<Course> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                courses
        );

        courseListView.setAdapter(adapter);
    }
}