package com.luka.gamesellerrating.service;

import com.luka.gamesellerrating.dto.AnonymousUserDTO;

public interface AnonymousUserService {

    AnonymousUserDTO save(String sessionId, String ipAddress);

}
