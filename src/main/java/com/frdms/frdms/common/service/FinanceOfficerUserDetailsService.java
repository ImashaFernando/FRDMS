package com.frdms.frdms.common.service;

import com.frdms.frdms.common.entity.FinanceOfficerAccount;
import com.frdms.frdms.common.repository.FinanceOfficerAccountRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class FinanceOfficerUserDetailsService
        implements UserDetailsService {

    private final FinanceOfficerAccountRepository repository;

    public FinanceOfficerUserDetailsService(
            FinanceOfficerAccountRepository repository) {

        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        FinanceOfficerAccount account =
                repository.findByEmail(email)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "Finance Officer not found"
                                )
                        );

        return new User(
                account.getEmail(),
                account.getPassword(),
                Collections.singletonList(
                        new SimpleGrantedAuthority(
                                "ROLE_FINANCE_OFFICER"
                        )
                )
        );
    }
}