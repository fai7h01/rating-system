package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.GameObjectDTO;
import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.entity.GameObject;
import com.luka.gamesellerrating.exception.GameObjectAccessDeniedException;
import com.luka.gamesellerrating.exception.GameObjectAlreadyExistsException;
import com.luka.gamesellerrating.exception.GameObjectNotFoundException;
import com.luka.gamesellerrating.mapper.GameObjectMapper;
import com.luka.gamesellerrating.repository.GameObjectRepository;
import com.luka.gamesellerrating.service.AuthenticationService;
import com.luka.gamesellerrating.service.GameObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameObjectServiceImpl implements GameObjectService {

    private final GameObjectRepository gameObjectRepository;
    private final AuthenticationService authService;
    private final GameObjectMapper gameObjMapper;

    @Override
    public GameObjectDTO save(GameObjectDTO gameObject) {
        validateNewGameObject(gameObject.getTitle());
        var currentUser = getLoggedInUser();
        gameObject.setSeller(currentUser);
        var entity =gameObjMapper.toEntity(gameObject);
        return gameObjMapper.toDto(gameObjectRepository.save(entity));
    }

    @Override
    public GameObjectDTO update(Long id, GameObjectDTO gameObject) {
        var foundGameObj = findGameObjEntityById(id);
        validateUserAccess(foundGameObj.getSeller().getId());
        updateGameObjFields(foundGameObj, gameObject);
        return gameObjMapper.toDto(gameObjectRepository.save(foundGameObj));
    }

    @Override
    public List<GameObjectDTO> findAll() {
        return gameObjMapper.toDtoList(gameObjectRepository.findAll());
    }

    @Override
    public List<GameObjectDTO> findAllBySellerId(Long id) {
        return gameObjMapper.toDtoList(gameObjectRepository.findAllBySellerId(id));
    }

    @Override
    public void delete(Long id) {
        var foundGameObj = findGameObjEntityById(id);
        validateUserAccess(foundGameObj.getSeller().getId());
        foundGameObj.setIsDeleted(true);
        gameObjectRepository.save(foundGameObj);
    }

    private GameObject findGameObjEntityById(Long id) {
        return gameObjectRepository.findById(id)
                .orElseThrow(() -> new GameObjectNotFoundException("Game obj not found."));
    }

    private void updateGameObjFields(GameObject entity, GameObjectDTO dto) {
        entity.setText(dto.getText());
        entity.setTitle(dto.getTitle());
    }

    private void validateNewGameObject(String objTitle) {
        var currentUser = getLoggedInUser();
        if (gameObjectRepository.existsBySellerIdAndTitle(currentUser.getId(), objTitle)) {
            throw new GameObjectAlreadyExistsException("Game obj already exists with that title.");
        }
    }

    private void validateUserAccess(Long sellerId) {
        var currentUser = getLoggedInUser();
        if (!currentUser.getId().equals(sellerId)) {
            throw new GameObjectAccessDeniedException("Only author can manage their own item.");
        }
    }

    private UserDTO getLoggedInUser() {
        return authService.getLoggedInUser();
    }
}
