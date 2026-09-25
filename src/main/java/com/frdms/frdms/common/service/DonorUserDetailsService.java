package com.frdms.frdms.common.service;

import com.frdms.frdms.donation_management.entity.Donor;
import com.frdms.frdms.donation_management.repository.DonorRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class DonorUserDetailsService implements UserDetailsService {

    private final DonorRepository donorRepository;

    public DonorUserDetailsService(
            DonorRepository donorRepository) {

        this.donorRepository = donorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Donor donor = donorRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Donor not found: " + username
                        ));

        return new User(
                donor.getUsername(),
                donor.getPassword(),
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_DONOR")
                )
        );
    }
}