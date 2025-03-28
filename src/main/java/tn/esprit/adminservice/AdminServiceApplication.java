package tn.esprit.adminservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.esprit.adminservice.entity.CentralAdmin; // Import the concrete subclass
import tn.esprit.adminservice.repository.UserRepository;

@SpringBootApplication
public class AdminServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.findByUsernameIgnoreCase("admin") == null) {
				CentralAdmin admin = new CentralAdmin(); // Use CentralAdmin instead of User
				admin.setUsername("admin");
				admin.setPassword(passwordEncoder.encode("password"));
				admin.setRole("CENTRAL_ADMIN"); // Align with your entity role structure
				admin.setEmail("admin@example.com"); // Required field
				admin.setStatus("Active"); // Required field
				admin.setFirstName("Admin"); // Required field
				admin.setLastName("User"); // Required field
				admin.setBirthdate(java.time.LocalDate.of(1980, 1, 1)); // Required field
				// Optional fields (can be set or left as null)
				admin.setDepartment("IT");
				admin.setAccessLevel("Superuser");
				admin.setEmployeeId("EMP001");
				admin.setHireDate(java.time.LocalDate.now());

				userRepository.save(admin);
			}
		};
	}
}