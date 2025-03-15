package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.AnonymousUserDTO;
import com.luka.gamesellerrating.entity.AnonymousUser;
import com.luka.gamesellerrating.mapper.AnonymousUserMapper;
import com.luka.gamesellerrating.repository.AnonymousUserRepository;
import com.luka.gamesellerrating.service.AnonymousUserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnonymousUserServiceImpl implements AnonymousUserService {

    private final AnonymousUserRepository anonymousUserRepository;
    private final AnonymousUserMapper anonymousUserMapper;

    public AnonymousUserServiceImpl(AnonymousUserRepository anonymousUserRepository, AnonymousUserMapper anonymousUserMapper) {
        this.anonymousUserRepository = anonymousUserRepository;
        this.anonymousUserMapper = anonymousUserMapper;
    }

    @Override
    public List<AnonymousUserDTO> findAll() {
        return anonymousUserMapper.toDtoList(anonymousUserRepository.findAll());
    }

    @Override
    public AnonymousUserDTO save(String identifier) {
        var foundAnonymousUser = anonymousUserRepository.findByIdentifier(identifier);
        if (foundAnonymousUser.isPresent()) {
            return anonymousUserMapper.toDto(foundAnonymousUser.get());
        }
        var newAnonymousUser = AnonymousUser.create();
        newAnonymousUser.setIdentifier(identifier);
        var savedAnonymousUser = anonymousUserRepository.save(newAnonymousUser);
        return anonymousUserMapper.toDto(savedAnonymousUser);
    }

    @Override
    public Optional<AnonymousUserDTO> findByIdentifier(String identifier) {
        var foundAnonymousUser = anonymousUserRepository.findByIdentifier(identifier);
        return foundAnonymousUser.map(anonymousUserMapper::toDto);
    }
}
