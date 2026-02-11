package com.seongho.backend_core_lab.domain.notification.service;

import com.seongho.backend_core_lab.domain.notification.dto.NotificationResponse;
import com.seongho.backend_core_lab.domain.notification.entity.Notification;
import com.seongho.backend_core_lab.domain.notification.repository.NotificationRepository;
import com.seongho.backend_core_lab.domain.user.entity.User;
import com.seongho.backend_core_lab.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    
    // 사용자별 SSE Emitter 저장
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    
    // SSE 연결 생성 (타임아웃 30분)
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);
        emitters.put(userId, emitter);
        
        emitter.onCompletion(() -> {
            log.info("SSE 연결 완료: 사용자ID={}", userId);
            emitters.remove(userId);
        });
        
        emitter.onTimeout(() -> {
            log.info("SSE 연결 타임아웃: 사용자ID={}", userId);
            emitters.remove(userId);
        });
        
        emitter.onError((e) -> {
            log.error("SSE 연결 에러: 사용자ID={}", userId, e);
            emitters.remove(userId);
        });
        
        // 초기 연결 확인 메시지
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("알림 구독 완료"));
        } catch (IOException e) {
            log.error("SSE 초기 메시지 전송 실패", e);
        }
        
        log.info("SSE 연결 생성: 사용자ID={}", userId);
        return emitter;
    }
    
    @Transactional
    public void createNotification(Long userId, String message, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        
        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .postId(postId)
                .build();
        
        Notification savedNotification = notificationRepository.save(notification);
        log.info("알림 생성: ID={}, 사용자ID={}", savedNotification.getId(), userId);
        
        // SSE로 실시간 알림 전송
        sendNotification(userId, NotificationResponse.from(savedNotification));
    }
    
    private void sendNotification(Long userId, NotificationResponse notification) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notification));
                log.info("알림 전송 성공: 사용자ID={}", userId);
            } catch (IOException e) {
                log.error("알림 전송 실패: 사용자ID={}", userId, e);
                emitters.remove(userId);
            }
        }
    }
    
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(Long userId) {
        List<Notification> notifications = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId);
        
        return notifications.stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        List<Notification> notifications = notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        
        return notifications.stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void markAsRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다"));
        
        if (!notification.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("알림에 대한 권한이 없습니다");
        }
        
        notification.markAsRead();
        log.info("알림 읽음 처리: ID={}", notificationId);
    }
}
