package com.luka.gamesellerrating.service;

import com.luka.gamesellerrating.dto.AnonymousUserDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface AnonymousUserService {

    AnonymousUserDTO save(HttpServletRequest request);

}
