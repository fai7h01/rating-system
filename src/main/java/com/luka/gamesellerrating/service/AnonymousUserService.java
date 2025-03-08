package com.luka.gamesellerrating.service;

import com.luka.gamesellerrating.dto.AnonymousUserDTO;

import java.util.Optional;

public interface AnonymousUserService {

    AnonymousUserDTO save(String identifier);
    Optional<AnonymousUserDTO> findByIdentifier(String identifier);
}
