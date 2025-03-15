package com.luka.gamesellerrating.mapper;

import com.luka.gamesellerrating.dto.RatingDTO;
import com.luka.gamesellerrating.entity.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    @Mapping(source = "value", target = "stars")
    RatingDTO toDto(Rating entity);

    @Mapping(source = "stars", target = "value")
    Rating toEntity(RatingDTO dto);

    List<RatingDTO> toDtoList(List<Rating> list);
}
