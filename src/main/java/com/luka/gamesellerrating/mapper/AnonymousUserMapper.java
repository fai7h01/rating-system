package com.luka.gamesellerrating.mapper;

import com.luka.gamesellerrating.dto.AnonymousUserDTO;
import com.luka.gamesellerrating.entity.AnonymousUser;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnonymousUserMapper {

    AnonymousUserDTO toDto(AnonymousUser entity);
    AnonymousUser toEntity(AnonymousUserDTO dto);
    List<AnonymousUserDTO> toDtoList(List<AnonymousUser> list);
}
