package com.frdms.frdms.config;

import com.frdms.frdms.common.service.DonorUserDetailsService;
import com.frdms.frdms.common.service.FinanceOfficerUserDetailsService;
import com.frdms.frdms.common.service.ProjectManagerUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final ProjectManagerUserDetailsService projectManagerUserDetailsService;
    private final FinanceOfficerUserDetailsService financeOfficerUserDetailsService;
    private final DonorUserDetailsService donorUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SecurityConfig(
            ProjectManagerUserDetailsService projectManagerUserDetailsService,
            FinanceOfficerUserDetailsService financeOfficerUserDetailsService,
            DonorUserDetailsService donorUserDetailsService,
            PasswordEncoder passwordEncoder) {

        this.projectManagerUserDetailsService =
                projectManagerUserDetailsService;

        this.financeOfficerUserDetailsService =
                financeOfficerUserDetailsService;

        this.donorUserDetailsService =
                donorUserDetailsService;

        this.passwordEncoder =
                passwordEncoder;
    }


    // =========================================================
    // 1. PROJECT MANAGER AUTHENTICATION PROVIDER
    // =========================================================

    @Bean
    public DaoAuthenticationProvider projectManagerAuthenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        projectManagerUserDetailsService
                );

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }


    // =========================================================
    // 2. FINANCE OFFICER AUTHENTICATION PROVIDER
    // =========================================================

    @Bean
    public DaoAuthenticationProvider financeOfficerAuthenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        financeOfficerUserDetailsService
                );

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }


    // =========================================================
    // 3. DONOR AUTHENTICATION PROVIDER
    // =========================================================

    @Bean
    public DaoAuthenticationProvider donorAuthenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        donorUserDetailsService
                );

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }


    // =========================================================
    // 4. COMMON AUTHENTICATION MANAGER
    // =========================================================

    @Bean
    public AuthenticationManager commonAuthenticationManager() {

        return new ProviderManager(
                List.of(
                        projectManagerAuthenticationProvider(),
                        financeOfficerAuthenticationProvider(),
                        donorAuthenticationProvider()
                )
        );
    }


    // =========================================================
    // 5. COMMON LOGIN
    // =========================================================

    @Bean
    @Order(1)
    public SecurityFilterChain commonLoginSecurityFilterChain(
            HttpSecurity http,
            AuthenticationManager commonAuthenticationManager)
            throws Exception {

        http
                .securityMatcher("/login")

                .authenticationManager(
                        commonAuthenticationManager
                )

                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login")
                        .permitAll()
                )

                .formLogin(form -> form

                        // COMMON LOGIN PAGE
                        .loginPage("/login")

                        // COMMON LOGIN PROCESSING URL
                        .loginProcessingUrl("/login")

                        // Login form field
                        .usernameParameter("username")
                        .passwordParameter("password")

                        // =================================================
                        // ROLE BASED REDIRECT
                        // =================================================

                        .successHandler(
                                (request, response, authentication) -> {

                                    boolean isPM =
                                            authentication
                                                    .getAuthorities()
                                                    .stream()
                                                    .anyMatch(authority ->
                                                            authority
                                                                    .getAuthority()
                                                                    .equals(
                                                                            "ROLE_PROJECT_MANAGER"
                                                                    )
                                                    );

                                    boolean isFinance =
                                            authentication
                                                    .getAuthorities()
                                                    .stream()
                                                    .anyMatch(authority ->
                                                            authority
                                                                    .getAuthority()
                                                                    .equals(
                                                                            "ROLE_FINANCE_OFFICER"
                                                                    )
                                                    );

                                    boolean isDonor =
                                            authentication
                                                    .getAuthorities()
                                                    .stream()
                                                    .anyMatch(authority ->
                                                            authority
                                                                    .getAuthority()
                                                                    .equals(
                                                                            "ROLE_DONOR"
                                                                    )
                                                    );


                                    // PROJECT MANAGER
                                    if (isPM) {

                                        response.sendRedirect(
                                                "/project-manager/dashboard"
                                        );

                                    }

                                    // FINANCE OFFICER
                                    else if (isFinance) {

                                        response.sendRedirect(
                                                "/finance/dashboard"
                                        );

                                    }

                                    // DONOR
                                    else if (isDonor) {

                                        response.sendRedirect(
                                                "/donor/dashboard"
                                        );

                                    }

                                    // UNKNOWN ROLE
                                    else {

                                        response.sendRedirect("/");
                                    }
                                }
                        )

                        .failureUrl("/login?error=true")

                        .permitAll()
                )


                // =========================================================
                // COMMON LOGOUT
                // =========================================================

                .logout(logout -> logout

                        .logoutUrl("/logout")

                        .logoutSuccessUrl("/login?logout=true")

                        .invalidateHttpSession(true)

                        .clearAuthentication(true)

                        .deleteCookies("JSESSIONID")

                        .permitAll()
                );


        return http.build();
    }


    // =========================================================
    // 6. PROJECT MANAGER
    // =========================================================

    @Bean
    @Order(2)
    public SecurityFilterChain projectManagerSecurityFilterChain(
            HttpSecurity http)
            throws Exception {

        http
                .securityMatcher(
                        "/project-manager/**",
                        "/pm/**"
                )

                .authenticationProvider(
                        projectManagerAuthenticationProvider()
                )

                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/access-denied",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        )
                        .permitAll()

                        .anyRequest()
                        .authenticated()
                )

                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/access-denied")
                );


        return http.build();
    }


    // =========================================================
    // 7. FINANCE OFFICER
    // =========================================================

    @Bean
    @Order(3)
    public SecurityFilterChain financeOfficerSecurityFilterChain(
            HttpSecurity http)
            throws Exception {

        http
                .securityMatcher(
                        "/finance-officer/**",
                        "/finance/**"
                )

                .authenticationProvider(
                        financeOfficerAuthenticationProvider()
                )

                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/access-denied",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        )
                        .permitAll()

                        .anyRequest()
                        .authenticated()
                )

                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/access-denied")
                );


        return http.build();
    }


    // =========================================================
    // 8. DONOR
    // =========================================================

    @Bean
    @Order(4)
    public SecurityFilterChain donorSecurityFilterChain(
            HttpSecurity http)
            throws Exception {

        http
                .securityMatcher(
                        "/donor/**"
                )

                .authenticationProvider(
                        donorAuthenticationProvider()
                )

                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/access-denied",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        )
                        .permitAll()

                        .anyRequest()
                        .authenticated()
                )

                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/access-denied")
                );


        return http.build();
    }


    // =========================================================
    // 9. DONATION MANAGEMENT
    // =========================================================

    @Bean
    @Order(5)
    public SecurityFilterChain donationSecurityFilterChain(
            HttpSecurity http)
            throws Exception {

        http
                .securityMatcher(
                        "/donations/**"
                )

                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .anyRequest()
                        .authenticated()
                )

                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/access-denied")
                );


        return http.build();
    }


    // =========================================================
    // 10. DEFAULT / PUBLIC
    // =========================================================

    @Bean
    @Order(6)
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/",
                                "/login",
                                "/access-denied",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        )
                        .permitAll()

                        .anyRequest()
                        .permitAll()
                );


        return http.build();
    }
}