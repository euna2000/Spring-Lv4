package com.sparta.springblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.springblog.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
