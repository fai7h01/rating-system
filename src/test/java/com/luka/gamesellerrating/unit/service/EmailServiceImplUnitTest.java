package com.luka.gamesellerrating.unit.service;

import com.luka.gamesellerrating.dto.TokenDTO;
import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.enums.TokenType;
import com.luka.gamesellerrating.service.TokenService;
import com.luka.gamesellerrating.service.UserService;
import com.luka.gamesellerrating.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplUnitTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserService userService;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        when(userService.findByEmail(any())).thenReturn(userDTO);

        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setToken("dummyToken");
        tokenDTO.setExpiryDate(LocalDate.now().plusDays(1));
        when(tokenService.generateToken(any(), eq(TokenType.VERIFICATION_TOKEN))).thenReturn(tokenDTO);
    }

    @Test
    void testSendUserVerificationEmail() {
        String testEmail = "john.doe@example.com";
        emailService.sendUserVerificationEmail("john.doe@example.com");

        ArgumentCaptor<SimpleMailMessage> mailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, timeout(1000)).send(mailCaptor.capture());

        SimpleMailMessage value = mailCaptor.getValue();
        assertEquals(testEmail, Objects.requireNonNull(value.getTo())[0]);
    }
}