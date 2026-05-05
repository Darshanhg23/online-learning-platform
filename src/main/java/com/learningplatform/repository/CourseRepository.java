package com.learningplatform.repository;

import com.learningplatform.model.Course;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class CourseRepository {

    private final JdbcTemplate jdbcTemplate;

    public CourseRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper to convert ResultSet → Course object
    private static final RowMapper<Course> COURSE_ROW_MAPPER = new RowMapper<Course>() {
        @Override
        public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
            Course course = new Course();
            course.setId(rs.getInt("id"));
            course.setTitle(rs.getString("title"));
            course.setDescription(rs.getString("description"));
            course.setCategory(rs.getString("category"));
            course.setDuration(rs.getString("duration"));
            course.setInstructor(rs.getString("instructor"));
            course.setThumbnailUrl(rs.getString("thumbnail_url"));
            course.setFeatured(rs.getBoolean("is_featured"));
            course.setTotalLessons(rs.getInt("total_lessons"));
            return course;
        }
    };

    // Get all courses
    public List<Course> findAll() {
        String sql = "SELECT * FROM courses ORDER BY id";
        return jdbcTemplate.query(sql, COURSE_ROW_MAPPER);
    }

    // Get all featured courses (for homepage)
    public List<Course> findFeatured() {
        String sql = "SELECT * FROM courses WHERE is_featured = TRUE ORDER BY id";
        return jdbcTemplate.query(sql, COURSE_ROW_MAPPER);
    }

    // Get course by ID
    public Optional<Course> findById(int id) {
        String sql = "SELECT * FROM courses WHERE id = ?";
        List<Course> courses = jdbcTemplate.query(sql, COURSE_ROW_MAPPER, id);
        return courses.isEmpty() ? Optional.empty() : Optional.of(courses.get(0));
    }

    // Get courses by category
    public List<Course> findByCategory(String category) {
        String sql = "SELECT * FROM courses WHERE category = ? ORDER BY id";
        return jdbcTemplate.query(sql, COURSE_ROW_MAPPER, category);
    }

    // Search courses by keyword (title or description)
    public List<Course> search(String keyword) {
        String sql = "SELECT * FROM courses WHERE title LIKE ? OR description LIKE ? ORDER BY id";
        String pattern = "%" + keyword + "%";
        return jdbcTemplate.query(sql, COURSE_ROW_MAPPER, pattern, pattern);
    }

    // Get all unique categories
    public List<String> findAllCategories() {
        String sql = "SELECT DISTINCT category FROM courses ORDER BY category";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    // Save a new course
    public int save(Course course) {
        String sql = "INSERT INTO courses (title, description, category, duration, instructor, thumbnail_url, is_featured, total_lessons) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                course.getTitle(),
                course.getDescription(),
                course.getCategory(),
                course.getDuration(),
                course.getInstructor(),
                course.getThumbnailUrl(),
                course.isFeatured(),
                course.getTotalLessons());
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
    }

    // Update an existing course
    public void update(Course course) {
        String sql = "UPDATE courses SET title = ?, description = ?, category = ?, duration = ?, instructor = ?, thumbnail_url = ?, is_featured = ?, total_lessons = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                course.getTitle(),
                course.getDescription(),
                course.getCategory(),
                course.getDuration(),
                course.getInstructor(),
                course.getThumbnailUrl(),
                course.isFeatured(),
                course.getTotalLessons(),
                course.getId());
    }

    // Delete a course
    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM courses WHERE id = ?", id);
    }
}
