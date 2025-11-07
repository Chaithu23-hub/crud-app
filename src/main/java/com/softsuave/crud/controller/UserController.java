package com.softsuave.crud.controller;

import com.softsuave.crud.dto.SignupApiRequestDTO;
import com.softsuave.crud.dto.UserRegistrationRequestDTO;
import com.softsuave.crud.dto.ValidateOtpApiRequestDTO;
import com.softsuave.crud.entity.Users;
import com.softsuave.crud.repository.UserRepository;
import com.softsuave.crud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Memory Comment:
 * This controller handles all user-related public endpoints, mainly registration.
 *
 * URL pattern:
 * Base path: /api
 * Example endpoint: /api/register
 *
 * Note:
 * Your SecurityConfig allows this endpoint ("/api/register") to be accessed publicly,
 * so anyone can register without authentication.
 */
@RestController
@RequestMapping("/api")
public class UserController {

    // ✅ Step 1: Create a logger for this class
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Memory Comment:
     * Endpoint: POST /api/register
     * Purpose: Register a new user into the database.
     *
     * @RequestBody - Converts the JSON body into a DTO (UserRegistrationRequestDTO).
     */
    @PostMapping("/register")
    public String registerUser(@RequestBody UserRegistrationRequestDTO userRegistrationRequestDTO) {
        logger.info("Received registration request for username: {}", userRegistrationRequestDTO.getUsername());

        try {
            //  Step 1: Create a new Users entity
            Users newUser = new Users();
            newUser.setUsername(userRegistrationRequestDTO.getUsername());

            //  Step 2: Securely encode the password before saving
            newUser.setPassword(passwordEncoder.encode(userRegistrationRequestDTO.getPassword()));
            logger.debug("Password encoded successfully for username: {}", userRegistrationRequestDTO.getUsername());

            //  Step 3: Assign the default role
            newUser.setRole("ROLE_USER");
            logger.debug("Assigned default role 'ROLE_USER' to user: {}", userRegistrationRequestDTO.getUsername());

            //  Step 4: Save user to the database
            userRepository.save(newUser);
            logger.info("User '{}' registered successfully!", newUser.getUsername());

            return "User registered successfully!";
        } catch (Exception e) {
            //  Step 5: Log any errors during registration
            logger.error("Error occurred while registering user '{}': {}", userRegistrationRequestDTO.getUsername(), e.getMessage());
            return "Registration failed! Please try again.";
        }
    }
    @PostMapping("/signup")
    public String signup(@RequestBody SignupApiRequestDTO request) {
           userService.generateOtpforSignup(request);
           return "An email containing OTP has been sent to the user";

    }

    @PostMapping("/validateotp")
    public String validateOtpAndSaveUser(@RequestBody ValidateOtpApiRequestDTO request){
        userService.validateOtpAndCreateUser(request);
        return "OTP is validated and user created";
    }

}
