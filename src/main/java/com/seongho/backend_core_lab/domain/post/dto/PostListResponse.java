package com.seongho.backend_core_lab.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostListResponse {
    
    private List<PostResponse> posts;
    private int totalCount;
}
