package com.frdms.frdms.config;

import com.frdms.frdms.common.entity.FinanceOfficerAccount;
import com.frdms.frdms.common.repository.FinanceOfficerAccountRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class FinanceOfficerDataInitializer {

    @Bean
    CommandLineRunner createFinanceOfficer(
            FinanceOfficerAccountRepository repository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            String email = "finance@frdms.com";

            if (repository.findByEmail(email).isEmpty()) {

                FinanceOfficerAccount account =
                        new FinanceOfficerAccount();

                account.setEmail(email);

                account.setPassword(
                        passwordEncoder.encode("Finance123")
                );

                repository.save(account);

                System.out.println(
                        "Finance Officer account created successfully."
                );
            }
        };
    }
}