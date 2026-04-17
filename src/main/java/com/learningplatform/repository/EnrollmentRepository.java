package com.learningplatform.repository;

import com.learningplatform.model.Enrollment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class EnrollmentRepository {

    private final JdbcTemplate jdbcTemplate;

    public EnrollmentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ── Row Mappers ─────────────────────────────────────────────────────────

    private static final RowMapper<Enrollment> ENROLLMENT_ROW_MAPPER = (rs, rowNum) -> {
        Enrollment e = new Enrollment();
        e.setId(rs.getInt("id"));
        e.setCourseId(rs.getInt("course_id"));
        Timestamp ts = rs.getTimestamp("enrolled_at");
        if (ts != null) e.setEnrolledAt(ts.toLocalDateTime());
        e.setProgressPercent(rs.getInt("progress_percent"));
        return e;
    };

    private static final RowMapper<Enrollment> ENROLLMENT_WITH_COURSE_MAPPER = (rs, rowNum) -> {
        Enrollment e = new Enrollment();
        e.setId(rs.getInt("e_id"));
        e.setCourseId(rs.getInt("e_course_id"));
        Timestamp ts = rs.getTimestamp("e_enrolled_at");
        if (ts != null) e.setEnrolledAt(ts.toLocalDateTime());
        e.setProgressPercent(rs.getInt("e_progress_percent"));
        // Joined course fields
        e.setCourseTitle(rs.getString("c_title"));
        e.setCourseInstructor(rs.getString("c_instructor"));
        e.setCourseDuration(rs.getString("c_duration"));
        e.setCourseCategory(rs.getString("c_category"));
        e.setCourseThumbnailUrl(rs.getString("c_thumbnail_url"));
        e.setCourseTotalLessons(rs.getInt("c_total_lessons"));
        return e;
    };

    // ── Enroll (user-aware) ─────────────────────────────────────────────────

    /** Enroll a user in a course — returns generated enrollment ID */
    public int enroll(int courseId, int userId) {
        String sql = "INSERT INTO enrollments (user_id, course_id, enrolled_at, progress_percent) VALUES (?, ?, ?, 0)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setInt(2, courseId);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    // ── Enrollment checks (per-user) ────────────────────────────────────────

    /** True if the given user is already enrolled in courseId */
    public boolean isEnrolledByUser(int courseId, int userId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE course_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, courseId, userId);
        return count != null && count > 0;
    }

    /** Legacy — used only when no session (backward compat) */
    public boolean isEnrolled(int courseId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE course_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, courseId);
        return count != null && count > 0;
    }

    // ── Lookups ─────────────────────────────────────────────────────────────

    public Optional<Enrollment> findByCourseIdAndUser(int courseId, int userId) {
        String sql = "SELECT * FROM enrollments WHERE course_id = ? AND user_id = ?";
        List<Enrollment> list = jdbcTemplate.query(sql, ENROLLMENT_ROW_MAPPER, courseId, userId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    /** Legacy — fallback when user_id not known */
    public Optional<Enrollment> findByCourseId(int courseId) {
        String sql = "SELECT * FROM enrollments WHERE course_id = ? LIMIT 1";
        List<Enrollment> list = jdbcTemplate.query(sql, ENROLLMENT_ROW_MAPPER, courseId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public Optional<Enrollment> findById(int id) {
        String sql = "SELECT * FROM enrollments WHERE id = ?";
        List<Enrollment> list = jdbcTemplate.query(sql, ENROLLMENT_ROW_MAPPER, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    /** All enrollments for a user, joined with course data */
    public List<Enrollment> findAllWithCourseByUser(int userId) {
        String sql = "SELECT e.id AS e_id, e.course_id AS e_course_id, " +
                     "e.enrolled_at AS e_enrolled_at, e.progress_percent AS e_progress_percent, " +
                     "c.title AS c_title, c.instructor AS c_instructor, " +
                     "c.duration AS c_duration, c.category AS c_category, " +
                     "c.thumbnail_url AS c_thumbnail_url, c.total_lessons AS c_total_lessons " +
                     "FROM enrollments e JOIN courses c ON e.course_id = c.id " +
                     "WHERE e.user_id = ? " +
                     "ORDER BY e.enrolled_at DESC";
        return jdbcTemplate.query(sql, ENROLLMENT_WITH_COURSE_MAPPER, userId);
    }

    /** All enrollments (admin — full join) */
    public List<Enrollment> findAllWithCourse() {
        String sql = "SELECT e.id AS e_id, e.course_id AS e_course_id, " +
                     "e.enrolled_at AS e_enrolled_at, e.progress_percent AS e_progress_percent, " +
                     "c.title AS c_title, c.instructor AS c_instructor, " +
                     "c.duration AS c_duration, c.category AS c_category, " +
                     "c.thumbnail_url AS c_thumbnail_url, c.total_lessons AS c_total_lessons " +
                     "FROM enrollments e JOIN courses c ON e.course_id = c.id " +
                     "ORDER BY e.enrolled_at DESC";
        return jdbcTemplate.query(sql, ENROLLMENT_WITH_COURSE_MAPPER);
    }

    // ── Update progress ─────────────────────────────────────────────────────

    public void updateProgress(int enrollmentId, int progressPercent) {
        String sql = "UPDATE enrollments SET progress_percent = ? WHERE id = ?";
        jdbcTemplate.update(sql, progressPercent, enrollmentId);
    }
}
