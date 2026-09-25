package com.frdms.frdms.config;

import com.frdms.frdms.common.entity.ProjectManagerAccount;
import com.frdms.frdms.common.repository.ProjectManagerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ProjectManagerDataInitializer implements CommandLineRunner {

    private final ProjectManagerRepository projectManagerRepository;
    private final PasswordEncoder passwordEncoder;

    public ProjectManagerDataInitializer(
            ProjectManagerRepository projectManagerRepository,
            PasswordEncoder passwordEncoder) {

        this.projectManagerRepository = projectManagerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        String email = "manager@frdms.com";

        if (!projectManagerRepository.existsByEmail(email)) {

            ProjectManagerAccount account =
                    new ProjectManagerAccount();

            account.setFullName("Project Manager");
            account.setEmail(email);

            account.setPassword(
                    passwordEncoder.encode("Admin123")
            );

            projectManagerRepository.save(account);

            System.out.println(
                    "Project Manager account created successfully."
            );
        }
    }
}