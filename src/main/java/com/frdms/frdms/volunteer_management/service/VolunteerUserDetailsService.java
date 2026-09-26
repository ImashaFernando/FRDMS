package com.frdms.frdms.volunteer_management.service;

import com.frdms.frdms.volunteer_management.entity.VolunteerAccount;
import com.frdms.frdms.volunteer_management.repository.VolunteerAccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class VolunteerUserDetailsService implements UserDetailsService {

    private final VolunteerAccountRepository volunteerAccountRepository;

    public VolunteerUserDetailsService(
            VolunteerAccountRepository volunteerAccountRepository
    ) {
        this.volunteerAccountRepository = volunteerAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        VolunteerAccount account =
                volunteerAccountRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "Volunteer account not found"
                                )
                        );

        return User.builder()
                .username(account.getEmail())
                .password(account.getPassword())
                .roles("VOLUNTEER")
                .build();
    }
}