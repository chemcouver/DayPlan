package com.gamecodeschool.dayplan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Task {
    private int taskId;
    private final int courseId;
    private final String taskTitle;
    private final String taskDescription;
    private final String dueDate;
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
        return taskTitle + "\n" + getCountdownText();
    }

    public String getCountdownText() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            Calendar dueCalendar = Calendar.getInstance();
            dueCalendar.setTime(Objects.requireNonNull(format.parse(dueDate)));

            Calendar today = Calendar.getInstance();

            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            dueCalendar.set(Calendar.HOUR_OF_DAY, 0);
            dueCalendar.set(Calendar.MINUTE, 0);
            dueCalendar.set(Calendar.SECOND, 0);
            dueCalendar.set(Calendar.MILLISECOND, 0);

            long difference = dueCalendar.getTimeInMillis() - today.getTimeInMillis();
            long days = difference / (1000 * 60 * 60 * 24);

            if (days == 0) {
                return "Today";
            } else if (days == 1) {
                return "Tomorrow";
            } else if (days > 1) {
                return "In " + days + " days";
            } else if (days == -1) {
                return "Overdue by 1 day";
            } else {
                return "Overdue by " + Math.abs(days) + " days";
            }

        } catch (ParseException e) {
            return dueDate;
        }
    }
}