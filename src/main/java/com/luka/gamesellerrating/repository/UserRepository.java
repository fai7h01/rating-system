package com.luka.gamesellerrating.repository;

import com.luka.gamesellerrating.entity.User;
import com.luka.gamesellerrating.enums.Role;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    @Query("SELECT u FROM User u WHERE u.role = :role AND LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    List<User> findAllByUsernameContainingAndRole(@Param("username") String username,
                                                  @Param("role") Role role);
    List<User> findAllByRole(Role role, Sort sort);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByUsernameIgnoreCase(String username);
}
