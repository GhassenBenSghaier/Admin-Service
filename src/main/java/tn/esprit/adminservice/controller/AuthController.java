//package tn.esprit.adminservice.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import tn.esprit.adminservice.entity.User;
//import tn.esprit.adminservice.repository.UserRepository;
//import tn.esprit.adminservice.util.JwtUtil;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//
//    private final UserRepository userRepository;
//    private final JwtUtil jwtUtil;
//    private final PasswordEncoder passwordEncoder;
//
//    @Autowired
//    public AuthController(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
//        this.userRepository = userRepository;
//        this.jwtUtil = jwtUtil;
//        this.passwordEncoder = passwordEncoder; // Inject via constructor
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody Map<String, String> credentials) {
//        String username = credentials.get("username");
//        String password = credentials.get("password");
//
//        User user = userRepository.findByUsernameIgnoreCase(username);
//        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
//            String token = jwtUtil.generateToken(username, user.getRole()); // Pass the role
//            return ResponseEntity.ok(token);
//        }
//        return ResponseEntity.status(401).body("Invalid credentials");
//    }
//}
//


package tn.esprit.adminservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.adminservice.entity.User;
import tn.esprit.adminservice.repository.UserRepository;
import tn.esprit.adminservice.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        User user = userRepository.findByUsernameIgnoreCase(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtUtil.generateToken(username, user.getRole());
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

//    @PostMapping("/register")
//    public ResponseEntity<String> register(@RequestBody Map<String, String> registrationData) {
//        String username = registrationData.get("username");
//        String password = registrationData.get("password");
//        String email = registrationData.get("email");
//        String name = registrationData.get("name");
//
//        System.out.println("Registration data: " + registrationData); // Debugging
//
//        if (userRepository.findByUsernameIgnoreCase(username) != null || userRepository.existsByEmail(email)) {
//            return ResponseEntity.badRequest().body("Username or email already exists");
//        }
//
//        User user = new User();
//        user.setUsername(username);
//        user.setPassword(passwordEncoder.encode(password));
//        user.setRole("STUDENT");
//        user.setEmail(email);
//        user.setStatus("Pending");
//        userRepository.save(user);
//
//        return ResponseEntity.ok("User registered successfully");
//    }
//}
@PostMapping("/register")
public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> registrationData) {
    String username = registrationData.get("username");
    String password = registrationData.get("password");
    String email = registrationData.get("email");
    String name = registrationData.get("name");
    String role = registrationData.get("role"); // Get role from request

    System.out.println("Registration data: " + registrationData);

    if (userRepository.findByUsernameIgnoreCase(username) != null || userRepository.existsByEmail(email)) {
        return ResponseEntity.badRequest().body(Map.of("message", "Username or email already exists"));
    }

    if (!"STUDENT".equalsIgnoreCase(role) && !"TEACHER".equalsIgnoreCase(role)) {
        return ResponseEntity.badRequest().body(Map.of("message", "Invalid role. Must be STUDENT or TEACHER"));
    }

    User user = new User();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(password));
    user.setRole(role.toUpperCase()); // Set the role from the request
    user.setEmail(email);
    user.setStatus("Pending");
    userRepository.save(user);

    return ResponseEntity.ok(Map.of("message", "User registered successfully", "userId", user.getId()));
}
    class AuthRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }}
