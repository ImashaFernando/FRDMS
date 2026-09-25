package com.frdms.frdms.config;

import com.frdms.frdms.common.entity.ReliefCoordinatorAccount;
import com.frdms.frdms.common.repository.ReliefCoordinatorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ReliefCoordinatorDataInitializer {

    @Bean
    CommandLineRunner createReliefCoordinator(
            ReliefCoordinatorRepository repository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            String username = "reliefcoordinator";

            if (!repository.existsByUsername(username)) {

                ReliefCoordinatorAccount account =
                        new ReliefCoordinatorAccount();

                account.setFullName("Relief Coordinator");

                account.setUsername(username);

                account.setPassword(
                        passwordEncoder.encode("RC123")
                );

                repository.save(account);

                System.out.println(
                        "Relief Coordinator account created successfully."
                );
            }
        };
    }
}