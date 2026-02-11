package com.seongho.backend_core_lab.domain.post.dto;

import com.seongho.backend_core_lab.domain.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostResponse {
    
    private Long id;
    private String title;
    private String content;
    private String authorName;
    private Long likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorName(post.getAuthor().getUsername() != null 
                        ? post.getAuthor().getUsername() 
                        : post.getAuthor().getEmail())
                .likeCount(post.getLikeCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
