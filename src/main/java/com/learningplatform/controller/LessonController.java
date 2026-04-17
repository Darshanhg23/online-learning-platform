package com.learningplatform.controller;

import com.learningplatform.model.LessonProgress;
import com.learningplatform.repository.EnrollmentRepository;
import com.learningplatform.repository.LessonProgressRepository;
import com.learningplatform.repository.LessonRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lessons")
@CrossOrigin(origins = "*")
public class LessonController {

    private final LessonProgressRepository lessonProgressRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;

    public LessonController(LessonProgressRepository lessonProgressRepository,
                            LessonRepository lessonRepository,
                            EnrollmentRepository enrollmentRepository) {
        this.lessonProgressRepository = lessonProgressRepository;
        this.lessonRepository = lessonRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    // POST /api/lessons/{lessonId}/complete — mark lesson complete
    @PostMapping("/{lessonId}/complete")
    public ResponseEntity<Map<String, Object>> markComplete(
            @PathVariable int lessonId,
            @RequestBody Map<String, Integer> body) {

        int enrollmentId = body.get("enrollmentId");

        lessonProgressRepository.markComplete(enrollmentId, lessonId);

        // Recalculate and update progress percentage
        recalculateProgress(enrollmentId);

        int completedCount = lessonProgressRepository.countCompleted(enrollmentId);
        return ResponseEntity.ok(Map.of(
                "message", "Lesson marked complete",
                "lessonId", lessonId,
                "enrollmentId", enrollmentId,
                "completedCount", completedCount
        ));
    }

    // POST /api/lessons/{lessonId}/incomplete — mark lesson incomplete
    @PostMapping("/{lessonId}/incomplete")
    public ResponseEntity<Map<String, Object>> markIncomplete(
            @PathVariable int lessonId,
            @RequestBody Map<String, Integer> body) {

        int enrollmentId = body.get("enrollmentId");

        lessonProgressRepository.markIncomplete(enrollmentId, lessonId);

        // Recalculate progress
        recalculateProgress(enrollmentId);

        int completedCount = lessonProgressRepository.countCompleted(enrollmentId);
        return ResponseEntity.ok(Map.of(
                "message", "Lesson marked incomplete",
                "lessonId", lessonId,
                "enrollmentId", enrollmentId,
                "completedCount", completedCount
        ));
    }

    // GET /api/lessons/progress/{enrollmentId} — get all lesson progress for enrollment
    @GetMapping("/progress/{enrollmentId}")
    public ResponseEntity<List<LessonProgress>> getProgress(@PathVariable int enrollmentId) {
        return ResponseEntity.ok(lessonProgressRepository.findByEnrollmentId(enrollmentId));
    }

    // Helper: recalculate progress % and update enrollment
    private void recalculateProgress(int enrollmentId) {
        enrollmentRepository.findById(enrollmentId).ifPresent(enrollment -> {
            // Get the course to know total lessons
            int courseId = enrollment.getCourseId();
            int totalLessons = lessonRepository.findByCourseId(courseId).size();
            int completedLessons = lessonProgressRepository.countCompleted(enrollmentId);

            int percent = totalLessons > 0 ? (completedLessons * 100) / totalLessons : 0;
            enrollmentRepository.updateProgress(enrollmentId, percent);
        });
    }
}
