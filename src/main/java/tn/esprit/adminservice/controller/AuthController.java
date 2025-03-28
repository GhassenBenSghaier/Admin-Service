package tn.esprit.adminservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.adminservice.entity.*;
import tn.esprit.adminservice.repository.UserRepository;
import tn.esprit.adminservice.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
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
            String token = jwtUtil.generateToken(username, user.getRole(), user.getProfile(), user.getStatus()); // Pass status
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> registrationData) {
        String username = registrationData.get("username");
        String password = registrationData.get("password");
        String email = registrationData.get("email");
        String role = registrationData.get("role");
        String firstName = registrationData.get("firstName");
        String lastName = registrationData.get("lastName");
        String birthdateStr = registrationData.get("birthdate"); // Expecting ISO format (e.g., "2000-01-01")
        String gender = registrationData.get("gender");
        String address = registrationData.get("address");
        String phoneNumber = registrationData.get("phoneNumber");

        if (userRepository.findByUsernameIgnoreCase(username) != null || userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username or email already exists"));
        }

        User user;
        switch (role.toUpperCase()) {
            case "CENTRAL_ADMIN":
                user = new CentralAdmin();
                ((CentralAdmin) user).setDepartment(registrationData.get("department"));
                ((CentralAdmin) user).setAccessLevel(registrationData.get("accessLevel"));
                ((CentralAdmin) user).setEmployeeId(registrationData.get("employeeId"));
                ((CentralAdmin) user).setHireDate(registrationData.get("hireDate") != null ?
                        LocalDate.parse(registrationData.get("hireDate")) : null);
                break;
            case "LOCAL_ADMIN":
                user = new LocalAdmin();
                ((LocalAdmin) user).setSchoolName(registrationData.get("schoolName"));
                ((LocalAdmin) user).setAdminCode(registrationData.get("adminCode"));
                ((LocalAdmin) user).setEmployeeId(registrationData.get("employeeId"));
                ((LocalAdmin) user).setHireDate(registrationData.get("hireDate") != null ?
                        LocalDate.parse(registrationData.get("hireDate")) : null);
                break;
            case "TEACHER":
                user = new Teacher();
                ((Teacher) user).setSubjectSpecialization(registrationData.get("subjectSpecialization"));
                ((Teacher) user).setEmployeeId(registrationData.get("employeeId"));
                ((Teacher) user).setHireDate(registrationData.get("hireDate") != null ?
                        LocalDate.parse(registrationData.get("hireDate")) : null);
                ((Teacher) user).setQualification(registrationData.get("qualification"));
                ((Teacher) user).setDepartment(registrationData.get("department"));
                ((Teacher) user).setTeacherRank(registrationData.get("teacherRank"));
                ((Teacher) user).setSchoolName(registrationData.get("schoolName")); // Set schoolName for Teacher
                break;
            case "STUDENT":
            default:
                user = new Student();
                ((Student) user).setStudentId(registrationData.get("studentId"));
                ((Student) user).setEnrollmentDate(registrationData.get("enrollmentDate") != null ?
                        LocalDate.parse(registrationData.get("enrollmentDate")) : null);
                ((Student) user).setGradeLevel(registrationData.get("gradeLevel"));
                ((Student) user).setSchoolClass(registrationData.get("schoolClass"));
                ((Student) user).setParentName(registrationData.get("parentName"));
                ((Student) user).setParentContact(registrationData.get("parentContact"));
                ((Student) user).setPreviousSchool(registrationData.get("previousSchool"));
                ((Student) user).setMedicalConditions(registrationData.get("medicalConditions"));
                ((Student) user).setStudentStatus(registrationData.get("studentStatus"));
                ((Student) user).setSchoolName(registrationData.get("schoolName")); // Set schoolName for Student
                break;
        }

        // Set common attributes
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role.toUpperCase());
        user.setEmail(email);
        user.setStatus("Pending");
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setBirthdate(birthdateStr != null ? LocalDate.parse(birthdateStr) : null);
        user.setGender(gender);
        user.setAddress(address);
        user.setPhoneNumber(phoneNumber);

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User registered successfully", "userId", user.getId()));
    }
}