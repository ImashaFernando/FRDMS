package com.frdms.frdms.common.service;

import com.frdms.frdms.common.entity.ProjectManagerAccount;
import com.frdms.frdms.common.repository.ProjectManagerRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class ProjectManagerUserDetailsService
        implements UserDetailsService {

    private final ProjectManagerRepository projectManagerRepository;

    public ProjectManagerUserDetailsService(
            ProjectManagerRepository projectManagerRepository) {

        this.projectManagerRepository =
                projectManagerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        ProjectManagerAccount account =
                projectManagerRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "Project Manager account not found"
                                )
                        );

        return new User(
                account.getEmail(),
                account.getPassword(),
                Collections.singletonList(
                        new SimpleGrantedAuthority(
                                "ROLE_PROJECT_MANAGER"
                        )
                )
        );
    }
}