package tn.esprit.adminservice.dto;

import java.time.LocalDate;



public class UserDTO {
    // Common fields
    private String username;
    private String password;
    private String role;
    private String profileCode;
    private String email;
    private String status;
    private String firstName;
    private String lastName;
    private LocalDate birthdate;
    private String gender;
    private String address;
    private String phoneNumber;

    // CentralAdmin fields
    private String department;
    private String accessLevel;
    private String employeeId;
    private LocalDate hireDate;

    // LocalAdmin fields
    private String schoolName;
    private String adminCode;

    // Teacher fields
    private String subjectSpecialization;
    private String qualification;
    private String teacherRank;
    private String schoolNameTeacher; // Differentiated for clarity, can reuse schoolName if preferred

    // Student fields
    private String studentId;
    private LocalDate enrollmentDate;
    private String gradeLevel;
    private String schoolClass;
    private String parentName;
    private String parentContact;
    private String previousSchool;
    private String medicalConditions;
    private String studentStatus;
    private String schoolNameStudent; // Differentiated for clarity, can reuse schoolName if preferred

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public LocalDate getBirthdate() { return birthdate; }
    public void setBirthdate(LocalDate birthdate) { this.birthdate = birthdate; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getProfileCode() { return profileCode; }
    public void setProfileCode(String profileCode) { this.profileCode = profileCode; }

    // CentralAdmin
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getAccessLevel() { return accessLevel; }
    public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    // LocalAdmin
    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
    public String getAdminCode() { return adminCode; }
    public void setAdminCode(String adminCode) { this.adminCode = adminCode; }

    // Teacher
    public String getSubjectSpecialization() { return subjectSpecialization; }
    public void setSubjectSpecialization(String subjectSpecialization) { this.subjectSpecialization = subjectSpecialization; }
    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }
    public String getTeacherRank() { return teacherRank; }
    public void setTeacherRank(String teacherRank) { this.teacherRank = teacherRank; }
    public String getSchoolNameTeacher() { return schoolNameTeacher; }
    public void setSchoolNameTeacher(String schoolNameTeacher) { this.schoolNameTeacher = schoolNameTeacher; }

    // Student
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    public String getGradeLevel() { return gradeLevel; }
    public void setGradeLevel(String gradeLevel) { this.gradeLevel = gradeLevel; }
    public String getSchoolClass() { return schoolClass; }
    public void setSchoolClass(String schoolClass) { this.schoolClass = schoolClass; }
    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }
    public String getParentContact() { return parentContact; }
    public void setParentContact(String parentContact) { this.parentContact = parentContact; }
    public String getPreviousSchool() { return previousSchool; }
    public void setPreviousSchool(String previousSchool) { this.previousSchool = previousSchool; }
    public String getMedicalConditions() { return medicalConditions; }
    public void setMedicalConditions(String medicalConditions) { this.medicalConditions = medicalConditions; }
    public String getStudentStatus() { return studentStatus; }
    public void setStudentStatus(String studentStatus) { this.studentStatus = studentStatus; }
    public String getSchoolNameStudent() { return schoolNameStudent; }
    public void setSchoolNameStudent(String schoolNameStudent) { this.schoolNameStudent = schoolNameStudent; }
}