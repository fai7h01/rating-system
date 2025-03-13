package com.luka.gamesellerrating.service.helper;

import com.luka.gamesellerrating.dto.TokenDTO;
import com.luka.gamesellerrating.enums.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenCacheManager {

    private final RedisTemplate<String, TokenDTO> redisTemplate;

    public void storeToken(TokenDTO tokenDTO, TokenType tokenType) {
        String key = buildKey(tokenDTO.getEmail(), tokenType);
        redisTemplate.delete(key);
        redisTemplate.opsForValue().set(key, tokenDTO, tokenType.getDuration());
    }

    public TokenDTO getToken(String email, TokenType tokenType) {
        return redisTemplate.opsForValue().get(buildKey(email, tokenType));
    }

    public void removeToken(String email, TokenType tokenType) {
        redisTemplate.delete(buildKey(email, tokenType));
    }

    private String buildKey(String email, TokenType type) {
        return type.getRedisPrefix() + email;
    }
}
