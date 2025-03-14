package com.luka.gamesellerrating.repository;

import com.luka.gamesellerrating.entity.GameObject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameObjectRepository extends JpaRepository<GameObject, Long> {

    boolean existsBySellerIdAndTitle(Long id, String title);
    List<GameObject> findAllBySellerId(Long sellerId);
}
