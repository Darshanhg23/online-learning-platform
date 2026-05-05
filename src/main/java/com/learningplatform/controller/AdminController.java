package com.learningplatform.controller;

import com.learningplatform.model.Course;
import com.learningplatform.model.Lesson;
import com.learningplatform.model.User;
import com.learningplatform.repository.CourseRepository;
import com.learningplatform.repository.LessonRepository;
import com.learningplatform.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class AdminController {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;

    public AdminController(UserRepository userRepository,
                           CourseRepository courseRepository,
                           LessonRepository lessonRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
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

    // ── COURSE MANAGEMENT ───────────────────────────────────────────────────

    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAdminCourses() {
        return ResponseEntity.ok(courseRepository.findAll());
    }

    @PostMapping("/courses")
    public ResponseEntity<Map<String, Object>> createCourse(@RequestBody Course course) {
        int id = courseRepository.save(course);
        return ResponseEntity.ok(Map.of("message", "Course created", "id", id));
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<Map<String, String>> updateCourse(@PathVariable int id, @RequestBody Course course) {
        course.setId(id);
        courseRepository.update(course);
        return ResponseEntity.ok(Map.of("message", "Course updated"));
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Map<String, String>> deleteCourse(@PathVariable int id) {
        courseRepository.delete(id);
        return ResponseEntity.ok(Map.of("message", "Course deleted"));
    }

    // ── LESSON MANAGEMENT ───────────────────────────────────────────────────

    @PostMapping("/lessons")
    public ResponseEntity<Map<String, String>> createLesson(@RequestBody Lesson lesson) {
        lessonRepository.save(lesson);
        return ResponseEntity.ok(Map.of("message", "Lesson created"));
    }

    @PutMapping("/lessons/{id}")
    public ResponseEntity<Map<String, String>> updateLesson(@PathVariable int id, @RequestBody Lesson lesson) {
        lesson.setId(id);
        lessonRepository.update(lesson);
        return ResponseEntity.ok(Map.of("message", "Lesson updated"));
    }

    @DeleteMapping("/lessons/{id}")
    public ResponseEntity<Map<String, String>> deleteLesson(@PathVariable int id) {
        lessonRepository.delete(id);
        return ResponseEntity.ok(Map.of("message", "Lesson deleted"));
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadVideo(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "File is empty"));
        }
        try {
            String originalName = file.getOriginalFilename();
            if (originalName == null) originalName = "video.mp4";
            
            String fileName = System.currentTimeMillis() + "_" + originalName.replaceAll("\\s+", "_");
            
            String rootPath = System.getProperty("user.dir");
            Path uploadDir = Paths.get(rootPath, "src/main/resources/static/videos/");
            Path targetDir = Paths.get(rootPath, "target/classes/static/videos/");
            
            if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);
            
            Path filePath = uploadDir.resolve(fileName);
            Files.write(filePath, file.getBytes());
            
            // Also write to target folder for immediate access
            if (Files.exists(Paths.get(rootPath, "target"))) {
                if (!Files.exists(targetDir)) Files.createDirectories(targetDir);
                Files.write(targetDir.resolve(fileName), file.getBytes());
            }

            return ResponseEntity.ok(Map.of("url", "videos/" + fileName));
        } catch (Exception e) {
            System.err.println("Upload error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Upload failed: " + e.getMessage()));
        }
    }

    @DeleteMapping("/courses/{id}/lessons")
    public ResponseEntity<Map<String, String>> clearLessons(@PathVariable int id) {
        lessonRepository.deleteByCourseId(id);
        return ResponseEntity.ok(Map.of("message", "Lessons cleared"));
    }
}



