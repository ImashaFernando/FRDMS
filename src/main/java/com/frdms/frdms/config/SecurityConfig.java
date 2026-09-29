package com.frdms.frdms.config;

import com.frdms.frdms.victim_management.repository.VictimRepository;
import com.frdms.frdms.victim_management.service.VictimUserDetailsService;
import com.frdms.frdms.common.service.DonorUserDetailsService;
import com.frdms.frdms.common.service.FinanceOfficerUserDetailsService;
import com.frdms.frdms.common.service.ProjectManagerUserDetailsService;
import com.frdms.frdms.common.service.ReliefCoordinatorUserDetailsService;
import com.frdms.frdms.shelter_management.service.StaffUserDetailsService;
import com.frdms.frdms.volunteer_management.service.VolunteerUserDetailsService;
import com.frdms.frdms.common.service.DistributionOfficerUserDetailsService;

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

    // =========================================================
    // USER DETAILS SERVICES
    // =========================================================

    private final ProjectManagerUserDetailsService
            projectManagerUserDetailsService;

    private final FinanceOfficerUserDetailsService
            financeOfficerUserDetailsService;

    private final DonorUserDetailsService
            donorUserDetailsService;

    private final ReliefCoordinatorUserDetailsService
            reliefCoordinatorUserDetailsService;

    private final VolunteerUserDetailsService
            volunteerUserDetailsService;

    private final StaffUserDetailsService
            staffUserDetailsService;

    private final DistributionOfficerUserDetailsService
            distributionOfficerUserDetailsService;

    private final VictimUserDetailsService
            victimUserDetailsService;

    private final PasswordEncoder passwordEncoder;

    private final VictimRepository victimRepository;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SecurityConfig(
            ProjectManagerUserDetailsService
                    projectManagerUserDetailsService,

            FinanceOfficerUserDetailsService
                    financeOfficerUserDetailsService,

            DonorUserDetailsService
                    donorUserDetailsService,

            ReliefCoordinatorUserDetailsService
                    reliefCoordinatorUserDetailsService,

            VolunteerUserDetailsService
                    volunteerUserDetailsService,

            StaffUserDetailsService
                    staffUserDetailsService,

            DistributionOfficerUserDetailsService
                    distributionOfficerUserDetailsService,

            VictimUserDetailsService
                    victimUserDetailsService,

            PasswordEncoder passwordEncoder,

            VictimRepository victimRepository) {

        this.projectManagerUserDetailsService =
                projectManagerUserDetailsService;

        this.financeOfficerUserDetailsService =
                financeOfficerUserDetailsService;

        this.donorUserDetailsService =
                donorUserDetailsService;

        this.reliefCoordinatorUserDetailsService =
                reliefCoordinatorUserDetailsService;

        this.volunteerUserDetailsService =
                volunteerUserDetailsService;

        this.staffUserDetailsService =
                staffUserDetailsService;

        this.distributionOfficerUserDetailsService =
                distributionOfficerUserDetailsService;

        this.victimUserDetailsService =
                victimUserDetailsService;

        this.passwordEncoder =
                passwordEncoder;

        this.victimRepository =
                victimRepository;
    }


    // =========================================================
    // 1. PROJECT MANAGER AUTHENTICATION PROVIDER
    // =========================================================

    @Bean
    public DaoAuthenticationProvider
    projectManagerAuthenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        projectManagerUserDetailsService
                );

        provider.setPasswordEncoder(
                passwordEncoder
        );

        return provider;
    }


    // =========================================================
    // 2. FINANCE OFFICER AUTHENTICATION PROVIDER
    // =========================================================

    @Bean
    public DaoAuthenticationProvider
    financeOfficerAuthenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        financeOfficerUserDetailsService
                );

        provider.setPasswordEncoder(
                passwordEncoder
        );

        return provider;
    }


    // =========================================================
    // 3. DONOR AUTHENTICATION PROVIDER
    // =========================================================

    @Bean
    public DaoAuthenticationProvider
    donorAuthenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        donorUserDetailsService
                );

        provider.setPasswordEncoder(
                passwordEncoder
        );

        return provider;
    }


    // =========================================================
    // 4. RELIEF COORDINATOR AUTHENTICATION PROVIDER
    // =========================================================

    @Bean
    public DaoAuthenticationProvider
    reliefCoordinatorAuthenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        reliefCoordinatorUserDetailsService
                );

        provider.setPasswordEncoder(
                passwordEncoder
        );

        return provider;
    }


    // =========================================================
    // 5. DISTRIBUTION OFFICER AUTHENTICATION PROVIDER
    // =========================================================

    @Bean
    public DaoAuthenticationProvider
    distributionOfficerAuthenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        distributionOfficerUserDetailsService
                );

        provider.setPasswordEncoder(
                passwordEncoder
        );

        return provider;
    }


    // =========================================================
    // 6. VOLUNTEER AUTHENTICATION PROVIDER
    // =========================================================

    @Bean
    public DaoAuthenticationProvider
    volunteerAuthenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        volunteerUserDetailsService
                );

        provider.setPasswordEncoder(
                passwordEncoder
        );

        return provider;
    }


    // =========================================================
    // 7. VICTIM AUTHENTICATION PROVIDER
    // =========================================================

    @Bean
    public DaoAuthenticationProvider
    victimAuthenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        victimUserDetailsService
                );

        provider.setPasswordEncoder(
                passwordEncoder
        );

        return provider;
    }


    // =========================================================
    // 8. STAFF AUTHENTICATION PROVIDER
    // =========================================================

    @Bean
    public DaoAuthenticationProvider
    staffAuthenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        staffUserDetailsService
                );

        provider.setPasswordEncoder(
                passwordEncoder
        );

        return provider;
    }


    // =========================================================
    // COMMON AUTHENTICATION MANAGER
    // =========================================================

    @Bean
    public AuthenticationManager
    commonAuthenticationManager() {

        return new ProviderManager(
                List.of(

                        // IMPORTANT:
                        // Victim provider first
                        victimAuthenticationProvider(),

                        projectManagerAuthenticationProvider(),

                        financeOfficerAuthenticationProvider(),

                        donorAuthenticationProvider(),

                        reliefCoordinatorAuthenticationProvider(),

                        distributionOfficerAuthenticationProvider(),

                        volunteerAuthenticationProvider(),

                        staffAuthenticationProvider()
                )
        );
    }


    // =========================================================
    // 1. COMMON LOGIN
    // =========================================================

    @Bean
    @Order(1)
    public SecurityFilterChain
    commonLoginSecurityFilterChain(
            HttpSecurity http,
            AuthenticationManager
                    commonAuthenticationManager)
            throws Exception {

        http

                // -------------------------------------------------
                // COMMON LOGIN + LOGOUT
                // -------------------------------------------------

                .securityMatcher(
                        "/login",
                        "/logout"
                )

                .authenticationManager(
                        commonAuthenticationManager
                )

                // -------------------------------------------------
                // CSRF
                // -------------------------------------------------

                .csrf(csrf -> csrf.disable())

                // -------------------------------------------------
                // PUBLIC LOGIN
                // -------------------------------------------------

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/login",
                                "/logout"
                        )
                        .permitAll()
                )

                // -------------------------------------------------
                // COMMON LOGIN
                // -------------------------------------------------

                .formLogin(form -> form

                        .loginPage(
                                "/login"
                        )

                        .loginProcessingUrl(
                                "/login"
                        )

                        .usernameParameter(
                                "username"
                        )

                        .passwordParameter(
                                "password"
                        )

                        // =========================================
                        // SUCCESS HANDLER
                        // =========================================

                        .successHandler(
                                (request,
                                 response,
                                 authentication) -> {

                                    System.out.println(
                                            "================================"
                                    );

                                    System.out.println(
                                            "✅ LOGIN SUCCESS"
                                    );

                                    System.out.println(
                                            "USERNAME: "
                                                    + authentication
                                                    .getName()
                                    );

                                    System.out.println(
                                            "AUTHORITIES: "
                                                    + authentication
                                                    .getAuthorities()
                                    );

                                    System.out.println(
                                            "================================"
                                    );


                                    // ---------------------------------
                                    // PROJECT MANAGER
                                    // ---------------------------------

                                    boolean isPM =
                                            authentication
                                                    .getAuthorities()
                                                    .stream()
                                                    .anyMatch(
                                                            authority ->
                                                                    authority
                                                                            .getAuthority()
                                                                            .equals(
                                                                                    "ROLE_PROJECT_MANAGER"
                                                                            )
                                                    );


                                    // ---------------------------------
                                    // FINANCE OFFICER
                                    // ---------------------------------

                                    boolean isFinance =
                                            authentication
                                                    .getAuthorities()
                                                    .stream()
                                                    .anyMatch(
                                                            authority ->
                                                                    authority
                                                                            .getAuthority()
                                                                            .equals(
                                                                                    "ROLE_FINANCE_OFFICER"
                                                                            )
                                                    );


                                    // ---------------------------------
                                    // DONOR
                                    // ---------------------------------

                                    boolean isDonor =
                                            authentication
                                                    .getAuthorities()
                                                    .stream()
                                                    .anyMatch(
                                                            authority ->
                                                                    authority
                                                                            .getAuthority()
                                                                            .equals(
                                                                                    "ROLE_DONOR"
                                                                            )
                                                    );


                                    // ---------------------------------
                                    // VOLUNTEER
                                    // ---------------------------------

                                    boolean isVolunteer =
                                            authentication
                                                    .getAuthorities()
                                                    .stream()
                                                    .anyMatch(
                                                            authority ->
                                                                    authority
                                                                            .getAuthority()
                                                                            .equals(
                                                                                    "ROLE_VOLUNTEER"
                                                                            )
                                                    );


                                    // ---------------------------------
                                    // RELIEF COORDINATOR
                                    // ---------------------------------

                                    boolean isReliefCoordinator =
                                            authentication
                                                    .getAuthorities()
                                                    .stream()
                                                    .anyMatch(
                                                            authority ->
                                                                    authority
                                                                            .getAuthority()
                                                                            .equals(
                                                                                    "ROLE_RELIEF_COORDINATOR"
                                                                            )
                                                    );


                                    // ---------------------------------
                                    // DISTRIBUTION OFFICER
                                    // ---------------------------------

                                    boolean isDistributionOfficer =
                                            authentication
                                                    .getAuthorities()
                                                    .stream()
                                                    .anyMatch(
                                                            authority ->
                                                                    authority
                                                                            .getAuthority()
                                                                            .equals(
                                                                                    "ROLE_DISTRIBUTION_OFFICER"
                                                                            )
                                                    );


                                    // ---------------------------------
                                    // VICTIM
                                    // ---------------------------------

                                    boolean isVictim =
                                            authentication
                                                    .getAuthorities()
                                                    .stream()
                                                    .anyMatch(
                                                            authority ->
                                                                    authority
                                                                            .getAuthority()
                                                                            .equals(
                                                                                    "ROLE_VICTIM"
                                                                            )
                                                    );


                                    // ---------------------------------
                                    // STAFF
                                    // ---------------------------------

                                    boolean isStaff =
                                            authentication
                                                    .getAuthorities()
                                                    .stream()
                                                    .anyMatch(
                                                            authority ->
                                                                    "ROLE_STAFF"
                                                                            .equals(
                                                                                    authority
                                                                                            .getAuthority()
                                                                            )
                                                    );


                                    // =================================
                                    // ROLE REDIRECTS
                                    // =================================

                                    if (isPM) {

                                        response.sendRedirect(
                                                "/project-manager/dashboard"
                                        );

                                    }

                                    else if (isFinance) {

                                        response.sendRedirect(
                                                "/finance/dashboard"
                                        );

                                    }

                                    else if (isDonor) {

                                        response.sendRedirect(
                                                "/donor/dashboard"
                                        );

                                    }

                                    else if (isVolunteer) {

                                        response.sendRedirect(
                                                "/volunteer/profile"
                                        );

                                    }

                                    else if (isReliefCoordinator) {

                                        response.sendRedirect(
                                                "/relief-coordinator/dashboard"
                                        );

                                    }

                                    else if (isDistributionOfficer) {

                                        response.sendRedirect(
                                                "/distribution/dashboard"
                                        );

                                    }

                                    else if (isStaff) {

                                        response.sendRedirect(
                                                "/staff/shelters"
                                        );

                                    }

                                    // =================================
                                    // VICTIM LOGIN
                                    // =================================

                                    else if (isVictim) {

                                        String nic =
                                                authentication
                                                        .getName();

                                        System.out.println(
                                                "🔍 Loading victim for NIC: "
                                                        + nic
                                        );

                                        victimRepository
                                                .findByNicNumber(
                                                        nic
                                                )
                                                .ifPresent(
                                                        victim -> {

                                                            // -------------------------
                                                            // SAVE VICTIM ID
                                                            // -------------------------

                                                            request
                                                                    .getSession()
                                                                    .setAttribute(
                                                                            "victimId",
                                                                            victim.getId()
                                                                    );


                                                            // -------------------------
                                                            // SAVE VICTIM NIC
                                                            // -------------------------

                                                            request
                                                                    .getSession()
                                                                    .setAttribute(
                                                                            "victimNic",
                                                                            victim.getNicNumber()
                                                                    );


                                                            // -------------------------
                                                            // SAVE VICTIM NAME
                                                            // -------------------------

                                                            request
                                                                    .getSession()
                                                                    .setAttribute(
                                                                            "victimName",
                                                                            victim.getFullName()
                                                                    );


                                                            // -------------------------
                                                            // SAVE DISASTER ID
                                                            // -------------------------

                                                            if (
                                                                    victim.getDisaster()
                                                                            != null
                                                            ) {

                                                                request
                                                                        .getSession()
                                                                        .setAttribute(
                                                                                "victimDisasterId",
                                                                                victim
                                                                                        .getDisaster()
                                                                                        .getId()
                                                                        );
                                                            }


                                                            System.out.println(
                                                                    "✅ VICTIM SESSION CREATED"
                                                            );

                                                            System.out.println(
                                                                    "Victim ID: "
                                                                            + victim.getId()
                                                            );

                                                            System.out.println(
                                                                    "Victim NIC: "
                                                                            + victim.getNicNumber()
                                                            );

                                                            System.out.println(
                                                                    "Victim Name: "
                                                                            + victim.getFullName()
                                                            );
                                                        }
                                                );


                                        System.out.println(
                                                "➡️ REDIRECTING TO /victim/dashboard"
                                        );

                                        response.sendRedirect(
                                                "/victim/dashboard"
                                        );
                                    }

                                    else {

                                        response.sendRedirect(
                                                "/"
                                        );
                                    }
                                }
                        )

                        // =========================================
                        // LOGIN FAILURE
                        // =========================================

                        .failureHandler(
                                (request,
                                 response,
                                 exception) -> {

                                    System.out.println(
                                            "================================"
                                    );

                                    System.out.println(
                                            "❌ LOGIN FAILED"
                                    );

                                    System.out.println(
                                            "USERNAME: "
                                                    + request
                                                    .getParameter(
                                                            "username"
                                                    )
                                    );

                                    System.out.println(
                                            "EXCEPTION: "
                                                    + exception
                                                    .getClass()
                                                    .getName()
                                    );

                                    System.out.println(
                                            "MESSAGE: "
                                                    + exception
                                                    .getMessage()
                                    );

                                    System.out.println(
                                            "================================"
                                    );

                                    response.sendRedirect(
                                            "/login?error=true"
                                    );
                                }
                        )

                        .permitAll()
                )

                // -------------------------------------------------
                // COMMON LOGOUT
                // -------------------------------------------------

                .logout(logout -> logout

                        .logoutUrl(
                                "/logout"
                        )

                        .logoutSuccessUrl(
                                "/?logout=true"
                        )

                        .invalidateHttpSession(
                                true
                        )

                        .clearAuthentication(
                                true
                        )

                        .deleteCookies(
                                "JSESSIONID"
                        )

                        .permitAll()
                );


        return http.build();
    }


    // =========================================================
    // 2. PROJECT MANAGER
    // =========================================================

    @Bean
    @Order(2)
    public SecurityFilterChain
    projectManagerSecurityFilterChain(
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
                        .hasRole(
                                "PROJECT_MANAGER"
                        )
                )

                .formLogin(form -> form

                        .loginPage(
                                "/project-manager/login"
                        )

                        .loginProcessingUrl(
                                "/project-manager/login"
                        )

                        .usernameParameter(
                                "email"
                        )

                        .passwordParameter(
                                "password"
                        )

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

                .exceptionHandling(exception ->
                        exception
                                .accessDeniedPage(
                                        "/access-denied"
                                )
                );

        return http.build();
    }


    // =========================================================
    // 3. RELIEF COORDINATOR
    // =========================================================

    @Bean
    @Order(3)
    public SecurityFilterChain
    reliefCoordinatorSecurityFilterChain(
            HttpSecurity http)
            throws Exception {

        http

                .securityMatcher(
                        "/relief-coordinator/**"
                )

                .csrf(csrf -> csrf.disable())

                ==================================================
        // OTHER FRDMS URLS
        // =========================================================

        @Bean
        @Order(4)
        public SecurityFilterChain defaultSecurityFilterChain(
                HttpSecurity http
    ) throws Exception {

            http
                    =======

                .authorizeHttpRequests(auth -> auth

                    .requestMatchers(
                            "/css/**",
                            "/js/**",
                            "/images/**"
                    )
                    .permitAll()

                    .anyRequest()
                    .hasRole(
                            "RELIEF_COORDINATOR"
                    )
            )

                    .exceptionHandling(exception ->
                            exception
                                    .accessDeniedPage(
                                            "/access-denied"
                                    )
                    );

            return http.build();
        }


        // =========================================================
        // 4. VOLUNTEER
        // =========================================================

        @Bean
        @Order(4)
        public SecurityFilterChain
        volunteerSecurityFilterChain(
                HttpSecurity http)
            throws Exception {

            http

                    .securityMatcher(
                            "/volunteer/**"
                    )

                    .csrf(csrf -> csrf.disable())

                    .authorizeHttpRequests(auth -> auth

                            .requestMatchers(
                                    "/volunteer/register",
                                    "/css/**",
                                    "/js/**",
                                    "/images/**"
                            )
                            .permitAll()

                            .anyRequest()
                            .hasRole(
                                    "VOLUNTEER"
                            )
                    )

                    .exceptionHandling(exception ->
                            exception
                                    .accessDeniedPage(
                                            "/access-denied"
                                    )
                    );

            return http.build();
        }


        // =========================================================
        // 5. STAFF / SHELTER
        // =========================================================

        @Bean
        @Order(5)
        public SecurityFilterChain
        staffSecurityFilterChain(
                HttpSecurity http)
            throws Exception {

            http

                    .securityMatcher(
                            "/staff/**"
                    )

                    .csrf(csrf -> csrf.disable())

                    .authorizeHttpRequests(auth -> auth

                            .requestMatchers(
                                    "/css/**",
                                    "/js/**",
                                    "/images/**"
                            )
                            .permitAll()

                            .anyRequest()
                            .hasRole(
                                    "STAFF"
                            )
                    )

                    .exceptionHandling(exception ->
                            exception
                                    .accessDeniedPage(
                                            "/access-denied"
                                    )
                    );

            return http.build();
        }


        // =========================================================
        // 6. FINANCE OFFICER
        // =========================================================

        @Bean
        @Order(6)
        public SecurityFilterChain
        financeOfficerSecurityFilterChain(
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

                    .exceptionHandling(exception ->
                            exception
                                    .accessDeniedPage(
                                            "/access-denied"
                                    )
                    );

            return http.build();
        }


        // =========================================================
        // 7. DONOR
        // =========================================================

        @Bean
        @Order(7)
        public SecurityFilterChain
        donorSecurityFilterChain(
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

                    .exceptionHandling(exception ->
                            exception
                                    .accessDeniedPage(
                                            "/access-denied"
                                    )
                    );

            return http.build();
        }


        // =========================================================
        // 8. DONATION MANAGEMENT
        // =========================================================

        @Bean
        @Order(8)
        public SecurityFilterChain
        donationSecurityFilterChain(
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

                    .exceptionHandling(exception ->
                            exception
                                    .accessDeniedPage(
                                            "/access-denied"
                                    )
                    );

            return http.build();
        }


        // =========================================================
        // 9. DISTRIBUTION OFFICER
        // =========================================================

        @Bean
        @Order(9)
        public SecurityFilterChain
        distributionOfficerSecurityFilterChain(
                HttpSecurity http)
            throws Exception {

            http

                    .securityMatcher(
                            "/distribution/**"
                    )

                    .csrf(csrf -> csrf.disable())

                    .authorizeHttpRequests(auth -> auth

                            .requestMatchers(
                                    "/css/**",
                                    "/js/**",
                                    "/images/**"
                            )
                            .permitAll()

                            .anyRequest()
                            .hasRole(
                                    "DISTRIBUTION_OFFICER"
                            )
                    )

                    .exceptionHandling(exception ->
                            exception
                                    .accessDeniedPage(
                                            "/access-denied"
                                    )
                    );

            return http.build();
        }


        // =========================================================
        // 10. DEFAULT / PUBLIC / VICTIM / DISASTER
        // =========================================================

        @Bean
        @Order(10)
        public SecurityFilterChain
        defaultSecurityFilterChain(
                HttpSecurity http)
            throws Exception {

            http

                    .csrf(csrf -> csrf.disable())

                    .authorizeHttpRequests(auth -> auth

                            // =========================================
                            // PUBLIC HOME / COMMON LOGIN / REGISTER
                            // =========================================

                            .requestMatchers(
                                    "/",
                                    "/login",
                                    "/register",
                                    "/access-denied",
                                    "/css/**",
                                    "/js/**",
                                    "/images/**"
                            )
                            .permitAll()


                            // =========================================
                            // DONOR REGISTRATION
                            // =========================================

                            .requestMatchers(
                                    "/donors/register",
                                    "/donors/success"
                            )
                            .permitAll()


                            // =========================================
                            // VICTIM REGISTRATION
                            // =========================================

                            .requestMatchers(
                                    "/victims/register"
                            )
                            .permitAll()


                            // =========================================
                            // VOLUNTEER REGISTRATION
                            // =========================================

                            .requestMatchers(
                                    "/volunteer/register"
                            )
                            .permitAll()


                            // =========================================
                            // VICTIM
                            // =========================================

                            .requestMatchers(
                                    "/victims/**"
                            )
                            .hasAnyRole(
                                    "VICTIM",
                                    "PROJECT_MANAGER"
                            )


                            // =========================================
                            // NEEDS ASSESSMENT - VICTIM
                            // =========================================

                            .requestMatchers(
                                    "/needs-assessment/form",
                                    "/needs-assessment/submit",
                                    "/needs-assessment/status"
                            )
                            .hasRole(
                                    "VICTIM"
                            )


                            // =========================================
                            // GN REPORT
                            // =========================================

                            .requestMatchers(
                                    "/gn-reports/**"
                            )
                            .permitAll()


                            // =========================================
                            // DISASTER MANAGEMENT
                            // =========================================

                            .requestMatchers(
                                    "/disasters/**",
                                    "/affected-areas/**"
                            )
                            .hasRole(
                                    "PROJECT_MANAGER"
                            )


                            // =========================================
                            // EVERYTHING ELSE
                            // =========================================

                            .anyRequest()
                            .authenticated()
                    );

            return http.build();
        }
    }
