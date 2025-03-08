package com.luka.gamesellerrating.service;

import com.luka.gamesellerrating.dto.AnonymousUserDTO;

import java.util.Optional;

public interface AnonymousUserService {

    AnonymousUserDTO save(String sessionId, String ipAddress);
    Optional<AnonymousUserDTO> findBySessionIdAndIpAddress(String sessionId, String ipAddress);
}
