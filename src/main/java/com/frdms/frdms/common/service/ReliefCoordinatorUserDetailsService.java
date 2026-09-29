package com.frdms.frdms.common.service;

import com.frdms.frdms.common.entity.ReliefCoordinatorAccount;
import com.frdms.frdms.common.repository.ReliefCoordinatorRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ReliefCoordinatorUserDetailsService
        implements UserDetailsService {

    private final ReliefCoordinatorRepository
            reliefCoordinatorRepository;


    public ReliefCoordinatorUserDetailsService(
            ReliefCoordinatorRepository reliefCoordinatorRepository) {

        this.reliefCoordinatorRepository =
                reliefCoordinatorRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        ReliefCoordinatorAccount account =
                reliefCoordinatorRepository
                        .findByUsername(username)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "Relief Coordinator not found"
                                )
                        );


        return User.builder()
                .username(account.getUsername())
                .password(account.getPassword())
                .authorities(
                        new SimpleGrantedAuthority(
                                "ROLE_RELIEF_COORDINATOR"
                        )
                )
                .build();
    }
}