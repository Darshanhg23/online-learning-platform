package com.learningplatform.repository;

import com.learningplatform.model.LessonProgress;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class LessonProgressRepository {

    private final JdbcTemplate jdbcTemplate;

    public LessonProgressRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<LessonProgress> LP_ROW_MAPPER = new RowMapper<LessonProgress>() {
        @Override
        public LessonProgress mapRow(ResultSet rs, int rowNum) throws SQLException {
            LessonProgress lp = new LessonProgress();
            lp.setId(rs.getInt("id"));
            lp.setEnrollmentId(rs.getInt("enrollment_id"));
            lp.setLessonId(rs.getInt("lesson_id"));
            lp.setCompleted(rs.getBoolean("completed"));
            Timestamp ts = rs.getTimestamp("completed_at");
            if (ts != null) lp.setCompletedAt(ts.toLocalDateTime());
            return lp;
        }
    };

    // Mark a lesson as complete (upsert pattern)
    public void markComplete(int enrollmentId, int lessonId) {
        String sql = "INSERT INTO lesson_progress (enrollment_id, lesson_id, completed, completed_at) " +
                     "VALUES (?, ?, TRUE, ?) " +
                     "ON DUPLICATE KEY UPDATE completed = TRUE, completed_at = ?";
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        jdbcTemplate.update(sql, enrollmentId, lessonId, now, now);
    }

    // Mark a lesson as incomplete
    public void markIncomplete(int enrollmentId, int lessonId) {
        String sql = "INSERT INTO lesson_progress (enrollment_id, lesson_id, completed, completed_at) " +
                     "VALUES (?, ?, FALSE, NULL) " +
                     "ON DUPLICATE KEY UPDATE completed = FALSE, completed_at = NULL";
        jdbcTemplate.update(sql, enrollmentId, lessonId);
    }

    // Get all lesson progress for an enrollment
    public List<LessonProgress> findByEnrollmentId(int enrollmentId) {
        String sql = "SELECT * FROM lesson_progress WHERE enrollment_id = ?";
        return jdbcTemplate.query(sql, LP_ROW_MAPPER, enrollmentId);
    }

    // Count completed lessons for an enrollment
    public int countCompleted(int enrollmentId) {
        String sql = "SELECT COUNT(*) FROM lesson_progress WHERE enrollment_id = ? AND completed = TRUE";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, enrollmentId);
        return count != null ? count : 0;
    }
}
