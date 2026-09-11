package com.frdms.frdms.config;

import com.frdms.frdms.common.service.ProjectManagerUserDetailsService;
import com.frdms.frdms.volunteer_management.service.VolunteerUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final VolunteerUserDetailsService volunteerUserDetailsService;
    private final ProjectManagerUserDetailsService projectManagerUserDetailsService;

    public SecurityConfig(
            VolunteerUserDetailsService volunteerUserDetailsService,
            ProjectManagerUserDetailsService projectManagerUserDetailsService
    ) {
        this.volunteerUserDetailsService = volunteerUserDetailsService;
        this.projectManagerUserDetailsService = projectManagerUserDetailsService;
    }

    // =========================================================
    // PASSWORD ENCODER
    // =========================================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // =========================================================
    // VOLUNTEER AUTHENTICATION PROVIDER
    // =========================================================

    @Bean
    public DaoAuthenticationProvider volunteerAuthenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(volunteerUserDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }


    // =========================================================
    // PROJECT MANAGER AUTHENTICATION PROVIDER
    // =========================================================

    @Bean
    public DaoAuthenticationProvider projectManagerAuthenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(projectManagerUserDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }


    // =========================================================
    // PROJECT MANAGER SECURITY
    // =========================================================

    @Bean
    @Order(1)
    public SecurityFilterChain projectManagerSecurityFilterChain(
            HttpSecurity http
    ) throws Exception {

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

                        .loginPage("/project-manager/login")

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
    // VOLUNTEER SECURITY
    // =========================================================

    @Bean
    @Order(2)
    public SecurityFilterChain volunteerSecurityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .securityMatcher("/volunteer/**")

                .authenticationProvider(
                        volunteerAuthenticationProvider()
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/volunteer/register",
                                "/volunteer/login"
                        ).permitAll()

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
    // OTHER FRDMS URLS
    // =========================================================

    @Bean
    @Order(3)
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        .anyRequest()
                        .authenticated()
                )

                .formLogin(Customizer.withDefaults());

        return http.build();
    }
}