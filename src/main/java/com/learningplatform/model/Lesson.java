package com.learningplatform.model;

public class Lesson {
    private int id;
    private int courseId;
    private String title;
    private int lessonOrder;
    private String duration;

    public Lesson() {}

    public Lesson(int id, int courseId, String title, int lessonOrder, String duration) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.lessonOrder = lessonOrder;
        this.duration = duration;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getLessonOrder() { return lessonOrder; }
    public void setLessonOrder(int lessonOrder) { this.lessonOrder = lessonOrder; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    @Override
    public String toString() {
        return "Lesson{id=" + id + ", title='" + title + "', order=" + lessonOrder + "}";
    }
}
