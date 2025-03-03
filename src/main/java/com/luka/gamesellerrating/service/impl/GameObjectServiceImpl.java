package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.GameObjectDTO;
import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.entity.GameObject;
import com.luka.gamesellerrating.exception.GameObjectAlreadyExistsException;
import com.luka.gamesellerrating.exception.GameObjectNotFoundException;
import com.luka.gamesellerrating.repository.GameObjectRepository;
import com.luka.gamesellerrating.service.GameObjectService;
import com.luka.gamesellerrating.service.KeycloakService;
import com.luka.gamesellerrating.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameObjectServiceImpl implements GameObjectService {

    private final GameObjectRepository gameObjectRepository;
    private final KeycloakService keycloakService;
    private final MapperUtil mapperUtil;

    public GameObjectServiceImpl(GameObjectRepository gameObjectRepository, KeycloakService keycloakService, MapperUtil mapperUtil) {
        this.gameObjectRepository = gameObjectRepository;
        this.keycloakService = keycloakService;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public GameObjectDTO save(GameObjectDTO gameObject) {
        if (gameObjectRepository.existsByTitle(gameObject.getTitle())) {
            throw new GameObjectAlreadyExistsException("Game obj already exists with that title.");
        }
        UserDTO loggedInUser = keycloakService.getLoggedInUser();
        gameObject.setUser(loggedInUser);
        GameObject entity = mapperUtil.convert(gameObject, new GameObject());
        GameObject saved = gameObjectRepository.save(entity);
        return mapperUtil.convert(saved, new GameObjectDTO());
    }

    @Override
    public GameObjectDTO update(Long id, GameObjectDTO gameObject) {


        return null;
    }

    @Override
    public List<GameObjectDTO> findAll() {
        return List.of();
    }

    @Override
    public void delete(Long id) {

    }
}
