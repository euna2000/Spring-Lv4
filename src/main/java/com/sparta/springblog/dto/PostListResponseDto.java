package com.sparta.springblog.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PostListResponseDto {
    private List<com.thesun4sky.springblog.dto.PostResponseDto> postsList;

    public PostListResponseDto(List<com.thesun4sky.springblog.dto.PostResponseDto> postList) {
        this.postsList = postList;
    }
}