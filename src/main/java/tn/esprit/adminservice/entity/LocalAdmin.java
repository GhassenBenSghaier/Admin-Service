package tn.esprit.adminservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "local_admins")
@Data
public class LocalAdmin extends User {

    private String schoolName;
    private String adminCode; // Unique code for local admin identification
    private String employeeId;
    private LocalDate hireDate;

}