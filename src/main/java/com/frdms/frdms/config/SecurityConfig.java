package com.frdms.frdms.config;

import com.frdms.frdms.common.service.DonorUserDetailsService;
import com.frdms.frdms.common.service.FinanceOfficerUserDetailsService;
import com.frdms.frdms.common.service.ProjectManagerUserDetailsService;
import com.frdms.frdms.common.service.ReliefCoordinatorUserDetailsService;

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
    private final ReliefCoordinatorUserDetailsService reliefCoordinatorUserDetailsService;
    private final PasswordEncoder passwordEncoder;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SecurityConfig(
            ProjectManagerUserDetailsService projectManagerUserDetailsService,
            FinanceOfficerUserDetailsService financeOfficerUserDetailsService,
            DonorUserDetailsService donorUserDetailsService,
            ReliefCoordinatorUserDetailsService reliefCoordinatorUserDetailsService,
            PasswordEncoder passwordEncoder) {

        this.projectManagerUserDetailsService =
                projectManagerUserDetailsService;

        this.financeOfficerUserDetailsService =
                financeOfficerUserDetailsService;

        this.donorUserDetailsService =
                donorUserDetailsService;

        this.reliefCoordinatorUserDetailsService =
                reliefCoordinatorUserDetailsService;

        this.passwordEncoder =
                passwordEncoder;
    }


    // =========================================================
    // PASSWORD ENCODER
    // =========================================================



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
    // RELIEF COORDINATOR AUTHENTICATION PROVIDER
    // =========================================================

    @Bean
    public DaoAuthenticationProvider reliefCoordinatorAuthenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        reliefCoordinatorUserDetailsService
                );

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }


    // =========================================================
    // COMMON AUTHENTICATION MANAGER
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
    // 1. COMMON LOGIN
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

                        .loginPage("/login")

                        .loginProcessingUrl("/login")

                        .usernameParameter("username")
                        .passwordParameter("password")

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

                                    if (isPM) {

                                        response.sendRedirect(
                                                "/project-manager/dashboard"
                                        );

                                    } else if (isFinance) {

                                        response.sendRedirect(
                                                "/finance/dashboard"
                                        );

                                    } else if (isDonor) {

                                        response.sendRedirect(
                                                "/donor/dashboard"
                                        );

                                    } else {

                                        response.sendRedirect("/");
                                    }
                                }
                        )

                        .failureUrl("/login?error=true")

                        .permitAll()
                )

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
    // 2. PROJECT MANAGER
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
                                "/project-manager/login",
                                "/access-denied",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        )
                        .permitAll()

                        .anyRequest()
                        .hasRole("PROJECT_MANAGER")
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

                .logout(logout -> logout

                        .logoutUrl(
                                "/project-manager/logout"
                        )

                        .logoutSuccessUrl(
                                "/login?logout=true"
                        )

                        .permitAll()
                )

                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/access-denied")
                );

        return http.build();
    }


    // =========================================================
    // 3. RELIEF COORDINATOR
    // =========================================================

    @Bean
    @Order(3)
    public SecurityFilterChain reliefCoordinatorSecurityFilterChain(
            HttpSecurity http)
            throws Exception {

        http
                .securityMatcher(
                        "/relief-coordinator/**"
                )

                .authenticationProvider(
                        reliefCoordinatorAuthenticationProvider()
                )

                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/relief-coordinator/login",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        )
                        .permitAll()

                        .anyRequest()
                        .hasRole("RELIEF_COORDINATOR")
                )

                .formLogin(form -> form

                        .loginPage(
                                "/relief-coordinator/login"
                        )

                        .loginProcessingUrl(
                                "/relief-coordinator/login"
                        )

                        .usernameParameter("username")

                        .passwordParameter("password")

                        .defaultSuccessUrl(
                                "/relief-coordinator/dashboard",
                                true
                        )

                        .failureUrl(
                                "/relief-coordinator/login?error=true"
                        )

                        .permitAll()
                )

                .logout(logout -> logout

                        .logoutUrl(
                                "/relief-coordinator/logout"
                        )

                        .logoutSuccessUrl(
                                "/relief-coordinator/login?logout=true"
                        )

                        .permitAll()
                );

        return http.build();
    }


    // =========================================================
    // 4. FINANCE OFFICER
    // =========================================================

    @Bean
    @Order(4)
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
    // 5. DONOR
    // =========================================================

    @Bean
    @Order(5)
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
    // 6. DONATION MANAGEMENT
    // =========================================================

    @Bean
    @Order(6)
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
    // 7. DEFAULT / PUBLIC / VICTIM / DISASTER
    // =========================================================

    @Bean
    @Order(7)
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http)
            throws Exception {

        http

                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // -----------------------------------------
                        // STATIC FILES
                        // -----------------------------------------

                        .requestMatchers(
                                "/",
                                "/login",
                                "/access-denied",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        )
                        .permitAll()


                        // -----------------------------------------
                        // VICTIM REGISTRATION
                        // -----------------------------------------

                        .requestMatchers(
                                "/victims/register"
                        )
                        .permitAll()


                        // -----------------------------------------
                        // VICTIM LOGIN
                        // -----------------------------------------

                        .requestMatchers(
                                "/victim-login",
                                "/victim-login/**"
                        )
                        .permitAll()


                        // -----------------------------------------
                        // VICTIM DASHBOARD / SESSION FUNCTIONS
                        // -----------------------------------------

                        .requestMatchers(
                                "/victim/**"
                        )
                        .permitAll()


                        // -----------------------------------------
                        // NEEDS ASSESSMENT - VICTIM SIDE
                        // -----------------------------------------

                        .requestMatchers(
                                "/needs-assessment/form",
                                "/needs-assessment/submit",
                                "/needs-assessment/status"
                        )
                        .permitAll()


                        // -----------------------------------------
                        // GN REPORT
                        // -----------------------------------------

                        .requestMatchers(
                                "/gn-reports/**"
                        )
                        .permitAll()


                        // -----------------------------------------
                        // DISASTER MANAGEMENT
                        // -----------------------------------------

                        .requestMatchers(
                                "/disasters/**",
                                "/affected-areas/**"
                        )
                        .hasRole("PROJECT_MANAGER")


                        // -----------------------------------------
                        // VICTIM MANAGEMENT - PROJECT MANAGER
                        // -----------------------------------------

                        .requestMatchers(
                                "/victims/**"
                        )
                        .hasRole("PROJECT_MANAGER")


                        // -----------------------------------------
                        // EVERYTHING ELSE
                        // -----------------------------------------

                        .anyRequest()
                        .authenticated()
                );

        return http.build();
    }
}