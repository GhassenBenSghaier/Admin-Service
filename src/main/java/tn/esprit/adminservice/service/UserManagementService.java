package tn.esprit.adminservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.esprit.adminservice.dto.UserDTO;
import tn.esprit.adminservice.entity.*;
import tn.esprit.adminservice.repository.ProfileRepository;
import tn.esprit.adminservice.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserManagementService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User addUser(UserDTO userDTO) {
        // Validate email uniqueness
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Set default status if not provided
        String status = userDTO.getStatus() != null ? userDTO.getStatus() : "Active";

        // Encode password if provided
        String encodedPassword = userDTO.getPassword() != null ? passwordEncoder.encode(userDTO.getPassword()) : null;

        // Default to STUDENT if role is null
        String roleUpper = (userDTO.getRole() == null) ? "STUDENT" : userDTO.getRole().toUpperCase();

        // Create the appropriate subclass instance based on role
        User newUser;
        switch (roleUpper) {
            case "CENTRAL_ADMIN":
                newUser = new CentralAdmin();
                break;
            case "LOCAL_ADMIN":
                newUser = new LocalAdmin();
                break;
            case "TEACHER":
                newUser = new Teacher();
                break;
            case "STUDENT":
            default:
                newUser = new Student();
                break;
        }

        // Set common fields
        newUser.setUsername(userDTO.getUsername());
        newUser.setPassword(encodedPassword);
        newUser.setRole(roleUpper);
        newUser.setEmail(userDTO.getEmail());
        newUser.setStatus(status);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        newUser.setBirthdate(userDTO.getBirthdate());
        newUser.setGender(userDTO.getGender());
        newUser.setAddress(userDTO.getAddress());
        newUser.setPhoneNumber(userDTO.getPhoneNumber());

        // Set role-specific fields from DTO
        switch (roleUpper) {
            case "CENTRAL_ADMIN":
                CentralAdmin centralAdmin = (CentralAdmin) newUser;
                centralAdmin.setDepartment(userDTO.getDepartment());
                centralAdmin.setAccessLevel(userDTO.getAccessLevel());
                centralAdmin.setEmployeeId(userDTO.getEmployeeId());
                centralAdmin.setHireDate(userDTO.getHireDate());
                break;
            case "LOCAL_ADMIN":
                LocalAdmin localAdmin = (LocalAdmin) newUser;
                localAdmin.setSchoolName(userDTO.getSchoolName());
                localAdmin.setAdminCode(userDTO.getAdminCode());
                localAdmin.setEmployeeId(userDTO.getEmployeeId());
                localAdmin.setHireDate(userDTO.getHireDate());
                break;
            case "TEACHER":
                Teacher teacher = (Teacher) newUser;
                teacher.setSubjectSpecialization(userDTO.getSubjectSpecialization());
                teacher.setEmployeeId(userDTO.getEmployeeId());
                teacher.setHireDate(userDTO.getHireDate());
                teacher.setQualification(userDTO.getQualification());
                teacher.setDepartment(userDTO.getDepartment());
                teacher.setTeacherRank(userDTO.getTeacherRank());
                teacher.setSchoolName(userDTO.getSchoolNameTeacher());
                break;
            case "STUDENT":
            default:
                Student student = (Student) newUser;
                student.setStudentId(userDTO.getStudentId());
                student.setEnrollmentDate(userDTO.getEnrollmentDate());
                student.setGradeLevel(userDTO.getGradeLevel());
                student.setSchoolClass(userDTO.getSchoolClass());
                student.setParentName(userDTO.getParentName());
                student.setParentContact(userDTO.getParentContact());
                student.setPreviousSchool(userDTO.getPreviousSchool());
                student.setMedicalConditions(userDTO.getMedicalConditions());
                student.setStudentStatus(userDTO.getStudentStatus() != null ? userDTO.getStudentStatus() : "Enrolled");
                student.setSchoolName(userDTO.getSchoolNameStudent());
                break;
        }


        if (userDTO.getProfileCode() != null) {
            Profile profile = profileRepository.findByCode(userDTO.getProfileCode())
                    .orElseThrow(() -> new IllegalArgumentException("Profile not found with code: " + userDTO.getProfileCode()));
            if (!"ACTIVE".equals(profile.getStatus())) {
                throw new IllegalStateException("Cannot assign inactive profile: " + userDTO.getProfileCode());
            }
            newUser.setProfile(profile);
        }

        return userRepository.save(newUser);
    }
    public User updateUser(Long id, UserDTO userDTO) {
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }

        User existingUser = existingUserOpt.get();

        // Log the incoming DTO password explicitly
        System.out.println("Received userDTO password: " + userDTO.getPassword());
        System.out.println("Received userDTO status: " + userDTO.getStatus());

        // Prevent role changes (optional: remove if role changes are allowed)
        if (userDTO.getRole() != null && !existingUser.getRole().equalsIgnoreCase(userDTO.getRole())) {
            throw new IllegalArgumentException("Cannot change user role");
        }

        // Update common fields
        existingUser.setUsername(userDTO.getUsername());
        if (userRepository.existsByEmail(userDTO.getEmail()) &&
                !userDTO.getEmail().equals(existingUser.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setStatus(userDTO.getStatus() != null ? userDTO.getStatus() : existingUser.getStatus());
        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setBirthdate(userDTO.getBirthdate());
        existingUser.setGender(userDTO.getGender());
        existingUser.setAddress(userDTO.getAddress());
        existingUser.setPhoneNumber(userDTO.getPhoneNumber());

        // Update password only if explicitly provided and non-empty
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            System.out.println("Updating password for user " + id + " to: " + userDTO.getPassword());
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        } else {
            System.out.println("Password unchanged for user " + id + "; retaining: " + existingUser.getPassword());
        }

        // Update subclass-specific fields (unchanged)
        switch (existingUser.getRole().toUpperCase()) {
            case "CENTRAL_ADMIN":
                CentralAdmin centralAdmin = (CentralAdmin) existingUser;
                centralAdmin.setDepartment(userDTO.getDepartment());
                centralAdmin.setAccessLevel(userDTO.getAccessLevel());
                centralAdmin.setEmployeeId(userDTO.getEmployeeId());
                centralAdmin.setHireDate(userDTO.getHireDate());
                break;
            case "LOCAL_ADMIN":
                LocalAdmin localAdmin = (LocalAdmin) existingUser;
                localAdmin.setSchoolName(userDTO.getSchoolName());
                localAdmin.setAdminCode(userDTO.getAdminCode());
                localAdmin.setEmployeeId(userDTO.getEmployeeId());
                localAdmin.setHireDate(userDTO.getHireDate());
                break;
            case "TEACHER":
                Teacher teacher = (Teacher) existingUser;
                teacher.setSubjectSpecialization(userDTO.getSubjectSpecialization());
                teacher.setEmployeeId(userDTO.getEmployeeId());
                teacher.setHireDate(userDTO.getHireDate());
                teacher.setQualification(userDTO.getQualification());
                teacher.setDepartment(userDTO.getDepartment());
                teacher.setTeacherRank(userDTO.getTeacherRank());
                teacher.setSchoolName(userDTO.getSchoolNameTeacher());
                break;
            case "STUDENT":
                Student student = (Student) existingUser;
                student.setStudentId(userDTO.getStudentId());
                student.setEnrollmentDate(userDTO.getEnrollmentDate());
                student.setGradeLevel(userDTO.getGradeLevel());
                student.setSchoolClass(userDTO.getSchoolClass());
                student.setParentName(userDTO.getParentName());
                student.setParentContact(userDTO.getParentContact());
                student.setPreviousSchool(userDTO.getPreviousSchool());
                student.setMedicalConditions(userDTO.getMedicalConditions());
                student.setStudentStatus(userDTO.getStudentStatus() != null ? userDTO.getStudentStatus() : student.getStudentStatus());
                student.setSchoolName(userDTO.getSchoolNameStudent());
                break;
        }

        // Update profile if provided (unchanged)
        if (userDTO.getProfileCode() != null) {
            Profile profile = profileRepository.findByCode(userDTO.getProfileCode())
                    .orElseThrow(() -> new IllegalArgumentException("Profile not found with code: " + userDTO.getProfileCode()));
            if (!"ACTIVE".equals(profile.getStatus())) {
                throw new IllegalStateException("Cannot assign inactive profile: " + userDTO.getProfileCode());
            }
            existingUser.setProfile(profile);
        }

        User savedUser = userRepository.save(existingUser);
        System.out.println("Saved user password for ID " + id + ": " + savedUser.getPassword());
        return savedUser;
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User assignProfile(Long userId, String profileCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        Profile profile = profileRepository.findByCode(profileCode)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found with code: " + profileCode));
        if (!"ACTIVE".equals(profile.getStatus())) {
            throw new IllegalStateException("Cannot assign inactive profile: " + profileCode);
        }
        user.setProfile(profile);
        return userRepository.save(user);
    }

    public User removeProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        user.setProfile(null);
        return userRepository.save(user);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}