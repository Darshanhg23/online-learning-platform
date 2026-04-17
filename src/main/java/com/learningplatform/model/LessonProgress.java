package com.learningplatform.model;

import java.time.LocalDateTime;

public class LessonProgress {
    private int id;
    private int enrollmentId;
    private int lessonId;
    private boolean completed;
    private LocalDateTime completedAt;

    public LessonProgress() {}

    public LessonProgress(int id, int enrollmentId, int lessonId, boolean completed, LocalDateTime completedAt) {
        this.id = id;
        this.enrollmentId = enrollmentId;
        this.lessonId = lessonId;
        this.completed = completed;
        this.completedAt = completedAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(int enrollmentId) { this.enrollmentId = enrollmentId; }

    public int getLessonId() { return lessonId; }
    public void setLessonId(int lessonId) { this.lessonId = lessonId; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
