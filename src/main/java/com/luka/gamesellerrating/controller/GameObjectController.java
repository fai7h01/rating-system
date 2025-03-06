package com.luka.gamesellerrating.controller;

import com.luka.gamesellerrating.dto.GameObjectDTO;
import com.luka.gamesellerrating.dto.wrapper.ResponseWrapper;
import com.luka.gamesellerrating.service.GameObjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/game-objects")
public class GameObjectController {

    private final GameObjectService gameObjectService;

    public GameObjectController(GameObjectService gameObjectService) {
        this.gameObjectService = gameObjectService;
    }


    @GetMapping
    public ResponseEntity<ResponseWrapper> listAll() {
        List<GameObjectDTO> gameObjects = gameObjectService.findAll();
        return ok(ResponseWrapper.builder().data(gameObjects).build());
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper> createGameObj(@RequestBody GameObjectDTO gameObject) {
        GameObjectDTO saved = gameObjectService.save(gameObject);
        return ok(ResponseWrapper.builder().data(saved).build());
    }


}
