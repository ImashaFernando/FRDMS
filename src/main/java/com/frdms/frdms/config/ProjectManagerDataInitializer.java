package com.frdms.frdms.config;

import com.frdms.frdms.common.entity.ProjectManagerAccount;
import com.frdms.frdms.common.repository.ProjectManagerRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ProjectManagerDataInitializer {

    @Bean
    CommandLineRunner createProjectManager(
            ProjectManagerRepository projectManagerRepository,
            PasswordEncoder passwordEncoder
    ) {

        return args -> {

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

            } else {

                System.out.println(
                        "Project Manager account already exists."
                );
            }
        };
    }
}