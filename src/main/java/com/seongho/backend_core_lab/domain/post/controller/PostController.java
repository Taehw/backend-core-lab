package com.seongho.backend_core_lab.domain.post.controller;

import com.seongho.backend_core_lab.domain.post.dto.CreatePostRequest;
import com.seongho.backend_core_lab.domain.post.dto.PostListResponse;
import com.seongho.backend_core_lab.domain.post.dto.PostResponse;
import com.seongho.backend_core_lab.domain.post.dto.UpdatePostRequest;
import com.seongho.backend_core_lab.domain.post.service.PostService;
import com.seongho.backend_core_lab.global.auth.SessionInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    
    private final PostService postService;
    
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            HttpServletRequest request,
            @Valid @RequestBody CreatePostRequest postRequest) {
        
        SessionInfo sessionInfo = (SessionInfo) request.getAttribute("sessionInfo");
        PostResponse response = postService.createPost(sessionInfo.getUserId(), postRequest);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<PostListResponse> getAllPosts() {
        PostListResponse response = postService.getAllPosts();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        PostResponse response = postService.getPost(postId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            HttpServletRequest request,
            @PathVariable Long postId,
            @Valid @RequestBody UpdatePostRequest postRequest) {
        
        SessionInfo sessionInfo = (SessionInfo) request.getAttribute("sessionInfo");
        PostResponse response = postService.updatePost(sessionInfo.getUserId(), postId, postRequest);
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            HttpServletRequest request,
            @PathVariable Long postId) {
        
        SessionInfo sessionInfo = (SessionInfo) request.getAttribute("sessionInfo");
        postService.deletePost(sessionInfo.getUserId(), postId);
        
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> toggleLike(
            HttpServletRequest request,
            @PathVariable Long postId) {
        
        SessionInfo sessionInfo = (SessionInfo) request.getAttribute("sessionInfo");
        postService.toggleLike(sessionInfo.getUserId(), postId);
        
        return ResponseEntity.ok().build();
    }
}
