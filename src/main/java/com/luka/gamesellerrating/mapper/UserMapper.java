package com.luka.gamesellerrating.mapper;


import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toDto(User entity);

    User toEntity(UserDTO dto);

    List<UserDTO> toDtoList(List<User> list);

}
