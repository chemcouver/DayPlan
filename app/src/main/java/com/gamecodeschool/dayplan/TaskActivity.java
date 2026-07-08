package com.gamecodeschool.dayplan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.TextView;

public class TaskActivity extends AppCompatActivity {
    private ListView taskListView;
    private Button addTaskButton;
    private Storage storage;
    private ArrayList<Task> tasks;
    private ArrayAdapter<Task> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        storage = new Storage(this);

        taskListView = findViewById(R.id.taskListView);
        addTaskButton = findViewById(R.id.addTaskButton);
        addTaskButton.setOnClickListener(v -> startActivity(new Intent(TaskActivity.this, AddTaskActivity.class)));

        taskListView.setOnItemClickListener((parent, view, position, id) -> {
            Task selectedTask = tasks.get(position);
            showTaskDetails(selectedTask);
        });

        taskListView.setOnItemLongClickListener((parent, view, position, id) -> {
            Task selectedTask = tasks.get(position);
            showTaskOptions(selectedTask);
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    private void loadTasks() {
        tasks = storage.getAllTasks();

        adapter = new ArrayAdapter<Task>(this, 0, tasks) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Task task = getItem(position);

                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(
                            R.layout.task_row,
                            parent,
                            false
                    );
                }

                TextView taskTitleTextView = convertView.findViewById(R.id.taskTitleTextView);
                TextView taskDueDateTextView = convertView.findViewById(R.id.taskDueDateTextView);
                TextView taskStatusSymbolTextView = convertView.findViewById(R.id.taskStatusSymbolTextView);

                if (task != null) {
                    taskTitleTextView.setText(task.getTaskTitle());

                    if (task.isComplete()) {
                        taskDueDateTextView.setText("Complete");
                        taskStatusSymbolTextView.setText("✓");
                        taskStatusSymbolTextView.setTextColor(Color.rgb(0, 150, 0));
                    } else {
                        taskDueDateTextView.setText(task.getCountdownText());
                        taskStatusSymbolTextView.setText("✕");
                        taskStatusSymbolTextView.setTextColor(Color.rgb(200, 0, 0));
                    }
                }

                return convertView;
            }
        };

        taskListView.setAdapter(adapter);
    }

    private void showTaskDetails(Task task) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_task_details, null);

        TextView taskTitleTextView = view.findViewById(R.id.taskTitleTextView);
        TextView taskDueDateTextView = view.findViewById(R.id.taskDueDateTextView);
        TextView taskDescriptionTextView = view.findViewById(R.id.taskDescriptionTextView);
        TextView courseTitleTextView = view.findViewById(R.id.courseTitleTextView);
        TextView courseDescriptionTextView = view.findViewById(R.id.courseDescriptionTextView);
        Button toggleCompleteButton = view.findViewById(R.id.toggleCompleteButton);
        Button closeButton = view.findViewById(R.id.closeButton);

        taskTitleTextView.setText(task.getTaskTitle());
        taskDueDateTextView.setText(task.getCountdownText());

        String taskDescription = task.getTaskDescription();

        if (taskDescription == null || taskDescription.trim().isEmpty()) {
            taskDescriptionTextView.setText("No task description added.");
        } else {
            taskDescriptionTextView.setText(taskDescription);
        }

        Course course = storage.getCourseById(task.getCourseId());

        if (course == null) {
            courseTitleTextView.setText("Course not found");
            courseDescriptionTextView.setText("");
        } else {
            courseTitleTextView.setText(course.getCourseName());

            String courseDescription = course.getCourseDescription();

            if (courseDescription == null || courseDescription.trim().isEmpty()) {
                courseDescriptionTextView.setText("No course description added.");
            } else {
                courseDescriptionTextView.setText(courseDescription);
            }
        }

        if (task.isComplete()) {
            toggleCompleteButton.setText("Mark Incomplete");
        } else {
            toggleCompleteButton.setText("Mark Complete");
        }

        toggleCompleteButton.setOnClickListener(v -> {
            boolean updated = storage.updateTaskCompletion(
                    task.getTaskId(),
                    !task.isComplete()
            );

            if (updated) {
                Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
                loadTasks();
            } else {
                Toast.makeText(this, "Could not update task", Toast.LENGTH_SHORT).show();
            }
        });

        closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private void showTaskOptions(Task task) {
        String[] options = {"Edit Task", "Delete Task"};

        new AlertDialog.Builder(this)
                .setTitle(task.getTaskTitle())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        editTask(task);
                    } else if (which == 1) {
                        confirmDeleteTask(task);
                    }
                })
                .show();
    }

    private void editTask(Task task) {
        Intent intent = new Intent(TaskActivity.this, AddTaskActivity.class);

        intent.putExtra("taskId", task.getTaskId());
        intent.putExtra("courseId", task.getCourseId());
        intent.putExtra("taskTitle", task.getTaskTitle());
        intent.putExtra("taskDescription", task.getTaskDescription());
        intent.putExtra("dueDate", task.getDueDate());
        intent.putExtra("complete", task.isComplete());

        startActivity(intent);
    }

    private void confirmDeleteTask(Task task) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Delete " + task.getTaskTitle() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean deleted = storage.deleteTask(task.getTaskId());

                    if (deleted) {
                        Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
                        loadTasks();
                    } else {
                        Toast.makeText(this, "Could not delete task", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showCompletionDialog(Task task) {
        String newStatus = task.isComplete() ? "incomplete" : "complete";

        new AlertDialog.Builder(this).setTitle("Update Task").setMessage("Mark this task as " + newStatus + "?").setPositiveButton("Yes", (dialog, which) -> {
            boolean updated = storage.updateTaskCompletion(
                    task.getTaskId(),
                    !task.isComplete()
            );

            if (updated) {
                Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();
                loadTasks();
            } else {
                Toast.makeText(this, "Could not update task", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("Cancel", null).show();
    }
}