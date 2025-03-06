package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.AnonymousUserDTO;
import com.luka.gamesellerrating.entity.AnonymousUser;
import com.luka.gamesellerrating.repository.AnonymousUserRepository;
import com.luka.gamesellerrating.service.AnonymousUserService;
import com.luka.gamesellerrating.util.MapperUtil;
import jakarta.servlet.http.HttpServletRequest;
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
    public AnonymousUserDTO save(HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        String ipAddress = getIpAddress(request);
        if (!anonymousUserRepository.existsByIpAddressAndSessionId(ipAddress, sessionId)){
            AnonymousUser anonymousUser = new AnonymousUser();
            anonymousUser.setSessionId(sessionId);
            anonymousUser.setIpAddress(ipAddress);
            return mapperUtil.convert(anonymousUser, new AnonymousUserDTO());
        }
        throw new RuntimeException("Anonymous user already exists.");
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
