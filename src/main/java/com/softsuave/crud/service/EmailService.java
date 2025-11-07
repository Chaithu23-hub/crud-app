package com.softsuave.crud.service;

import com.softsuave.crud.exception.SignupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("chepatichaithravardhanreddy@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Your One-Time Password (OTP) for Signup");
            message.setText("Hello,\n\nYour OTP for registration is: " + otp +
                    "\n\nThis OTP will expire in 10 minutes.");

            javaMailSender.send(message);
        } catch (Exception e) {
            throw new SignupException("Error sending email: " + e.getMessage());
        }
    }
}
