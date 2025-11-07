package com.softsuave.crud.service;

import com.softsuave.crud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Memory Comment: This is the most critical class for Spring Security.
 * Its one and only job is to implement the 'UserDetailsService' interface.
 *
 * Spring Security calls this class automatically whenever it needs to
 * authenticate a user (e.g., during login or when validating a JWT).
 *
 * @Service tells Spring this is a bean, making it the one-and-only
 * UserDetailsService, which resolves the "Found 2 beans" conflict.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    //  Step 1: Create a logger for this class
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    // Memory: We inject the UserRepository because this service's job
    // is to connect to the database to find a user.
    @Autowired
    private UserRepository userRepository;

    /**
     * Memory Comment: This is the *only* method required by the UserDetailsService interface.
     * Spring Security calls this method and gives it the 'username' that a user
     * is trying to log in with.
     *
     * Our job is to find that user in our database and return a 'UserDetails' object.
     * Fortunately, our 'Users' entity *implements* UserDetails, so we can just return it directly.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //  INFO: Log that authentication is starting for this user.
        logger.info("Attempting to load user details for username: {}", username);

        //  DEBUG: You can log this at DEBUG level if you want less noise in production.
        logger.debug("Looking up user '{}' in the database", username);

        // 1. Try to find the user in the database.
        return userRepository.findByUsername(username)
                .map(user -> {
                    //  INFO: Found the user successfully.
                    logger.info("User '{}' found successfully. Proceeding with authentication.", username);
                    return user;
                })
                .orElseThrow(() -> {
                    //  ERROR: Log clearly when a user isn’t found.
                    logger.error("User '{}' not found in the database. Throwing UsernameNotFoundException.", username);
                    return new UsernameNotFoundException("User not found");
                });
    }

}
