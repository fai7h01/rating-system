package com.luka.gamesellerrating.mapper;

import com.luka.gamesellerrating.dto.GameObjectDTO;
import com.luka.gamesellerrating.entity.GameObject;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GameObjectMapper {

    GameObjectDTO toDto(GameObject entity);
    GameObject toEntity(GameObjectDTO dto);
    List<GameObjectDTO> toDtoList(List<GameObject> list);
}
