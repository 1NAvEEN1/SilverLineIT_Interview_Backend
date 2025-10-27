package org.example.samplespringboot.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.samplespringboot.entity.User;
import org.example.samplespringboot.entity.UserRole;
import org.example.samplespringboot.repository.UserRepository;
import org.example.samplespringboot.repository.UserRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Starting database initialization...");

        // Create roles if they don't exist
        createRoleIfNotExists("INSTRUCTOR");
        createRoleIfNotExists("STUDENT");

        // Create sample instructor user if not exists
        createSampleInstructor();

        log.info("Database initialization completed.");
    }

    private void createRoleIfNotExists(String roleName) {
        userRoleRepository.findByName(roleName).ifPresentOrElse(
                role -> log.info("Role '{}' already exists with ID: {}", roleName, role.getId()),
                () -> {
                    UserRole newRole = new UserRole();
                    newRole.setName(roleName);
                    UserRole savedRole = userRoleRepository.save(newRole);
                    log.info("Created role '{}' with ID: {}", roleName, savedRole.getId());
                }
        );
    }

    private void createSampleInstructor() {
        String email = "tharindunaveen9@gmail.com";

        if (userRepository.existsByEmail(email)) {
            log.info("Sample instructor user already exists with email: {}", email);
            return;
        }

        UserRole instructorRole = userRoleRepository.findByName("INSTRUCTOR")
                .orElseThrow(() -> new RuntimeException("INSTRUCTOR role not found"));

        User instructor = new User();
        instructor.setFirstName("Naveen");
        instructor.setLastName("Jayathilaka");
        instructor.setEmail(email);
        instructor.setPassword(passwordEncoder.encode("12345678"));
        instructor.setPhoneNumber("0769114074");
        instructor.setAddress("123, Address");
        instructor.setRole(instructorRole);

        User savedUser = userRepository.save(instructor);
        log.info("Created sample instructor user with email: {} and ID: {}", email, savedUser.getId());
    }
}

