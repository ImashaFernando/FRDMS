package com.frdms.frdms.config;

import com.frdms.frdms.common.service.DonorUserDetailsService;
import com.frdms.frdms.common.service.FinanceOfficerUserDetailsService;
import com.frdms.frdms.common.service.ProjectManagerUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

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
    // PROJECT MANAGER AUTHENTICATION PROVIDER
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
    // FINANCE OFFICER AUTHENTICATION PROVIDER
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
    // DONOR AUTHENTICATION PROVIDER
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
    // 1. PROJECT MANAGER LOGIN
    // =========================================================

    @Bean
    @Order(1)
    public SecurityFilterChain projectManagerSecurityFilterChain(
            HttpSecurity http) throws Exception {

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
                                "/project-manager/login",
                                "/access-denied",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        )
                        .permitAll()

                        .anyRequest()
                        .authenticated()
                )

                .formLogin(form -> form

                        .loginPage(
                                "/project-manager/login"
                        )

                        .loginProcessingUrl(
                                "/project-manager/login"
                        )

                        .usernameParameter("email")

                        .passwordParameter("password")

                        .defaultSuccessUrl(
                                "/project-manager/dashboard",
                                true
                        )

                        .failureUrl(
                                "/project-manager/login?error=true"
                        )

                        .permitAll()
                )

                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/access-denied")
                )

                .logout(logout -> logout

                        .logoutUrl(
                                "/project-manager/logout"
                        )

                        .logoutSuccessUrl(
                                "/project-manager/login?logout=true"
                        )

                        .permitAll()
                );

        return http.build();
    }

    // =========================================================
    // 2. FINANCE OFFICER LOGIN
    // =========================================================

    @Bean
    @Order(2)
    public SecurityFilterChain financeOfficerSecurityFilterChain(
            HttpSecurity http) throws Exception {

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
                                "/finance-officer/login",
                                "/access-denied",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        )
                        .permitAll()

                        .anyRequest()
                        .authenticated()
                )

                .formLogin(form -> form

                        .loginPage(
                                "/finance-officer/login"
                        )

                        .loginProcessingUrl(
                                "/finance-officer/login"
                        )

                        .usernameParameter("email")

                        .passwordParameter("password")

                        .defaultSuccessUrl(
                                "/finance/dashboard",
                                true
                        )

                        .failureUrl(
                                "/finance-officer/login?error=true"
                        )

                        .permitAll()
                )

                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/access-denied")
                )

                .logout(logout -> logout

                        .logoutUrl(
                                "/finance-officer/logout"
                        )

                        .logoutSuccessUrl(
                                "/finance-officer/login?logout=true"
                        )

                        .permitAll()
                );

        return http.build();
    }

    // =========================================================
    // 3. DONOR LOGIN
    // =========================================================

    @Bean
    @Order(3)
    public SecurityFilterChain donorSecurityFilterChain(
            HttpSecurity http) throws Exception {

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
                                "/donor/login",
                                "/access-denied",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        )
                        .permitAll()

                        .anyRequest()
                        .authenticated()
                )

                .formLogin(form -> form

                        .loginPage(
                                "/donor/login"
                        )

                        .loginProcessingUrl(
                                "/donor/login"
                        )

                        .usernameParameter("username")

                        .passwordParameter("password")

                        .defaultSuccessUrl(
                                "/donor/dashboard",
                                true
                        )

                        .failureUrl(
                                "/donor/login?error=true"
                        )

                        .permitAll()
                )

                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/access-denied")
                )

                .logout(logout -> logout

                        .logoutUrl(
                                "/donor/logout"
                        )

                        .logoutSuccessUrl(
                                "/donor/login?logout=true"
                        )

                        .permitAll()
                );

        return http.build();
    }

    // =========================================================
    // 4. DONATION MANAGEMENT
    // =========================================================

    @Bean
    @Order(4)
    public SecurityFilterChain donationSecurityFilterChain(
            HttpSecurity http) throws Exception {

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
    // 5. DEFAULT / PUBLIC
    // =========================================================

    @Bean
    @Order(5)
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/",
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