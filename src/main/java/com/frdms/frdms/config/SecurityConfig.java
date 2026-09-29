package com.frdms.frdms.config;

import com.frdms.frdms.common.service.ProjectManagerUserDetailsService;
import com.frdms.frdms.common.service.ReliefCoordinatorUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final ProjectManagerUserDetailsService projectManagerUserDetailsService;

    private final ReliefCoordinatorUserDetailsService
            reliefCoordinatorUserDetailsService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SecurityConfig(
            ProjectManagerUserDetailsService projectManagerUserDetailsService,
            ReliefCoordinatorUserDetailsService reliefCoordinatorUserDetailsService) {

        this.projectManagerUserDetailsService =
                projectManagerUserDetailsService;

        this.reliefCoordinatorUserDetailsService =
                reliefCoordinatorUserDetailsService;
    }


    // =========================================================
    // PASSWORD ENCODER
    // =========================================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    // =========================================================
    // PROJECT MANAGER AUTHENTICATION
    // =========================================================

    @Bean
    public DaoAuthenticationProvider projectManagerAuthenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        projectManagerUserDetailsService
                );

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }


    // =========================================================
    // RELIEF COORDINATOR AUTHENTICATION
    // =========================================================

    @Bean
    public DaoAuthenticationProvider reliefCoordinatorAuthenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        reliefCoordinatorUserDetailsService
                );

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }


    // =========================================================
    // PROJECT MANAGER SECURITY
    // =========================================================

    @Bean
    @Order(1)
    public SecurityFilterChain projectManagerSecurityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .securityMatcher("/project-manager/**")

                .authenticationProvider(
                        projectManagerAuthenticationProvider()
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/project-manager/login"
                        ).permitAll()

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
                                "/project-manager/login?logout=true"
                        )

                        .permitAll()
                );

        return http.build();
    }


    // =========================================================
    // RELIEF COORDINATOR SECURITY
    // =========================================================

    @Bean
    @Order(2)
    public SecurityFilterChain reliefCoordinatorSecurityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .securityMatcher("/relief-coordinator/**")

                .authenticationProvider(
                        reliefCoordinatorAuthenticationProvider()
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/relief-coordinator/login"
                        ).permitAll()

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
    // DEFAULT SECURITY
    // =========================================================

    @Bean
    @Order(3)
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http) throws Exception {

        http

                .authorizeHttpRequests(auth -> auth


                        // -------------------------------------------------
                        // STATIC FILES
                        // -------------------------------------------------

                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()


                        // -------------------------------------------------
                        // VICTIM REGISTRATION
                        // -------------------------------------------------

                        .requestMatchers(
                                "/victims/register"
                        ).permitAll()


                        // -------------------------------------------------
                        // VICTIM LOGIN
                        // -------------------------------------------------
                        //
                        // IMPORTANT:
                        // VictimLoginController uses:
                        //
                        // /victims/login
                        //
                        // Therefore this URL must be public.
                        // -------------------------------------------------

                        .requestMatchers(
                                "/victim-login",
                                "/victim-login/**",
                                "/victims/login",
                                "/victims/login/**",
                                "/victims/logout"
                        ).permitAll()


                        // -------------------------------------------------
                        // VICTIM DASHBOARD / SESSION FUNCTIONS
                        // -------------------------------------------------

                        .requestMatchers(
                                "/victim/**"
                        ).permitAll()


                        // -------------------------------------------------
                        // NEEDS ASSESSMENT - VICTIM SIDE
                        // -------------------------------------------------

                        .requestMatchers(
                                "/needs-assessment/form",
                                "/needs-assessment/submit",
                                "/needs-assessment/status"
                        ).permitAll()


                        // -------------------------------------------------
                        // GN REPORT
                        // -------------------------------------------------

                        .requestMatchers(
                                "/gn-reports/**"
                        ).permitAll()


                        // -------------------------------------------------
                        // DISASTER MANAGEMENT
                        // -------------------------------------------------

                        .requestMatchers(
                                "/disasters/**",
                                "/affected-areas/**"
                        ).hasRole("PROJECT_MANAGER")


                        // -------------------------------------------------
                        // VICTIM MANAGEMENT - PROJECT MANAGER
                        // -------------------------------------------------

                        .requestMatchers(
                                "/victims/**"
                        ).hasRole("PROJECT_MANAGER")


                        // -------------------------------------------------
                        // EVERYTHING ELSE
                        // -------------------------------------------------

                        .anyRequest()
                        .authenticated()
                );


        return http.build();
    }
}