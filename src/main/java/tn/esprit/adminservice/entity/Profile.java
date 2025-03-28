package tn.esprit.adminservice.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "profiles")
@Data
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // e.g., "USER_MGR", "CAL_EDITOR"

    @Column(nullable = false)
    private String designation; // e.g., "User Manager", "Calendar Editor"

    @Column(nullable = false)
    private String status; // e.g., "ACTIVE", "INACTIVE"

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "profile_permissions",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();
}