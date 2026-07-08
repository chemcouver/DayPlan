package com.gamecodeschool.dayplan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

public class DashboardActivity extends AppCompatActivity {
    private Button viewCoursesButton;
    private Button viewTasksButton;
    private Button addTaskButton;
    private TextView summaryTextView;
    private ListView upcomingTaskListView;
    private Storage storage;
    private ArrayList<Task> upcomingTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        storage = new Storage(this);

        viewCoursesButton = findViewById(R.id.viewCoursesButton);
        viewTasksButton = findViewById(R.id.viewTasksButton);
        addTaskButton = findViewById(R.id.addTaskButton);

        summaryTextView = findViewById(R.id.summaryTextView);
        upcomingTaskListView = findViewById(R.id.upcomingTaskListView);

        viewCoursesButton.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, CourseActivity.class)));
        viewTasksButton.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, TaskActivity.class)));
        addTaskButton.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, AddTaskActivity.class)));

        upcomingTaskListView.setOnItemClickListener((parent, view, position, id) -> {
            Task selectedTask = upcomingTasks.get(position);
            showCompletionDialog(selectedTask);
        });

        upcomingTaskListView.setOnItemLongClickListener((parent, view, position, id) -> {
            Task selectedTask = upcomingTasks.get(position);
            showCompletionDialog(selectedTask);
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboard();
    }

    private void loadDashboard() {
        int totalTasks = storage.getTaskCount();
        int completedTasks = storage.getCompletedTaskCount();
        int incompleteTasks = storage.getIncompleteTaskCount();

        String summary = "Weekly Summary\n\n" + "Total tasks: " + totalTasks + "\n" + "Completed tasks: " + completedTasks + "\n" + "Incomplete tasks: " + incompleteTasks;
        summaryTextView.setText(summary);

        upcomingTasks = storage.getUpcomingIncompleteTasks(5);
        ArrayAdapter<Task> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, upcomingTasks);
        upcomingTaskListView.setAdapter(adapter);
    }

    private void showCompletionDialog(Task task) {
        new AlertDialog.Builder(this).setTitle("Complete Task").setMessage("Mark this task as complete?").setPositiveButton("Yes", (dialog, which) -> {
            boolean updated = storage.updateTaskCompletion(
                    task.getTaskId(),
                    true
            );

            if (updated) {
                Toast.makeText(this, "Task marked complete", Toast.LENGTH_SHORT).show();
                loadDashboard();
            } else {
                Toast.makeText(this, "Could not update task", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("Cancel", null).show();
    }
}