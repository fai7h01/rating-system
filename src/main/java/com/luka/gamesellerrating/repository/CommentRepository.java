package com.luka.gamesellerrating.repository;

import com.luka.gamesellerrating.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
