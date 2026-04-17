package com.learningplatform.repository;

import com.learningplatform.model.Lesson;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class LessonRepository {

    private final JdbcTemplate jdbcTemplate;

    public LessonRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Lesson> LESSON_ROW_MAPPER = new RowMapper<Lesson>() {
        @Override
        public Lesson mapRow(ResultSet rs, int rowNum) throws SQLException {
            Lesson lesson = new Lesson();
            lesson.setId(rs.getInt("id"));
            lesson.setCourseId(rs.getInt("course_id"));
            lesson.setTitle(rs.getString("title"));
            lesson.setLessonOrder(rs.getInt("lesson_order"));
            lesson.setDuration(rs.getString("duration"));
            return lesson;
        }
    };

    // Get all lessons for a specific course, ordered by lesson_order
    public List<Lesson> findByCourseId(int courseId) {
        String sql = "SELECT * FROM lessons WHERE course_id = ? ORDER BY lesson_order";
        return jdbcTemplate.query(sql, LESSON_ROW_MAPPER, courseId);
    }

    // Get lesson by ID
    public Lesson findById(int id) {
        String sql = "SELECT * FROM lessons WHERE id = ?";
        List<Lesson> lessons = jdbcTemplate.query(sql, LESSON_ROW_MAPPER, id);
        return lessons.isEmpty() ? null : lessons.get(0);
    }
}
