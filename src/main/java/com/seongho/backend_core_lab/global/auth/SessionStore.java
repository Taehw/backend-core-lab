package com.seongho.backend_core_lab.global.auth;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component 
public class SessionStore {
    
    private final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>(); // ConcurrentHashMap: 멀티스레드 환경에서 안전한 HashMap
    
    /**
     * 새로운 세션 생성
     * 
     * UUID를 사용하여 예측 불가능한 세션 ID 생성
     * ConcurrentHashMap에 저장하여 멀티스레드 환경에서 안전
     * 
     * @param sessionInfo 저장할 세션 정보
     * @return 생성된 세션 ID
     */
    public String createSession(SessionInfo sessionInfo) {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, sessionInfo);
        return sessionId;
    }
    
    /**
     * 세션 ID로 세션 정보 조회
     * 
     * @param sessionId 세션 ID
     * @return 세션 정보 (없으면 Optional.empty())
     */
    public Optional<SessionInfo> getSession(String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }
    
    /**
     * 세션 삭제 (로그아웃)
     * 
     * @param sessionId 삭제할 세션 ID
     */
    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }
    
    /**
     * 세션 존재 여부 확인
     * 
     * @param sessionId 확인할 세션 ID
     * @return 존재하면 true
     */
    public boolean hasSession(String sessionId) {
        return sessions.containsKey(sessionId);
    }
    
    /**
     * 현재 활성 세션 수 조회
     * 
     * @return 활성 세션 개수
     */
    public int getActiveSessionCount() {
        return sessions.size();
    }
}
