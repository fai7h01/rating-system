package com.luka.gamesellerrating.controller;

import com.luka.gamesellerrating.dto.GameObjectDTO;
import com.luka.gamesellerrating.dto.wrapper.ResponseWrapper;
import com.luka.gamesellerrating.service.GameObjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game-objects")
public class GameObjectController {

    private final GameObjectService gameObjectService;

    public GameObjectController(GameObjectService gameObjectService) {
        this.gameObjectService = gameObjectService;
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper> createGameObj(@RequestBody GameObjectDTO gameObject) {
        GameObjectDTO saved = gameObjectService.save(gameObject);
        return ResponseEntity.ok(ResponseWrapper.builder().data(saved).build());
    }
}
