package com.luka.gamesellerrating.repository;

import com.luka.gamesellerrating.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
