package com.luka.gamesellerrating.repository;

import com.luka.gamesellerrating.entity.AnonymousUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnonymousUserRepository extends JpaRepository<AnonymousUser, Long> {

    boolean existsByIpAddressAndSessionId(String ip, String sessionId);

}
