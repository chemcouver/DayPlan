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

        addTaskButton.setOnClickListener(v ->
                startActivity(new Intent(TaskActivity.this, AddTaskActivity.class))
        );

        taskListView.setOnItemClickListener((parent, view, position, id) -> {
            Task selectedTask = tasks.get(position);
            showCompletionDialog(selectedTask);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    private void loadTasks() {
        tasks = storage.getAllTasks();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                tasks
        );

        taskListView.setAdapter(adapter);
    }

    private void showCompletionDialog(Task task) {
        String newStatus = task.isComplete() ? "incomplete" : "complete";

        new AlertDialog.Builder(this)
                .setTitle("Update Task")
                .setMessage("Mark this task as " + newStatus + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
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
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}