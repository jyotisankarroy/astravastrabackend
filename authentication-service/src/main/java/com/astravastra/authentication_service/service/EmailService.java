package com.astravastra.authentication_service.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    
    public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	@Value("${spring.mail.username}")
    private String fromEmail;

    public boolean sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("AstraVastra OTP");
            
            // The email body
            String emailText = String.format(
                "Welcome to AstraVastra\n\n" +
                "Your verification OTP is: %s\n\n" +
                "This OTP is valid for 5 minutes. Do not share this code with anyone.\n\n" +
                "If you didn't request this OTP, please ignore this email.\n\n", 
                otp
            );
            
            message.setText(emailText);

            mailSender.send(message);
            log.info("OTP email sent successfully to {}", toEmail);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}", toEmail, e);
            throw new RuntimeException("Failed to send email. Please try again later.");
        }
    }
}
