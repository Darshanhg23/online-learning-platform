package com.learningplatform.controller;

import com.learningplatform.model.Course;
import com.learningplatform.model.Lesson;
import com.learningplatform.repository.CourseRepository;
import com.learningplatform.repository.EnrollmentRepository;
import com.learningplatform.repository.LessonRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class CourseController {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CourseController(CourseRepository courseRepository,
                            LessonRepository lessonRepository,
                            EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    // GET /api/courses — all courses with optional ?category= &search= filters
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {

        List<Course> courses;
        if (search != null && !search.isBlank()) {
            courses = courseRepository.search(search.trim());
        } else if (category != null && !category.isBlank()) {
            courses = courseRepository.findByCategory(category.trim());
        } else {
            courses = courseRepository.findAll();
        }
        return ResponseEntity.ok(courses);
    }

    // GET /api/courses/featured — homepage featured courses
    @GetMapping("/featured")
    public ResponseEntity<List<Course>> getFeaturedCourses() {
        return ResponseEntity.ok(courseRepository.findFeatured());
    }

    // GET /api/courses/categories — all category strings
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(courseRepository.findAllCategories());
    }

    // GET /api/courses/{id} — single course with lessons + per-user enrollment status
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCourseById(
            @PathVariable int id,
            HttpSession session) {

        Optional<Course> opt = courseRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Course course = opt.get();
        List<Lesson> lessons = lessonRepository.findByCourseId(id);

        Integer userId = (Integer) session.getAttribute("userId");
        boolean enrolled = false;
        if (userId != null) {
            enrolled = enrollmentRepository.isEnrolledByUser(id, userId);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("course", course);
        response.put("lessons", lessons);
        response.put("enrolled", enrolled);

        if (enrolled && userId != null) {
            enrollmentRepository.findByCourseIdAndUser(id, userId).ifPresent(e -> {
                response.put("enrollmentId", e.getId());
                response.put("progressPercent", e.getProgressPercent());
            });
        }

        return ResponseEntity.ok(response);
    }
}
