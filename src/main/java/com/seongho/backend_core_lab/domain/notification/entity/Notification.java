package com.seongho.backend_core_lab.domain.notification.entity;

import com.seongho.backend_core_lab.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, length = 500)
    private String message;
    
    @Column(nullable = false)
    private Long postId;
    
    @Column(nullable = false)
    private Boolean isRead = false;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Builder
    public Notification(User user, String message, Long postId) {
        this.user = user;
        this.message = message;
        this.postId = postId;
        this.isRead = false;
    }
    
    public void markAsRead() {
        this.isRead = true;
    }
}
