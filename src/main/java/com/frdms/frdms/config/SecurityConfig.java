package com.frdms.frdms.config;

import com.frdms.frdms.volunteer_management.service.VolunteerUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final VolunteerUserDetailsService volunteerUserDetailsService;

    public SecurityConfig(
            VolunteerUserDetailsService volunteerUserDetailsService
    ) {
        this.volunteerUserDetailsService = volunteerUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(volunteerUserDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .authenticationProvider(authenticationProvider())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/volunteer/register",
                                "/volunteer/login",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        .requestMatchers("/volunteer/**")
                        .hasRole("VOLUNTEER")

                        .anyRequest()
                        .authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/volunteer/login")
                        .loginProcessingUrl("/volunteer/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl(
                                "/volunteer/profile",
                                true
                        )
                        .failureUrl(
                                "/volunteer/login?error=true"
                        )
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/volunteer/logout")
                        .logoutSuccessUrl("/volunteer/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }
}