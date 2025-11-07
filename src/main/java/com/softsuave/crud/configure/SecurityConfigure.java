package com.softsuave.crud.configure;

import com.softsuave.crud.filters.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Memory Comment:
 * This is the main Spring Security configuration class.
 *
 * It defines how HTTP requests are secured, which endpoints are public,
 * and integrates JWT-based authentication instead of sessions.
 *
 * @Configuration → Marks this as a config class.
 * @EnableWebSecurity → Turns on web security.
 * @EnableMethodSecurity → Enables @PreAuthorize and @Secured annotations.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfigure {

    //  Step 1: Add logger
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfigure.class);

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    /**
     * Defines the main HTTP security configuration.
     * Controls which URLs are open, which require authentication,
     * and sets up the filter chain.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        logger.info("Initializing SecurityFilterChain configuration...");

        httpSecurity
                //  Disable CSRF for JWT-based REST APIs
                .csrf(csrf -> csrf.disable())

                //  Define authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("api/validateotp","api/signup", "/authenticate", "/auth/login").permitAll()
                        .anyRequest().authenticated()
                )

                //  Make sessions stateless since JWT is used
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //  Add custom JWT filter before UsernamePasswordAuthenticationFilter
        httpSecurity.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        logger.info("SecurityFilterChain setup complete. JWT authentication is active and stateless mode is enabled.");
        return httpSecurity.build();
    }

    /**
     * Defines the password encoder used for hashing and verifying passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Initializing BCryptPasswordEncoder as PasswordEncoder.");
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines the AuthenticationManager, the heart of Spring Security authentication.
     * It uses the CustomUserDetailsService + BCrypt encoder to verify credentials.
     */
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        logger.info("Setting up AuthenticationManager with DaoAuthenticationProvider...");

        //  DaoAuthenticationProvider handles DB-based authentication
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);

        AuthenticationManager manager = new ProviderManager(daoAuthenticationProvider);

        logger.info("AuthenticationManager created successfully using CustomUserDetailsService and BCryptPasswordEncoder.");
        return manager;
    }
}
