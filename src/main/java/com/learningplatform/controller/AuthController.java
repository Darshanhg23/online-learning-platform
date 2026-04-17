package com.learningplatform.controller;

import com.learningplatform.model.ActivityLog;
import com.learningplatform.model.User;
import com.learningplatform.repository.ActivityLogRepository;
import com.learningplatform.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class AuthController {

    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository,
                          ActivityLogRepository activityLogRepository) {
        this.userRepository = userRepository;
        this.activityLogRepository = activityLogRepository;
    }

    // ── POST /api/auth/register ──────────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @RequestBody Map<String, String> body,
            HttpServletRequest request,
            HttpSession session) {

        String name  = body.getOrDefault("name", "").trim();
        String email = body.getOrDefault("email", "").trim().toLowerCase();
        String pass  = body.getOrDefault("password", "");

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Name, email and password are required."));
        }
        if (pass.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Password must be at least 6 characters."));
        }
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Email already registered."));
        }

        User user = new User(name, email, passwordEncoder.encode(pass));
        int userId = userRepository.save(user);
        user.setId(userId);

        // Set session
        session.setAttribute("userId", userId);
        session.setAttribute("userEmail", email);
        session.setAttribute("userName", name);

        // Log activity
        activityLogRepository.log(new ActivityLog(
                userId, email, name, "REGISTER",
                "New user registered", getClientIp(request)));

        System.out.println("✅ REGISTER: " + email + " (" + name + ")");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Account created successfully.",
                             "id", userId, "name", name, "email", email));
    }

    // ── POST /api/auth/login ─────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> body,
            HttpServletRequest request,
            HttpSession session) {

        String email = body.getOrDefault("email", "").trim().toLowerCase();
        String pass  = body.getOrDefault("password", "");

        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty() || !passwordEncoder.matches(pass, opt.get().getPasswordHash())) {
            // Log failed attempt
            activityLogRepository.log(new ActivityLog(
                    null, email, null, "LOGIN_FAILED",
                    "Invalid credentials attempt", getClientIp(request)));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password."));
        }

        User user = opt.get();

        // Set session
        session.setAttribute("userId",    user.getId());
        session.setAttribute("userEmail", user.getEmail());
        session.setAttribute("userName",  user.getName());

        // Log activity
        activityLogRepository.log(new ActivityLog(
                user.getId(), user.getEmail(), user.getName(), "LOGIN",
                "User logged in", getClientIp(request)));

        System.out.println("✅ LOGIN: " + user.getEmail() + " (id=" + user.getId() + ") from " + getClientIp(request));

        return ResponseEntity.ok(Map.of(
                "message", "Login successful.",
                "id",    user.getId(),
                "name",  user.getName(),
                "email", user.getEmail(),
                "role",  user.getRole()));
    }

    // ── POST /api/auth/logout ────────────────────────────────────────────────
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(
            HttpServletRequest request,
            HttpSession session) {

        Integer userId    = (Integer) session.getAttribute("userId");
        String  userEmail = (String)  session.getAttribute("userEmail");
        String  userName  = (String)  session.getAttribute("userName");

        if (userId != null) {
            activityLogRepository.log(new ActivityLog(
                    userId, userEmail, userName, "LOGOUT",
                    "User logged out", getClientIp(request)));
            System.out.println("✅ LOGOUT: " + userEmail);
        }

        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
    }

    // ── GET /api/auth/me ─────────────────────────────────────────────────────
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not logged in."));
        }
        return ResponseEntity.ok(Map.of(
                "id",    userId,
                "name",  session.getAttribute("userName"),
                "email", session.getAttribute("userEmail")));
    }

    // ── Utility ──────────────────────────────────────────────────────────────
    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
