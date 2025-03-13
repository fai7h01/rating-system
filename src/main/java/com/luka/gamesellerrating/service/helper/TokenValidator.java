package com.luka.gamesellerrating.service.helper;

import com.luka.gamesellerrating.dto.TokenDTO;
import com.luka.gamesellerrating.exception.InvalidTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class TokenValidator {

    public void validateEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("Email is required");
        }
    }

    public void validateTokenRequest(String email, String tokenValue) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(tokenValue)) {
            throw new InvalidTokenException("Email and/or token are required");
        }
    }

    public void validateStoredToken(TokenDTO tokenDTO, String tokenValue) {
        if (tokenDTO == null) {
            throw new InvalidTokenException("Token not found");
        }
        if (!tokenDTO.isValid()) {
            throw new InvalidTokenException("Token has expired");
        }
        if (!tokenDTO.getToken().equals(tokenValue)) {
            throw new InvalidTokenException("Invalid token");
        }
    }
}
