package com.luka.gamesellerrating.entity;

import com.luka.gamesellerrating.service.KeycloakService;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BaseEntityListener extends AuditingEntityListener {

    private final KeycloakService keycloakService;

    public BaseEntityListener(@Lazy KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @PrePersist
    public void onPrePersist(BaseEntity entity) {
        Long userId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();
        entity.setInsertDateTime(now);
        entity.setLastUpdateDateTime(now);
        entity.setInsertUserId(userId);
        entity.setLastUpdateUserId(userId);
    }

    @PreUpdate
    public void onPreUpdate(BaseEntity entity) {
        Long userId = getCurrentUserId();
        entity.setLastUpdateDateTime(LocalDateTime.now());
        entity.setLastUpdateUserId(userId);
    }

    private Long getCurrentUserId() {
        try {
            var user = keycloakService.getLoggedInUser();
            return (user != null && user.getId() != null) ? user.getId() : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }
}
