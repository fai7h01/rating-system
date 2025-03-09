package com.luka.gamesellerrating.repository;

import com.luka.gamesellerrating.entity.User;
import com.luka.gamesellerrating.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameIgnoreCase(String username);
    Optional<User> findByUsernameIgnoreCaseAndRole(String username, Role role);
    List<User> findAllByRole(Role role);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByUsernameIgnoreCase(String username);
}
