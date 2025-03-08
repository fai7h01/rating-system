package com.luka.gamesellerrating.repository;

import com.luka.gamesellerrating.entity.AnonymousUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnonymousUserRepository extends JpaRepository<AnonymousUser, Long> {

    Optional<AnonymousUser> findByIdentifier(String identifier);
}
