package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.TokenDTO;
import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.enums.EmailType;
import com.luka.gamesellerrating.service.EmailService;
import com.luka.gamesellerrating.service.TokenService;
import com.luka.gamesellerrating.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.luka.gamesellerrating.enums.EmailType.RESET_PASSWORD_EMAIL;
import static com.luka.gamesellerrating.enums.EmailType.VERIFICATION_EMAIL;
import static com.luka.gamesellerrating.enums.TokenType.RESET_PASSWORD_TOKEN;
import static com.luka.gamesellerrating.enums.TokenType.VERIFICATION_TOKEN;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${server.port}")
    private String port;
    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender mailSender;
    private final TokenService tokenService;
    private final UserService userService;

    public EmailServiceImpl(JavaMailSender mailSender, TokenService tokenService, @Lazy UserService userService) {
        this.mailSender = mailSender;
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @Override
    @Async("asyncTaskExecutor")
    public void sendUserVerificationEmail(String email) {
        SimpleMailMessage simpleMailMessage = createEmail(email, VERIFICATION_EMAIL);
        this.sendEmail(simpleMailMessage);
    }

    @Override
    @Async("asyncTaskExecutor")
    public void sendPasswordResetEmail(String email) {
        SimpleMailMessage simpleMailMessage = createEmail(email, RESET_PASSWORD_EMAIL);
        this.sendEmail(simpleMailMessage);
    }

    private SimpleMailMessage createEmail(String email, EmailType emailType) {
        TokenDTO token = createToken(email, emailType);
        String subject = emailType.getValue();
        String message = createMessage(email, token, emailType);
        return buildMailMessage(email, subject, message);
    }

    private TokenDTO createToken(String email, EmailType emailType) {
        return emailType.equals(VERIFICATION_EMAIL)
                ? tokenService.generateToken(email, VERIFICATION_TOKEN)
                : tokenService.generateToken(email, RESET_PASSWORD_TOKEN);
    }

    private String createMessage(String email, TokenDTO token, EmailType emailType) {
        return emailType.equals(VERIFICATION_EMAIL)
                ? createVerificationEmailMessage(email, token)
                : createResetPasswordEmailMessage(email, token);
    }

    private String createVerificationEmailMessage(String email, TokenDTO token) {
        String userFullName = findUserFullName(email);
        String link = "http://localhost: " + port + "/api/v1/auth/confirmation?email=" + email + "&token=" + token.getToken();
        String tokenExpiryDate = token.getExpiryDate().toString();
        return createVerificationEmailText(userFullName, link, tokenExpiryDate);
    }

    private String createResetPasswordEmailMessage(String email, TokenDTO token) {
        String userFullName = findUserFullName(email);
        String link = "http://localhost: " + port + "/api/v1/auth/new-password?email=" + email + "&token=" + token.getToken();
        String tokenExpiryDate = token.getExpiryDate().toString();
        return createResetPasswordEmailText(userFullName, link, tokenExpiryDate);
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

    private void sendEmail(SimpleMailMessage simpleMailMessage) {
        mailSender.send(simpleMailMessage);
    }

    private String createVerificationEmailText(String fullName, String link, String tokenExpiryDate) {
        return String.format("""
                Dear %s,
                
                Thank you for registering with Game Marketplace! We're excited to have you on board.
                
                To complete your registration and unlock all the features, please confirm your email address by clicking the link below:
                
                %s
                
                If you did not registered to our application, you can safely ignore this email.
                
                For security reasons, this confirmation link will expire at %s. If the link expired, you will need to request a new confirmation.
                """, fullName, link, tokenExpiryDate);
    }

    private String createResetPasswordEmailText(String fullName, String link, String tokenExpiryDate) {
        return String.format("""
            Dear %s,
            
            We received a request to reset your password for your Game Marketplace account.
            
            To reset your password, please click the link below:
            
            %s
            
            If you did not request a password reset, you can safely ignore this email.
            
            This link will expire at %s. If the link expires, please submit a new password reset request.
            """, fullName, link, tokenExpiryDate);
    }
}
