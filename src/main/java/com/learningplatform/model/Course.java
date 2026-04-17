package com.learningplatform.model;

public class Course {
    private int id;
    private String title;
    private String description;
    private String category;
    private String duration;
    private String instructor;
    private String thumbnailUrl;
    private boolean featured;
    private int totalLessons;

    public Course() {}

    public Course(int id, String title, String description, String category,
                  String duration, String instructor, String thumbnailUrl,
                  boolean featured, int totalLessons) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.duration = duration;
        this.instructor = instructor;
        this.thumbnailUrl = thumbnailUrl;
        this.featured = featured;
        this.totalLessons = totalLessons;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }

    public int getTotalLessons() { return totalLessons; }
    public void setTotalLessons(int totalLessons) { this.totalLessons = totalLessons; }

    @Override
    public String toString() {
        return "Course{id=" + id + ", title='" + title + "', category='" + category + "'}";
    }
}
