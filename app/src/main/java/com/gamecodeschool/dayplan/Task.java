package com.gamecodeschool.dayplan;

public class Task {
    private int taskId;
    private int courseId;
    private String taskTitle;
    private String taskDescription;
    private String dueDate;
    private boolean complete;

    public Task(int taskId, int courseId, String taskTitle, String taskDescription, String dueDate, boolean complete) {
        this.taskId = taskId;
        this.courseId = courseId;
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.dueDate = dueDate;
        this.complete = complete;
    }

    public Task(int courseId, String taskTitle, String taskDescription, String dueDate, boolean complete) {
        this.courseId = courseId;
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.dueDate = dueDate;
        this.complete = complete;
    }

    public int getTaskId() {
        return taskId;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public String getDueDate() {
        return dueDate;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    @Override
    public String toString() {
        String status = complete ? "Complete" : "Incomplete";
        return taskTitle + " - Due: " + dueDate + " - " + status;
    }
}