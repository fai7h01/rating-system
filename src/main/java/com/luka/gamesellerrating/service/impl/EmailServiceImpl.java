package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.Token;
import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.enums.TokenType;
import com.luka.gamesellerrating.service.EmailService;
import com.luka.gamesellerrating.service.TokenService;
import com.luka.gamesellerrating.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${app.base-url}")
    private String BASE_URL;
    @Value("${spring.mail.username}")
    private String fromEmail;


    private final JavaMailSender mailSender;
    private final TokenService tokenService;
    private final UserService userService;

    public EmailServiceImpl(JavaMailSender mailSender, TokenService tokenService, UserService userService) {
        this.mailSender = mailSender;
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @Override
    @Async("asyncTaskExecutor")
    public void sendUserVerificationEmail(String email) {
        SimpleMailMessage simpleMailMessage = createVerificationEmail(email);
        this.sendEmail(simpleMailMessage);
    }

    private void sendEmail(SimpleMailMessage simpleMailMessage) {
        mailSender.send(simpleMailMessage);
    }

    private SimpleMailMessage createVerificationEmail(String email) {
        String fullName = findUserFullName(email);
        Token token = tokenService.generateToken(email, TokenType.VERIFICATION);
        String subject = "Email Verification";
        String message = createVerificationEmailMessage(email, fullName, token.getToken(), token.getExpiryDate());
        return buildMailMessage(email, subject, message);
    }

    private SimpleMailMessage buildMailMessage(String to, String subject, String message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(this.fromEmail);
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setText(message);
        return mail;
    }

    private String findUserFullName(String email) {
        UserDTO dto = userService.findByEmail(email);
        return dto.getFirstName() + " " + dto.getLastName();
    }

    private String createVerificationEmailMessage(String email, String fullName, String token, LocalDate expiryDate) {

        String companyName = "Marketplace";
        String link = BASE_URL + "/api/v1/auth?email=" + email + "&token=" + token;
        String tokenExpiryDate = expiryDate.toString();

        return createVerificationEmailText(fullName, companyName, link, tokenExpiryDate);
    }

    private String createVerificationEmailText(String fullName, String companyName, String link, String tokenExpiryDate) {
        return String.format("""
                Dear %s,
                
                Thank you for registering with %s! We're excited to have you on board.
                
                To complete your registration and unlock all the features, please confirm your email address by clicking the link below:
                
                %s
                
                If you did not registered to our application, you can safely ignore this email.
                
                For security reasons, this confirmation link will expire at %s. If the link expired, you will need to request a new confirmation.
                """, fullName, companyName, link, tokenExpiryDate);
    }
}
