package com.learningplatform.controller;

import com.learningplatform.model.ActivityLog;
import com.learningplatform.model.Enrollment;
import com.learningplatform.repository.ActivityLogRepository;
import com.learningplatform.repository.CourseRepository;
import com.learningplatform.repository.EnrollmentRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class EnrollmentController {

    private final EnrollmentRepository enrollmentRepository;
    private final ActivityLogRepository activityLogRepository;
    private final CourseRepository courseRepository;

    public EnrollmentController(EnrollmentRepository enrollmentRepository,
                                ActivityLogRepository activityLogRepository,
                                CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.activityLogRepository = activityLogRepository;
        this.courseRepository = courseRepository;
    }

    // ── POST /api/enrollments — enroll in a course ────────────────────────
    @PostMapping
    public ResponseEntity<Map<String, Object>> enroll(
            @RequestBody Map<String, Integer> body,
            HttpSession session,
            HttpServletRequest request) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please log in to enroll in courses."));
        }

        int courseId = body.get("courseId");

        // Check if already enrolled (per user)
        if (enrollmentRepository.isEnrolledByUser(courseId, userId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Already enrolled", "courseId", courseId));
        }

        int enrollmentId = enrollmentRepository.enroll(courseId, userId);

        // Look up course title for the activity log
        String courseTitle = courseRepository.findById(courseId)
                .map(c -> c.getTitle())
                .orElse("Course #" + courseId);

        String userEmail = (String) session.getAttribute("userEmail");
        String userName  = (String) session.getAttribute("userName");

        // Log the enrollment event
        activityLogRepository.log(new ActivityLog(
                userId, userEmail, userName, "ENROLL",
                "Enrolled in: " + courseTitle, getClientIp(request)));

        System.out.println("✅ ENROLL: " + userEmail + " → " + courseTitle);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Enrolled successfully",
                             "enrollmentId", enrollmentId,
                             "courseId", courseId));
    }

    // ── GET /api/enrollments — current user's enrolled courses ───────────
    @GetMapping
    public ResponseEntity<?> getAllEnrollments(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            // Return empty list for unauthenticated users (so pages don't break)
            return ResponseEntity.ok(List.of());
        }
        try {
            return ResponseEntity.ok(enrollmentRepository.findAllWithCourseByUser(userId));
        } catch (Exception e) {
            System.err.println("❌ Error loading enrollments for userId=" + userId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(List.of()); // Return empty list instead of 500
        }
    }

    // ── PUT /api/enrollments/{id}/progress — update progress ────────────
    @PutMapping("/{id}/progress")
    public ResponseEntity<Map<String, Object>> updateProgress(
            @PathVariable int id,
            @RequestBody Map<String, Integer> body,
            HttpSession session) {

        if (session.getAttribute("userId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please log in."));
        }

        int percent = body.get("progressPercent");
        if (percent < 0) percent = 0;
        if (percent > 100) percent = 100;

        Optional<Enrollment> opt = enrollmentRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        enrollmentRepository.updateProgress(id, percent);
        return ResponseEntity.ok(Map.of("message", "Progress updated",
                                         "enrollmentId", id,
                                         "progressPercent", percent));
    }

    // ── Utility ─────────────────────────────────────────────────────────────
    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) return forwarded.split(",")[0].trim();
        return request.getRemoteAddr();
    }
}
