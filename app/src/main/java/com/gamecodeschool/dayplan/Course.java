package com.gamecodeschool.dayplan;

/*
 * Hasan Awada, Student ID 3637019
 * Abraham Sameer, Student ID 3812585
 *
 * Course
 *
 * Description:
 * - Represents an academic course stored
 * - Stores the course ID, name, and description
 */
public class Course {
    private int courseId;
    private String courseName;
    private String courseDescription;

    public Course(int courseId, String courseName, String courseDescription) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
    }

    public Course(String courseName, String courseDescription) {
        this.courseName = courseName;
        this.courseDescription = courseDescription;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    @Override
    public String toString() {
        return courseName;
    }
}