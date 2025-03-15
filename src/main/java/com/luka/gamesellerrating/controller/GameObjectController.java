package com.luka.gamesellerrating.controller;

import com.luka.gamesellerrating.dto.GameObjectDTO;
import com.luka.gamesellerrating.dto.wrapper.ResponseWrapper;
import com.luka.gamesellerrating.service.GameObjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/api/v1/game-objects")
public class GameObjectController {

    private final GameObjectService gameObjectService;

    public GameObjectController(GameObjectService gameObjectService) {
        this.gameObjectService = gameObjectService;
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper> findAll() {
        List<GameObjectDTO> gameObjects = gameObjectService.findAll();
        return ok(ResponseWrapper.builder()
                .success(true)
                .code(OK.value())
                .message("Game objects retrieved successfully")
                .data(gameObjects)
                .build());
    }

    @GetMapping("/{sellerId}")
    public ResponseEntity<ResponseWrapper> findAllBySeller(@PathVariable("sellerId") Long id) {
        return ok(ResponseWrapper.builder()
                .success(true)
                .code(OK.value())
                .message("Game objects retrieved successfully")
                .data(gameObjectService.findAllBySellerId(id))
                .build());
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper> createGameObj(@RequestBody GameObjectDTO gameObject) {
        return status(CREATED).body(ResponseWrapper.builder()
                .success(true)
                .code(CREATED.value())
                .message("Game object created successfully")
                .data(gameObjectService.save(gameObject))
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper> updateGameObject(@PathVariable("id") Long id, @RequestBody GameObjectDTO gameObj) {
        return ok(ResponseWrapper.builder()
                .success(true)
                .code(OK.value())
                .message("Game object updated successfully")
                .data(gameObjectService.update(id, gameObj))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper> deleteGameObj(@PathVariable("id") Long id) {
        gameObjectService.delete(id);
        return noContent().build();
    }

}
