package com.seongho.backend_core_lab.domain.post.service;

import com.seongho.backend_core_lab.domain.notification.service.NotificationService;
import com.seongho.backend_core_lab.domain.post.dto.CreatePostRequest;
import com.seongho.backend_core_lab.domain.post.dto.PostListResponse;
import com.seongho.backend_core_lab.domain.post.dto.PostResponse;
import com.seongho.backend_core_lab.domain.post.dto.UpdatePostRequest;
import com.seongho.backend_core_lab.domain.post.entity.Post;
import com.seongho.backend_core_lab.domain.post.entity.PostLike;
import com.seongho.backend_core_lab.domain.post.repository.PostLikeRepository;
import com.seongho.backend_core_lab.domain.post.repository.PostRepository;
import com.seongho.backend_core_lab.domain.user.entity.User;
import com.seongho.backend_core_lab.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    
    @Transactional
    public PostResponse createPost(Long userId, CreatePostRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(user)
                .build();
        
        Post savedPost = postRepository.save(post);
        log.info("게시글 생성: ID={}, 작성자={}", savedPost.getId(), user.getUsername());
        
        return PostResponse.from(savedPost);
    }
    
    @Transactional(readOnly = true)
    public PostListResponse getAllPosts() {
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
        
        List<PostResponse> postResponses = posts.stream()
                .map(PostResponse::from)
                .collect(Collectors.toList());
        
        return PostListResponse.builder()
                .posts(postResponses)
                .totalCount(postResponses.size())
                .build();
    }
    
    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
        
        return PostResponse.from(post);
    }
    
    @Transactional
    public PostResponse updatePost(Long userId, Long postId, UpdatePostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
        
        if (!post.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("게시글 수정 권한이 없습니다");
        }
        
        post.update(request.getTitle(), request.getContent());
        log.info("게시글 수정: ID={}", postId);
        
        return PostResponse.from(post);
    }
    
    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
        
        if (!post.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("게시글 삭제 권한이 없습니다");
        }
        
        postRepository.delete(post);
        log.info("게시글 삭제: ID={}", postId);
    }
    
    @Transactional
    public void toggleLike(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        
        boolean alreadyLiked = postLikeRepository.existsByPostIdAndUserId(postId, userId);
        
        if (alreadyLiked) {
            // 좋아요 취소
            PostLike postLike = postLikeRepository.findByPostIdAndUserId(postId, userId)
                    .orElseThrow(() -> new IllegalArgumentException("좋아요를 찾을 수 없습니다"));
            postLikeRepository.delete(postLike);
            post.decrementLikeCount();
            log.info("좋아요 취소: 게시글ID={}, 사용자ID={}", postId, userId);
        } else {
            // 좋아요 추가
            PostLike postLike = PostLike.builder()
                    .post(post)
                    .user(user)
                    .build();
            postLikeRepository.save(postLike);
            post.incrementLikeCount();
            log.info("좋아요 추가: 게시글ID={}, 사용자ID={}", postId, userId);
            
            // 본인 글이 아닐 경우 알림 전송
            if (!post.getAuthor().getId().equals(userId)) {
                String likerName = user.getUsername() != null ? user.getUsername() : user.getEmail();
                notificationService.createNotification(
                        post.getAuthor().getId(),
                        likerName + "님이 회원님의 게시글을 좋아합니다",
                        postId
                );
            }
        }
    }
}
