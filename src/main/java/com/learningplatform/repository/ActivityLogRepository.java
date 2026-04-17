package com.learningplatform.repository;

import com.learningplatform.model.ActivityLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

@Repository
public class ActivityLogRepository {

    private final JdbcTemplate jdbcTemplate;

    public ActivityLogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<ActivityLog> LOG_MAPPER = (rs, rowNum) -> {
        ActivityLog log = new ActivityLog();
        log.setId(rs.getInt("id"));
        int uid = rs.getInt("user_id");
        if (!rs.wasNull()) log.setUserId(uid);
        log.setUserEmail(rs.getString("user_email"));
        log.setUserName(rs.getString("user_name"));
        log.setAction(rs.getString("action"));
        log.setDetail(rs.getString("detail"));
        log.setIpAddress(rs.getString("ip_address"));
        Timestamp ts = rs.getTimestamp("logged_at");
        if (ts != null) log.setLoggedAt(ts.toLocalDateTime());
        return log;
    };

    /** Insert a new activity log entry */
    public void log(ActivityLog entry) {
        String sql = "INSERT INTO activity_logs (user_id, user_email, user_name, action, detail, ip_address) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                entry.getUserId(),
                entry.getUserEmail(),
                entry.getUserName(),
                entry.getAction(),
                entry.getDetail(),
                entry.getIpAddress());
    }

    /** All logs, newest first */
    public List<ActivityLog> findAll() {
        return jdbcTemplate.query(
            "SELECT * FROM activity_logs ORDER BY logged_at DESC", LOG_MAPPER);
    }

    /** Logs filtered by action type */
    public List<ActivityLog> findByAction(String action) {
        return jdbcTemplate.query(
            "SELECT * FROM activity_logs WHERE action = ? ORDER BY logged_at DESC",
            LOG_MAPPER, action);
    }

    /** Logs for a specific user */
    public List<ActivityLog> findByUserId(int userId) {
        return jdbcTemplate.query(
            "SELECT * FROM activity_logs WHERE user_id = ? ORDER BY logged_at DESC",
            LOG_MAPPER, userId);
    }
}
