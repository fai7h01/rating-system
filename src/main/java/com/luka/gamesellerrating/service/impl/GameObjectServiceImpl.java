package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.GameObjectDTO;
import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.entity.GameObject;
import com.luka.gamesellerrating.exception.GameObjectAlreadyExistsException;
import com.luka.gamesellerrating.exception.GameObjectNotFoundException;
import com.luka.gamesellerrating.repository.GameObjectRepository;
import com.luka.gamesellerrating.service.AuthenticationService;
import com.luka.gamesellerrating.service.GameObjectService;
import com.luka.gamesellerrating.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameObjectServiceImpl implements GameObjectService {

    private final GameObjectRepository gameObjectRepository;
    private final AuthenticationService authenticationService;
    private final MapperUtil mapperUtil;

    public GameObjectServiceImpl(GameObjectRepository gameObjectRepository, AuthenticationService authenticationService, MapperUtil mapperUtil) {
        this.gameObjectRepository = gameObjectRepository;
        this.authenticationService = authenticationService;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public GameObjectDTO save(GameObjectDTO gameObject) {
        if (gameObjectRepository.existsByTitle(gameObject.getTitle())) {
            throw new GameObjectAlreadyExistsException("Game obj already exists with that title.");
        }
        gameObject.setSeller(getLoggedInUser());
        GameObject entity = mapperUtil.convert(gameObject, new GameObject());
        GameObject saved = gameObjectRepository.save(entity);
        return mapperUtil.convert(saved, new GameObjectDTO());
    }

    @Override
    public GameObjectDTO update(Long id, GameObjectDTO gameObject) {
        if (!gameObjectRepository.existsById(id)) {
            throw new GameObjectNotFoundException("Game obj not found.");
        }
        gameObject.setId(id);
        GameObject savedObj = gameObjectRepository.save(mapperUtil.convert(gameObject, new GameObject()));
        return mapperUtil.convert(savedObj, new GameObjectDTO());
    }

    @Override
    public List<GameObjectDTO> findAll() {
        return gameObjectRepository.findAll().stream()
                .map(mapperUtil.convertTo(GameObjectDTO.class))
                .toList();
    }

    @Override
    public void delete(Long id) {
        GameObject foundGameObj = gameObjectRepository.findById(id)
                .orElseThrow(() -> new GameObjectNotFoundException("Game obj not found."));
        foundGameObj.setIsDeleted(true);
        gameObjectRepository.save(foundGameObj);
    }

    private UserDTO getLoggedInUser() {
        return authenticationService.getLoggedInUser();
    }
}
