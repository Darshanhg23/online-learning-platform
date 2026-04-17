package com.learningplatform.model;

import java.time.LocalDateTime;

public class Enrollment {
    private int id;
    private int courseId;
    private LocalDateTime enrolledAt;
    private int progressPercent;

    // Joined fields (from courses table)
    private String courseTitle;
    private String courseInstructor;
    private String courseDuration;
    private String courseCategory;
    private String courseThumbnailUrl;
    private int courseTotalLessons;

    public Enrollment() {}

    public Enrollment(int id, int courseId, LocalDateTime enrolledAt, int progressPercent) {
        this.id = id;
        this.courseId = courseId;
        this.enrolledAt = enrolledAt;
        this.progressPercent = progressPercent;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public LocalDateTime getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(LocalDateTime enrolledAt) { this.enrolledAt = enrolledAt; }

    public int getProgressPercent() { return progressPercent; }
    public void setProgressPercent(int progressPercent) { this.progressPercent = progressPercent; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public String getCourseInstructor() { return courseInstructor; }
    public void setCourseInstructor(String courseInstructor) { this.courseInstructor = courseInstructor; }

    public String getCourseDuration() { return courseDuration; }
    public void setCourseDuration(String courseDuration) { this.courseDuration = courseDuration; }

    public String getCourseCategory() { return courseCategory; }
    public void setCourseCategory(String courseCategory) { this.courseCategory = courseCategory; }

    public String getCourseThumbnailUrl() { return courseThumbnailUrl; }
    public void setCourseThumbnailUrl(String courseThumbnailUrl) { this.courseThumbnailUrl = courseThumbnailUrl; }

    public int getCourseTotalLessons() { return courseTotalLessons; }
    public void setCourseTotalLessons(int courseTotalLessons) { this.courseTotalLessons = courseTotalLessons; }

    @Override
    public String toString() {
        return "Enrollment{id=" + id + ", courseId=" + courseId + ", progress=" + progressPercent + "%}";
    }
}
