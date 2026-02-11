package com.seongho.backend_core_lab.domain.notification.dto;

import com.seongho.backend_core_lab.domain.notification.entity.Notification;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {
    
    private Long id;
    private String message;
    private Long postId;
    private Boolean isRead;
    private LocalDateTime createdAt;
    
    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .postId(notification.getPostId())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
