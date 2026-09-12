package com.frdms.frdms.shelter_management.service;

import com.frdms.frdms.shelter_management.entity.StaffAccount;
import com.frdms.frdms.shelter_management.repository.StaffRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class StaffUserDetailsService
        implements UserDetailsService {

    private final StaffRepository staffRepository;

    public StaffUserDetailsService(
            StaffRepository staffRepository
    ) {
        this.staffRepository = staffRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        StaffAccount staffAccount =
                staffRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "Staff account not found."
                                )
                        );

        return User.builder()
                .username(staffAccount.getEmail())
                .password(staffAccount.getPassword())
                .roles("STAFF")
                .build();
    }
}
