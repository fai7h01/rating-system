package com.luka.gamesellerrating.repository;

import com.luka.gamesellerrating.entity.GameObject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameObjectRepository extends JpaRepository<GameObject, Long> {
}
