package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.AnonymousUserDTO;
import com.luka.gamesellerrating.entity.AnonymousUser;
import com.luka.gamesellerrating.exception.AnonymousUserAlreadyExistsException;
import com.luka.gamesellerrating.repository.AnonymousUserRepository;
import com.luka.gamesellerrating.service.AnonymousUserService;
import com.luka.gamesellerrating.util.MapperUtil;
import org.springframework.stereotype.Service;

@Service
public class AnonymousUserServiceImpl implements AnonymousUserService {

    private final AnonymousUserRepository anonymousUserRepository;
    private final MapperUtil mapperUtil;

    public AnonymousUserServiceImpl(AnonymousUserRepository anonymousUserRepository, MapperUtil mapperUtil) {
        this.anonymousUserRepository = anonymousUserRepository;
        this.mapperUtil = mapperUtil;
    }


    @Override
    public AnonymousUserDTO save(String sessionId, String ipAddress) {
        if (!anonymousUserRepository.existsByIpAddressAndSessionId(ipAddress, sessionId)){
            var anonymousUser = new AnonymousUser();
            anonymousUser.setSessionId(sessionId);
            anonymousUser.setIpAddress(ipAddress);
            var savedAnonymousUser = anonymousUserRepository.save(anonymousUser);
            return mapperUtil.convert(savedAnonymousUser, new AnonymousUserDTO());
        }
        throw new AnonymousUserAlreadyExistsException("Anonymous user already exists.");
    }

}
