package com.frdms.frdms.config;

import com.frdms.frdms.common.service.DonorUserDetailsService;
import com.frdms.frdms.common.service.FinanceOfficerUserDetailsService;
import com.frdms.frdms.common.service.ProjectManagerUserDetailsService;
import com.frdms.frdms.common.service.ReliefCoordinatorUserDetailsService;
import com.frdms.frdms.shelter_management.service.StaffUserDetailsService;
import com.frdms.frdms.volunteer_management.service.VolunteerUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.Customizer;
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
    private final VolunteerUserDetailsService volunteerUserDetailsService;
    private final StaffUserDetailsService staffUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SecurityConfig(
            ProjectManagerUserDetailsService projectManagerUserDetailsService,
            FinanceOfficerUserDetailsService financeOfficerUserDetailsService,
            DonorUserDetailsService donorUserDetailsService,
            ReliefCoordinatorUserDetailsService reliefCoordinatorUserDetailsService,
            VolunteerUserDetailsService volunteerUserDetailsService,
            StaffUserDetailsService staffUserDetailsService,
            PasswordEncoder passwordEncoder) {

        this.projectManagerUserDetailsService = projectManagerUserDetailsService;
        this.financeOfficerUserDetailsService = financeOfficerUserDetailsService;
        this.donorUserDetailsService = donorUserDetailsService;
        this.reliefCoordinatorUserDetailsService = reliefCoordinatorUserDetailsService;
        this.volunteerUserDetailsService = volunteerUserDetailsService;
        this.staffUserDetailsService = staffUserDetailsService;
        this.passwordEncoder = passwordEncoder;
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
    // VOLUNTEER AUTHENTICATION PROVIDER
    // =========================================================

    @Bean
    public DaoAuthenticationProvider volunteerAuthenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        volunteerUserDetailsService
                );

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    // =========================================================
    // STAFF AUTHENTICATION PROVIDER
    // =========================================================

    @Bean
    public DaoAuthenticationProvider staffAuthenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        staffUserDetailsService
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

                        // Return to COMMON login after logout
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
    // 4. VOLUNTEER
    // =========================================================

    @Bean
    @Order(4)
    public SecurityFilterChain volunteerSecurityFilterChain(
            HttpSecurity http)
            throws Exception {

        http
                .securityMatcher("/volunteer/**")

                .authenticationProvider(
                        volunteerAuthenticationProvider()
                )

                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/volunteer/register",
                                "/volunteer/login"
                        )
                        .permitAll()

                        .anyRequest()
                        .hasRole("VOLUNTEER")
                )

                .formLogin(form -> form

                        .loginPage(
                                "/volunteer/login"
                        )

                        .loginProcessingUrl(
                                "/volunteer/login"
                        )

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

                        .logoutUrl(
                                "/volunteer/logout"
                        )

                        .logoutSuccessUrl(
                                "/volunteer/login?logout=true"
                        )

                        .permitAll()
                );

        return http.build();
    }

    // =========================================================
    // 5. STAFF / SHELTER
    // =========================================================

    @Bean
    @Order(5)
    public SecurityFilterChain staffSecurityFilterChain(
            HttpSecurity http)
            throws Exception {

        http
                .securityMatcher("/staff/**")

                .authenticationProvider(
                        staffAuthenticationProvider()
                )

                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/staff/login"
                        )
                        .permitAll()

                        .anyRequest()
                        .hasRole("STAFF")
                )

                .formLogin(form -> form

                        .loginPage(
                                "/staff/login"
                        )

                        .loginProcessingUrl(
                                "/staff/login"
                        )

                        .usernameParameter("email")

                        .passwordParameter("password")

                        .defaultSuccessUrl(
                                "/staff/shelters",
                                true
                        )

                        .failureUrl(
                                "/staff/login?error=true"
                        )

                        .permitAll()
                )

                .logout(logout -> logout

                        .logoutUrl(
                                "/staff/logout"
                        )

                        .logoutSuccessUrl(
                                "/staff/login?logout=true"
                        )

                        .permitAll()
                );

        return http.build();
    }

    // =========================================================
    // 6. FINANCE OFFICER
    // =========================================================

    @Bean
    @Order(6)
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
    // 7. DONOR
    // =========================================================

    @Bean
    @Order(7)
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
    // 8. DONATION MANAGEMENT
    // =========================================================

    @Bean
    @Order(8)
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
    // 9. DEFAULT / PUBLIC / VICTIM / DISASTER
    // =========================================================

    @Bean
    @Order(9)
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