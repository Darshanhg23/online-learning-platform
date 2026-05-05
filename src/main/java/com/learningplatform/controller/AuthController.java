package com.learningplatform.controller;

import com.learningplatform.model.User;
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
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
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

        // ── DEVELOPMENT FALLBACK FOR ADMIN ──
        if ("admin@learnhub.com".equals(email) && "admin123".equals(pass)) {
            Optional<User> existingAdmin = userRepository.findByEmail(email);
            User admin;
            if (existingAdmin.isPresent()) {
                admin = existingAdmin.get();
            } else {
                // If not in DB yet, create a temporary session user
                admin = new User("System Admin", email, "secret");
                admin.setRole("ADMIN");
                admin.setId(999);
            }
            
            session.setAttribute("userId",    admin.getId());
            session.setAttribute("userEmail", admin.getEmail());
            session.setAttribute("userName",  admin.getName());
            
            return ResponseEntity.ok(Map.of(
                    "message", "Admin login successful (Dev Fallback).",
                    "id",    admin.getId(),
                    "name",  admin.getName(),
                    "email", admin.getEmail(),
                    "role",  "ADMIN"));
        }

        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty() || !passwordEncoder.matches(pass, opt.get().getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password."));
        }

        User user = opt.get();

        // Set session
        session.setAttribute("userId",    user.getId());
        session.setAttribute("userEmail", user.getEmail());
        session.setAttribute("userName",  user.getName());

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

        String  userEmail = (String)  session.getAttribute("userEmail");

        if (userEmail != null) {
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
        
        Optional<User> opt = userRepository.findById(userId);
        if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        
        User user = opt.get();
        return ResponseEntity.ok(Map.of(
                "id",    user.getId(),
                "name",  user.getName(),
                "email", user.getEmail(),
                "role",  user.getRole()));
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

