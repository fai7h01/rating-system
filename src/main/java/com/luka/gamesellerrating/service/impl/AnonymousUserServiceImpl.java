package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.AnonymousUserDTO;
import com.luka.gamesellerrating.entity.AnonymousUser;
import com.luka.gamesellerrating.repository.AnonymousUserRepository;
import com.luka.gamesellerrating.service.AnonymousUserService;
import com.luka.gamesellerrating.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnonymousUserServiceImpl implements AnonymousUserService {

    private final AnonymousUserRepository anonymousUserRepository;
    private final MapperUtil mapperUtil;

    public AnonymousUserServiceImpl(AnonymousUserRepository anonymousUserRepository, MapperUtil mapperUtil) {
        this.anonymousUserRepository = anonymousUserRepository;
        this.mapperUtil = mapperUtil;
    }


    @Override
    public List<AnonymousUserDTO> findAll() {
        return anonymousUserRepository.findAll().stream()
                .map(mapperUtil.convertTo(AnonymousUserDTO.class))
                .toList();
    }

    @Override
    public AnonymousUserDTO save(String identifier) {
        var foundAnonymousUser = anonymousUserRepository.findByIdentifier(identifier);
        if (foundAnonymousUser.isPresent()) {
            return mapperUtil.convert(foundAnonymousUser.get(), new AnonymousUserDTO());
        }
        var anonymousUser = new AnonymousUser();
        anonymousUser.setIdentifier(identifier);
        var savedAnonymousUser = anonymousUserRepository.save(anonymousUser);
        return mapperUtil.convert(savedAnonymousUser, new AnonymousUserDTO());
    }

    @Override
    public Optional<AnonymousUserDTO> findByIdentifier(String identifier) {
        var foundAnonymousUser = anonymousUserRepository.findByIdentifier(identifier);
        return foundAnonymousUser.map(anonymousUser -> mapperUtil.convert(anonymousUser, new AnonymousUserDTO()));
    }
}
