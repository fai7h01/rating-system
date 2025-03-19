package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.CommentDTO;
import com.luka.gamesellerrating.mapper.CommentMapper;
import com.luka.gamesellerrating.repository.CommentRepository;
import com.luka.gamesellerrating.service.AIService;
import com.luka.gamesellerrating.service.CommentService;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final AIService aiService;

    public CommentServiceImpl(CommentRepository commentRepository, CommentMapper commentMapper,
                              AIService aiService) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.aiService = aiService;
    }

    @Override
    public CommentDTO save(CommentDTO comment) {
        var commentEntity = commentMapper.toEntity(comment);
        String sentiment = aiService.analyzeSentiment(comment.getMessage());
        commentEntity.setSentimentAnalysis(sentiment);
        var savedComment = commentRepository.save(commentEntity);
        return commentMapper.toDto(savedComment);
    }
}
