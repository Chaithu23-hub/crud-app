package com.softsuave.crud.service;

import com.softsuave.crud.dto.SignupApiRequestDTO;
import com.softsuave.crud.dto.ValidateOtpApiRequestDTO;
import com.softsuave.crud.entity.Users;
import com.softsuave.crud.exception.SignupException;
import com.softsuave.crud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;




    public void generateOtpforSignup(SignupApiRequestDTO signupApiRequestDTO ) {

        if (userRepository.findByUsername(signupApiRequestDTO.getUsername()).isPresent()) {
            throw new SignupException("Username already exists!");
        }
        Optional<Users> existingUser = userRepository.findByEmail(signupApiRequestDTO.getEmail());


        if (existingUser.isPresent()) {
            throw new SignupException("Email '" + signupApiRequestDTO.getEmail() + "' is already registered.");
        }
        Users user = existingUser.orElse(new Users());

        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        String otpString = new DecimalFormat("000000").format(otp);


        try {
            emailService.sendOtpEmail(signupApiRequestDTO.getEmail(), otpString);
        } catch (Exception e) {
            throw new SignupException("Failed to send OTP email. Please try again later.");
        }


        user.setEmail(signupApiRequestDTO.getEmail());
        user.setUsername(signupApiRequestDTO.getUsername());
        user.setOtp(otpString);
        user.setOtpExpirationTime(LocalDateTime.now().plusMinutes(10));
        user.setPassword(null);
        user.setRole("ROLE_USER");

        userRepository.save(user);
    }


    public String validateOtpAndCreateUser(ValidateOtpApiRequestDTO request) {

        Users user = userRepository.findByEmail(request.getEmail()/*,request.getOtp()*/)
                .orElseThrow(() -> new SignupException("No signup request was found for this email."));

//       if(user.getPassword()!=null){
//           throw new RuntimeException("This account is already active. Please log in");
//       }

       if(user.getOtpExpirationTime().isBefore(LocalDateTime.now())){
           throw new SignupException("OTP expried request new");
       }

        if (!user.getOtp().equals(request.getOtp())) {
            throw new SignupException("Invalid OTP!");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setOtpExpirationTime(LocalDateTime.now());
        user.setOtp(null);

        userRepository.save(user);

        return "User created successfully! You can now log in.";
    }

}
