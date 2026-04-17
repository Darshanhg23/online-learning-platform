package com.learningplatform.controller;

import com.learningplatform.model.ActivityLog;
import com.learningplatform.model.User;
import com.learningplatform.repository.ActivityLogRepository;
import com.learningplatform.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class AdminController {

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;

    public AdminController(ActivityLogRepository activityLogRepository,
                           UserRepository userRepository) {
        this.activityLogRepository = activityLogRepository;
        this.userRepository = userRepository;
    }

    /**
     * GET /api/admin/logs           — all activity logs (newest first)
     * GET /api/admin/logs?action=LOGIN  — filter by action type
     */
    @GetMapping("/logs")
    public ResponseEntity<List<ActivityLog>> getLogs(
            @RequestParam(required = false) String action) {
        if (action != null && !action.isBlank()) {
            return ResponseEntity.ok(activityLogRepository.findByAction(action.toUpperCase()));
        }
        return ResponseEntity.ok(activityLogRepository.findAll());
    }

    /**
     * GET /api/admin/logs/user/{userId} — logs for one specific user
     */
    @GetMapping("/logs/user/{userId}")
    public ResponseEntity<List<ActivityLog>> getLogsByUser(@PathVariable int userId) {
        return ResponseEntity.ok(activityLogRepository.findByUserId(userId));
    }

    /**
     * GET /api/admin/users — all registered users (password hashes stripped)
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userRepository.findAll();
        // Strip password hashes — never expose them via API
        users.forEach(u -> u.setPasswordHash(""));
        return ResponseEntity.ok(users);
    }
}
