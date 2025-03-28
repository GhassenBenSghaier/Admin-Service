package tn.esprit.adminservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "central_admins")
@Data
public class CentralAdmin extends User {

    private String department; // e.g., "IT", "HR", "Finance"
    private String accessLevel; // e.g., "Superuser", "Restricted"
    private String employeeId;
    private LocalDate hireDate;

}