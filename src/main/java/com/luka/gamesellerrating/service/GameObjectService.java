package com.luka.gamesellerrating.service;

import com.luka.gamesellerrating.dto.GameObjectDTO;

import java.util.List;

public interface GameObjectService {

    GameObjectDTO save(GameObjectDTO gameObject);
    GameObjectDTO update(Long id, GameObjectDTO gameObject);
    List<GameObjectDTO> findAll();
    void delete(Long id);
}
