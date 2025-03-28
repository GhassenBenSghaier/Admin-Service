package tn.esprit.adminservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "students")
@Data
public class Student extends User {

    private String studentId;
    private LocalDate enrollmentDate;
    private String gradeLevel;
    private String schoolClass;
    private String parentName;
    private String parentContact;
    private String previousSchool;
    private String medicalConditions;
    private String studentStatus; // e.g., "Enrolled", "Graduated", "Transferred"
    private String schoolName; // New field added
}