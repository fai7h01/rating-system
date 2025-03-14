package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.GameObjectDTO;
import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.entity.GameObject;
import com.luka.gamesellerrating.exception.GameObjectAccessDeniedException;
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
    private final AuthenticationService authService;
    private final MapperUtil mapperUtil;

    public GameObjectServiceImpl(GameObjectRepository gameObjectRepository, AuthenticationService authService, MapperUtil mapperUtil) {
        this.gameObjectRepository = gameObjectRepository;
        this.authService = authService;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public GameObjectDTO save(GameObjectDTO gameObject) {
        var seller = getLoggedInUser();
        if (gameObjectRepository.existsBySellerIdAndTitle(seller.getId(), gameObject.getTitle())) {
            throw new GameObjectAlreadyExistsException("Game obj already exists with that title.");
        }
        gameObject.setSeller(seller);
        var entity = mapperUtil.convert(gameObject, new GameObject());
        var savedGameObj = gameObjectRepository.save(entity);
        return mapperUtil.convert(savedGameObj, new GameObjectDTO());
    }

    @Override
    public GameObjectDTO update(Long id, GameObjectDTO gameObject) {
        var foundGameObj = findGameObjEntityById(id);
        validateUserAccess(foundGameObj);
        updateGameObjFields(foundGameObj, gameObject);
        var savedGameObj = gameObjectRepository.save(foundGameObj);
        return mapperUtil.convert(savedGameObj, new GameObjectDTO());
    }

    @Override
    public List<GameObjectDTO> findAll() {
        return gameObjectRepository.findAll().stream()
                .map(mapperUtil.convertTo(GameObjectDTO.class))
                .toList();
    }

    @Override
    public List<GameObjectDTO> findAllBySellerId(Long id) {
        return gameObjectRepository.findAllBySellerId(id).stream()
                .map(mapperUtil.convertTo(GameObjectDTO.class))
                .toList();
    }

    @Override
    public void delete(Long id) {
        var foundGameObj = findGameObjEntityById(id);
        validateUserAccess(foundGameObj);
        foundGameObj.setIsDeleted(true);
        gameObjectRepository.save(foundGameObj);
    }

    private void validateUserAccess(GameObject gameObject) {
        if (authService.isUserAnonymous()) {
            throw new GameObjectAccessDeniedException("Only author can manage their own item.");
        }
        var currentUserId = getLoggedInUser().getId();
        if (!gameObject.getSeller().getId().equals(currentUserId)) {
            throw new GameObjectAccessDeniedException("Only author can manage their own item.");
        }
    }

    private UserDTO getLoggedInUser() {
        return authService.getLoggedInUser();
    }

    private GameObject findGameObjEntityById(Long id) {
        return gameObjectRepository.findById(id)
                .orElseThrow(() -> new GameObjectNotFoundException("Game obj not found."));
    }

    private void updateGameObjFields(GameObject entity, GameObjectDTO dto) {
        entity.setText(dto.getText());
        entity.setTitle(dto.getTitle());
    }
}
