package com.frdms.frdms.config;

import com.frdms.frdms.shelter_management.entity.StaffAccount;
import com.frdms.frdms.shelter_management.repository.StaffRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class StaffDataInitializer {

    @Bean
    CommandLineRunner createStaff(
            StaffRepository repository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            String email = "staff@frdms.com";

            if (!repository.existsByEmail(email)) {

                StaffAccount staff =
                        new StaffAccount();

                staff.setFullName("FRDMS Staff Member");

                staff.setEmail(email);

                staff.setPassword(
                        passwordEncoder.encode("Staff123")
                );

                repository.save(staff);

                System.out.println(
                        "Staff account created successfully."
                );
            }
        };
    }
}