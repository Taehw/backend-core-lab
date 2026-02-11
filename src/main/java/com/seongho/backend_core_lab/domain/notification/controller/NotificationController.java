package com.seongho.backend_core_lab.domain.notification.controller;

import com.seongho.backend_core_lab.domain.notification.dto.NotificationResponse;
import com.seongho.backend_core_lab.domain.notification.service.NotificationService;
import com.seongho.backend_core_lab.global.auth.SessionInfo;
import com.seongho.backend_core_lab.global.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;
    
    /**
     * SSE(Server-Sent Events) 구독 엔드포인트
     * 
     * EventSource는 헤더를 설정할 수 없으므로, 토큰을 쿼리 파라미터로 받아 검증
     * 프론트엔드에서: new EventSource(`/notifications/subscribe?token=${accessToken}`)
     */
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam(required = false) String token) {
        // 토큰이 없으면 401 에러
        if (token == null || token.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "토큰이 필요합니다");
        }
        
        // 토큰 검증
        if (!jwtTokenProvider.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다");
        }
        
        // 토큰에서 사용자 ID 추출
        Long userId = jwtTokenProvider.getUserId(token);
        
        return notificationService.subscribe(userId);
    }
    
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(HttpServletRequest request) {
        SessionInfo sessionInfo = (SessionInfo) request.getAttribute("sessionInfo");
        List<NotificationResponse> notifications = notificationService.getNotifications(sessionInfo.getUserId());
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(HttpServletRequest request) {
        SessionInfo sessionInfo = (SessionInfo) request.getAttribute("sessionInfo");
        List<NotificationResponse> notifications = notificationService.getUnreadNotifications(sessionInfo.getUserId());
        return ResponseEntity.ok(notifications);
    }
    
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            HttpServletRequest request,
            @PathVariable Long notificationId) {
        
        SessionInfo sessionInfo = (SessionInfo) request.getAttribute("sessionInfo");
        notificationService.markAsRead(sessionInfo.getUserId(), notificationId);
        
        return ResponseEntity.ok().build();
    }
}
