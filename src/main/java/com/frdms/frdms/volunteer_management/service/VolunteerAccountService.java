package com.frdms.frdms.volunteer_management.service;

import com.frdms.frdms.volunteer_management.entity.VolunteerAccount;
import com.frdms.frdms.volunteer_management.repository.VolunteerAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VolunteerAccountService {

    private final VolunteerAccountRepository volunteerAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public VolunteerAccountService(
            VolunteerAccountRepository volunteerAccountRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.volunteerAccountRepository = volunteerAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public VolunteerAccount registerVolunteerAccount(
            VolunteerAccount volunteerAccount
    ) {

        if (volunteerAccountRepository.existsByEmail(volunteerAccount.getEmail())) {
            throw new IllegalArgumentException(
                    "An account with this email already exists."
            );
        }

        String encodedPassword =
                passwordEncoder.encode(volunteerAccount.getPassword());

        volunteerAccount.setPassword(encodedPassword);

        return volunteerAccountRepository.save(volunteerAccount);
    }

    public boolean emailExists(String email) {
        return volunteerAccountRepository.existsByEmail(email);
    }

    public Optional<VolunteerAccount> findByEmail(String email) {
        return volunteerAccountRepository.findByEmail(email);
    }
}