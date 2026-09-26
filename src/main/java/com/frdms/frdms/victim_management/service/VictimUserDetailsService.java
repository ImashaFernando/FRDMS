package com.frdms.frdms.victim_management.service;

import com.frdms.frdms.victim_management.entity.Victim;
import com.frdms.frdms.victim_management.repository.VictimRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class VictimUserDetailsService implements UserDetailsService {

    private final VictimRepository victimRepository;

    public VictimUserDetailsService(
            VictimRepository victimRepository) {

        this.victimRepository = victimRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        System.out.println(
                "🔥 LOGIN ATTEMPT - NIC: [" + username + "]"
        );

        Victim victim = victimRepository
                .findByNicNumber(username.trim())
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Victim not found with NIC: "
                                        + username
                        )
                );

        System.out.println(
                "✅ VICTIM FOUND - ID: "
                        + victim.getId()
        );

        System.out.println(
                "🔐 PASSWORD EXISTS: "
                        + (victim.getPassword() != null)
        );

        System.out.println(
                "🔐 PASSWORD LENGTH: "
                        + (victim.getPassword() == null
                        ? 0
                        : victim.getPassword().length())
        );

        return User.builder()
                .username(victim.getNicNumber())
                .password(victim.getPassword())
                .roles("VICTIM")
                .build();
    }
}