package com.frdms.frdms.common.service;

import com.frdms.frdms.common.entity.DistributionOfficerAccount;
import com.frdms.frdms.common.repository.DistributionOfficerRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistributionOfficerUserDetailsService
        implements UserDetailsService {

    private final DistributionOfficerRepository repository;

    public DistributionOfficerUserDetailsService(
            DistributionOfficerRepository repository) {

        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        DistributionOfficerAccount account =
                repository.findByUsername(username)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "Distribution Officer not found"
                                )
                        );

        return new User(
                account.getUsername(),
                account.getPassword(),
                List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_DISTRIBUTION_OFFICER"
                        )
                )
        );
    }
}