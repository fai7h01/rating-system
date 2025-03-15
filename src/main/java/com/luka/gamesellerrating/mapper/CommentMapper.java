package com.luka.gamesellerrating.mapper;

import com.luka.gamesellerrating.dto.CommentDTO;
import com.luka.gamesellerrating.entity.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentDTO toDto(Comment entity);
    Comment toEntity(CommentDTO dto);
}
