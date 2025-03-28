package tn.esprit.adminservice.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "role" // Use the 'role' field to determine the subclass
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CentralAdmin.class, name = "CENTRAL_ADMIN"),
        @JsonSubTypes.Type(value = LocalAdmin.class, name = "LOCAL_ADMIN"),
        @JsonSubTypes.Type(value = Teacher.class, name = "TEACHER"),
        @JsonSubTypes.Type(value = Student.class, name = "STUDENT")
})
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private LocalDate birthdate;

    private String gender;
    private String address;
    private String phoneNumber;
}