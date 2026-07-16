package com.gamecodeschool.dayplan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.*;
import java.util.ArrayList;
import android.graphics.Color;
import android.view.*;

/*
 * Hasan Awada, Student ID 3637019
 *
 * DashboardActivity
 *
 * Description:
 * - Provides the main dashboard and navigation buttons
 * - Displays incomplete tasks grouped under their assigned course (or under general)
 * - Shows due date countdown information for each incomplete task
 * - Allows dashboard tasks to be marked as complete
 */
public class DashboardActivity extends AppCompatActivity {
    private Button viewCoursesButton;
    private Button viewTasksButton;
    private Button addTaskButton;
    private TextView summaryTextView;
    private ListView upcomingTaskListView;
    private Storage storage;
    private ArrayList<Task> upcomingTasks;
    private ArrayList<Object> dashboardItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        storage = new Storage(this);

        viewCoursesButton = findViewById(R.id.viewCoursesButton);
        viewTasksButton = findViewById(R.id.viewTasksButton);
        addTaskButton = findViewById(R.id.addTaskButton);
        upcomingTaskListView = findViewById(R.id.upcomingTaskListView);

        viewCoursesButton.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, CourseActivity.class)));
        viewTasksButton.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, TaskActivity.class)));
        addTaskButton.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, AddTaskActivity.class)));

        upcomingTaskListView.setOnItemClickListener((parent, view, position, id) -> {
            Object item = dashboardItems.get(position);

            if (item instanceof Task) {
                Task selectedTask = (Task) item;
                showCompletionDialog(selectedTask);
            }
        });

        upcomingTaskListView.setOnItemLongClickListener((parent, view, position, id) -> {
            Object item = dashboardItems.get(position);

            if (item instanceof Task) {
                Task selectedTask = (Task) item;
                showCompletionDialog(selectedTask);
            }

            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboard();
    }

    private void loadDashboard() {
        upcomingTasks = storage.getIncompleteTasks();
        dashboardItems = buildDashboardTaskList(upcomingTasks);

        ArrayAdapter<Object> adapter = new ArrayAdapter<Object>(this, 0, dashboardItems) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Object item = getItem(position);

                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_row, parent, false);
                }

                TextView taskTitleTextView = convertView.findViewById(R.id.taskTitleTextView);
                TextView taskDueDateTextView = convertView.findViewById(R.id.taskDueDateTextView);
                TextView taskStatusSymbolTextView = convertView.findViewById(R.id.taskStatusSymbolTextView);

                if (item instanceof String) {
                    String header = (String) item;

                    taskTitleTextView.setText(header);
                    taskDueDateTextView.setText("━━━━━━━━━━━━━━━━━━━━");
                    taskStatusSymbolTextView.setText("");

                    taskTitleTextView.setTextSize(15);
                    taskTitleTextView.setTypeface(null, android.graphics.Typeface.BOLD);

                    taskDueDateTextView.setTextSize(10);
                    taskDueDateTextView.setTypeface(null, android.graphics.Typeface.BOLD);

                } else if (item instanceof Task) {
                    Task task = (Task) item;
                    taskTitleTextView.setText(task.getTaskTitle());
                    taskDueDateTextView.setText(task.getCountdownText());
                    taskDueDateTextView.setTextSize(15);
                    taskDueDateTextView.setTypeface(null, android.graphics.Typeface.NORMAL);
                    taskStatusSymbolTextView.setText("✕");
                    taskStatusSymbolTextView.setTextColor(Color.rgb(200, 0, 0));
                    taskTitleTextView.setTextSize(16);
                    taskTitleTextView.setTypeface(null, android.graphics.Typeface.NORMAL);
                }

                return convertView;
            }
        };

        upcomingTaskListView.setAdapter(adapter);
    }

    private ArrayList<Object> buildDashboardTaskList(ArrayList<Task> tasks) {
        ArrayList<Object> items = new ArrayList<>();

        ArrayList<Task> generalTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.getCourseId() == -1) {
                generalTasks.add(task);
            }
        }

        if (!generalTasks.isEmpty()) {
            items.add("General");

            for (Task task : generalTasks) {
                items.add(task);
            }
        }

        ArrayList<Course> courses = storage.getAllCourses();

        for (Course course : courses) {
            ArrayList<Task> courseTasks = new ArrayList<>();

            for (Task task : tasks) {
                if (task.getCourseId() == course.getCourseId()) {
                    courseTasks.add(task);
                }
            }

            if (!courseTasks.isEmpty()) {
                items.add(course.getCourseName());

                for (Task task : courseTasks) {
                    items.add(task);
                }
            }
        }

        return items;
    }

    private void showCompletionDialog(Task task) {
        new AlertDialog.Builder(this).setTitle("Complete Task").setMessage("Mark this task as complete?").setPositiveButton("Yes", (dialog, which) -> {
            boolean updated = storage.updateTaskCompletion(task.getTaskId(), true);

            if (updated) {
                Toast.makeText(this, "Task marked complete", Toast.LENGTH_SHORT).show();
                loadDashboard();
            } else {
                Toast.makeText(this, "Could not update task", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("Cancel", null).show();
    }
}