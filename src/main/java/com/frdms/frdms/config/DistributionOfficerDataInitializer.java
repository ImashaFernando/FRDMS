package com.frdms.frdms.config;

import com.frdms.frdms.common.entity.DistributionOfficerAccount;
import com.frdms.frdms.common.repository.DistributionOfficerRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DistributionOfficerDataInitializer {

    @Bean
    CommandLineRunner createDistributionOfficer(
            DistributionOfficerRepository repository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            String username = "distributionofficer";

            if (!repository.existsByUsername(username)) {

                DistributionOfficerAccount account =
                        new DistributionOfficerAccount();

                account.setFullName("Distribution Officer");

                account.setUsername(username);

                account.setPassword(
                        passwordEncoder.encode("DO123")
                );

                repository.save(account);

                System.out.println(
                        "Distribution Officer account created successfully."
                );
            }
        };
    }
}