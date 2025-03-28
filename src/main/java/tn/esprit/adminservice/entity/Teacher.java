package tn.esprit.adminservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "teachers")
@Data
public class Teacher extends User {

    private String subjectSpecialization;
    private String employeeId;
    private LocalDate hireDate;
    private String qualification;
    private String department;
    private String teacherRank;
    private String schoolName; // New field added
}