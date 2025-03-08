package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.CommentDTO;
import com.luka.gamesellerrating.entity.Comment;
import com.luka.gamesellerrating.repository.CommentRepository;
import com.luka.gamesellerrating.service.CommentService;
import com.luka.gamesellerrating.util.MapperUtil;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final MapperUtil mapperUtil;

    public CommentServiceImpl(CommentRepository commentRepository, MapperUtil mapperUtil) {
        this.commentRepository = commentRepository;
        this.mapperUtil = mapperUtil;
    }


    @Override
    public CommentDTO save(CommentDTO comment) {
        var commentEntity = mapperUtil.convert(comment, new Comment());
        var savedComment = commentRepository.save(commentEntity);
        return mapperUtil.convert(savedComment, new CommentDTO());
    }
}
