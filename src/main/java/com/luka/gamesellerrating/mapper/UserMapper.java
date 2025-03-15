package com.luka.gamesellerrating.mapper;


import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDto(User entity);
    User toEntity(UserDTO dto);
    List<UserDTO> toDtoList(List<User> list);
}
