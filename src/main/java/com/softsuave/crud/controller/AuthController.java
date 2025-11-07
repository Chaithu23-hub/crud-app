package com.softsuave.crud.controller;

import com.softsuave.crud.entity.Users;
import com.softsuave.crud.repository.UserRepository;
import com.softsuave.crud.utility.JWTutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Memory Comment:
 * This controller manages authentication endpoints like '/auth/login'.
 *
 * It handles login requests, authenticates users via Spring Security,
 * and issues JWT tokens upon successful login.
 *
 * Note: '/auth/login' is publicly accessible as per SecurityConfig.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    // ✅ Step 1: Create logger for this class
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTutil jwTutil;

    @Autowired
    private UserRepository userRepository;

    /**
     * Endpoint: POST /auth/login
     * Accepts a username and password, authenticates them,
     * and returns a JWT token upon success.
     */
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Users users) {
        logger.info("Login attempt received for username: {}", users.getUsername());

        try {
            //  Step 1: Create authentication token from username & password
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(users.getUsername(), users.getPassword());
            logger.debug("Authentication token created for user: {}", users.getUsername());

            //  Step 2: Authenticate using Spring Security
            Authentication authentication = authenticationManager.authenticate(authRequest);
            logger.info("Authentication process started for user: {}", users.getUsername());

            //  Step 3: Check if authentication was successful
            if (authentication.isAuthenticated()) {
                logger.info("User '{}' successfully authenticated.", users.getUsername());

                //  Step 4: Retrieve user details from database to fetch role
                Users user = userRepository.findByUsername(users.getUsername())
                        .orElseThrow(() -> {
                            logger.error("User '{}' not found in database after authentication.", users.getUsername());
                            return new UsernameNotFoundException("User not found");
                        });

                //  Step 5: Generate JWT token using username and role
                String token = jwTutil.generateToken(user.getUsername(), user.getRole());
                logger.info("JWT token generated successfully for user: {}", users.getUsername());

                //  Step 6: Return token as JSON
                return Map.of("token", token);
            } else {
                logger.warn("Authentication failed for username: {}", users.getUsername());
                throw new RuntimeException("Invalid username or password");
            }

        } catch (UsernameNotFoundException e) {
            logger.error("UsernameNotFoundException for '{}': {}", users.getUsername(), e.getMessage());
            throw e;

        } catch (Exception e) {
            //  Step 7: Handle any unexpected errors
            logger.error("Unexpected error during login for '{}': {}", users.getUsername(), e.getMessage());
            throw new RuntimeException("Login failed due to internal error");
        }
    }


}
