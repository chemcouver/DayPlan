package com.gamecodeschool.dayplan;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import java.util.*;

public class Storage extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dayplan.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_COURSES = "courses";
    private static final String COURSE_ID = "course_id";
    private static final String COURSE_NAME = "course_name";
    private static final String COURSE_DESCRIPTION = "course_description";
    private static final String TABLE_TASKS = "tasks";
    private static final String TASK_ID = "task_id";
    private static final String TASK_COURSE_ID = "course_id";
    private static final String TASK_TITLE = "task_title";
    private static final String TASK_DESCRIPTION = "task_description";
    private static final String TASK_DUE_DATE = "due_date";
    private static final String TASK_COMPLETE = "is_complete";

    public Storage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCoursesTable = "CREATE TABLE " + TABLE_COURSES + " (" + COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COURSE_NAME + " TEXT NOT NULL, " + COURSE_DESCRIPTION + " TEXT" + ");";
        String createTasksTable = "CREATE TABLE " + TABLE_TASKS + " (" + TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK_COURSE_ID + " INTEGER, " + TASK_TITLE + " TEXT NOT NULL, " + TASK_DESCRIPTION + " TEXT, " + TASK_DUE_DATE + " TEXT, " + TASK_COMPLETE + " INTEGER DEFAULT 0, " + "FOREIGN KEY(" + TASK_COURSE_ID + ") REFERENCES " + TABLE_COURSES + "(" + COURSE_ID + ")" + ");";

        db.execSQL(createCoursesTable);
        db.execSQL(createTasksTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        onCreate(db);
    }

    public boolean createCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COURSE_NAME, course.getCourseName());
        values.put(COURSE_DESCRIPTION, course.getCourseDescription());

        long result = db.insert(TABLE_COURSES, null, values);
        return result != -1;
    }

    public ArrayList<Course> getAllCourses() {
        ArrayList<Course> courses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_COURSES + " ORDER BY " + COURSE_NAME + " ASC", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COURSE_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_NAME));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_DESCRIPTION));

                courses.add(new Course(id, name, description));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return courses;
    }

    public Course getCourseById(int courseId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_COURSES + " WHERE " + COURSE_ID + " = ?",
                new String[]{String.valueOf(courseId)}
        );

        Course course = null;

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COURSE_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_NAME));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_DESCRIPTION));

            course = new Course(id, name, description);
        }

        cursor.close();
        return course;
    }

    public boolean updateCourse(int courseId, String name, String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COURSE_NAME, name);
        values.put(COURSE_DESCRIPTION, description);

        int rows = db.update(TABLE_COURSES, values, COURSE_ID + " = ?", new String[]{String.valueOf(courseId)});
        return rows > 0;
    }

    public boolean deleteCourse(int courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, TASK_COURSE_ID + " = ?", new String[]{String.valueOf(courseId)});
        int rows = db.delete(TABLE_COURSES, COURSE_ID + " = ?", new String[]{String.valueOf(courseId)});
        return rows > 0;
    }

    public boolean createTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if (task.getCourseId() == -1) {
            values.putNull(TASK_COURSE_ID);
        } else {
            values.put(TASK_COURSE_ID, task.getCourseId());
        }
        values.put(TASK_TITLE, task.getTaskTitle());
        values.put(TASK_DESCRIPTION, task.getTaskDescription());
        values.put(TASK_DUE_DATE, task.getDueDate());
        values.put(TASK_COMPLETE, task.isComplete() ? 1 : 0);

        long result = db.insert(TABLE_TASKS, null, values);
        return result != -1;
    }

    public boolean updateTask(int taskId, int courseId, String title, String description, String dueDate, boolean complete) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if (courseId == -1) {
            values.putNull(TASK_COURSE_ID);
        } else {
            values.put(TASK_COURSE_ID, courseId);
        }
        values.put(TASK_TITLE, title);
        values.put(TASK_DESCRIPTION, description);
        values.put(TASK_DUE_DATE, dueDate);
        values.put(TASK_COMPLETE, complete ? 1 : 0);

        int rows = db.update(TABLE_TASKS, values, TASK_ID + " = ?", new String[]{String.valueOf(taskId)});
        return rows > 0;
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS + " ORDER BY " + TASK_DUE_DATE + " ASC", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_ID));
                int courseIdColumn = cursor.getColumnIndexOrThrow(TASK_COURSE_ID);
                int courseId = cursor.isNull(courseIdColumn) ? -1 : cursor.getInt(courseIdColumn);
                String title = cursor.getString(cursor.getColumnIndexOrThrow(TASK_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DESCRIPTION));
                String dueDate = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DUE_DATE));
                boolean complete = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_COMPLETE)) == 1;

                tasks.add(new Task(id, courseId, title, description, dueDate, complete));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return tasks;
    }

    public ArrayList<Task> getIncompleteTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS + " WHERE " + TASK_COMPLETE + " = 0" + " ORDER BY " + TASK_DUE_DATE + " ASC", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_ID));
                int courseIdColumn = cursor.getColumnIndexOrThrow(TASK_COURSE_ID);
                int courseId = cursor.isNull(courseIdColumn) ? -1 : cursor.getInt(courseIdColumn);
                String title = cursor.getString(cursor.getColumnIndexOrThrow(TASK_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DESCRIPTION));
                String dueDate = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DUE_DATE));
                boolean complete = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_COMPLETE)) == 1;

                tasks.add(new Task(id, courseId, title, description, dueDate, complete));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return tasks;
    }

    public ArrayList<Task> getCompletedTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS + " WHERE " + TASK_COMPLETE + " = 1" + " ORDER BY " + TASK_DUE_DATE + " ASC", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_ID));
                int courseIdColumn = cursor.getColumnIndexOrThrow(TASK_COURSE_ID);
                int courseId = cursor.isNull(courseIdColumn) ? -1 : cursor.getInt(courseIdColumn);
                String title = cursor.getString(cursor.getColumnIndexOrThrow(TASK_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DESCRIPTION));
                String dueDate = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DUE_DATE));
                boolean complete = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_COMPLETE)) == 1;

                tasks.add(new Task(id, courseId, title, description, dueDate, complete));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return tasks;
    }

    public ArrayList<Task> getUpcomingIncompleteTasks(int limit) {
        ArrayList<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS + " WHERE " + TASK_COMPLETE + " = 0" + " ORDER BY " + TASK_DUE_DATE + " ASC" + " LIMIT " + limit, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_ID));
                int courseIdColumn = cursor.getColumnIndexOrThrow(TASK_COURSE_ID);
                int courseId = cursor.isNull(courseIdColumn) ? -1 : cursor.getInt(courseIdColumn);
                String title = cursor.getString(cursor.getColumnIndexOrThrow(TASK_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DESCRIPTION));
                String dueDate = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DUE_DATE));
                boolean complete = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_COMPLETE)) == 1;

                tasks.add(new Task(id, courseId, title, description, dueDate, complete));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return tasks;
    }

    public int getTaskCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TASKS, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();

        return count;
    }

    public int getCompletedTaskCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TASKS + " WHERE " + TASK_COMPLETE + " = 1", null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();

        return count;
    }

    public int getIncompleteTaskCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TASKS + " WHERE " + TASK_COMPLETE + " = 0", null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        return count;
    }

    public boolean updateTaskCompletion(int taskId, boolean complete) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TASK_COMPLETE, complete ? 1 : 0);

        int rows = db.update(TABLE_TASKS, values, TASK_ID + " = ?", new String[]{String.valueOf(taskId)});

        return rows > 0;
    }

    public boolean deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_TASKS, TASK_ID + " = ?", new String[]{String.valueOf(taskId)});

        return rows > 0;
    }
}