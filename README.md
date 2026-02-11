# Backend Core Lab - 게시판 프로젝트

Spring Boot 기반 게시판 백엔드 API 프로젝트입니다.

## 주요 기능

### ✅ 구현된 기능

- **인증/인가**
  - 회원가입 / 로그인
  - JWT 기반 인증
  - OAuth2 소셜 로그인 (Google, Kakao)
  - Refresh Token을 통한 토큰 갱신
  - RBAC (Role-Based Access Control) - ADMIN, USER

- **게시판**
  - 게시글 작성 / 수정 / 삭제
  - 게시글 목록 조회 (최신순)
  - 게시글 상세 조회
  - 좋아요 기능 (토글 방식)

- **알림**
  - SSE (Server-Sent Events) 기반 실시간 알림
  - 내 게시글에 좋아요가 달리면 알림 전송
  - 알림 목록 조회
  - 읽지 않은 알림 조회
  - 알림 읽음 처리

## 기술 스택

- **Framework**: Spring Boot 4.0.2
- **Language**: Java 17
- **Database**: H2 (In-Memory)
- **ORM**: JPA/Hibernate
- **Authentication**: JWT + OAuth2
- **Real-time**: SSE (Server-Sent Events)
- **Build Tool**: Gradle

## 프로젝트 구조

```
src/main/java/com/seongho/backend_core_lab/
├── domain/
│   ├── auth/          # 인증 관련
│   ├── user/          # 사용자 엔티티
│   ├── post/          # 게시글 관련
│   └── notification/  # 알림 관련
├── global/
│   ├── auth/          # 세션 정보
│   ├── config/        # 설정 클래스
│   ├── filter/        # 인증 필터
│   ├── interceptor/   # 권한 인터셉터
│   ├── jwt/           # JWT 토큰 처리
│   └── util/          # 유틸리티
└── BackendCoreLabApplication.java
```

## 시작하기

### 1. 프로젝트 빌드
```bash
./gradlew build
```

### 2. 서버 실행
```bash
./gradlew bootRun
```

서버는 `http://localhost:8080`에서 실행됩니다.

### 3. API 테스트
```bash
powershell -ExecutionPolicy Bypass -File test-api.ps1
```

## API 문서

자세한 API 명세는 [API_SPEC.md](API_SPEC.md) 파일을 참고하세요.

### 주요 엔드포인트

#### 인증
- `POST /auth/signup` - 회원가입
- `POST /auth/login` - 로그인
- `POST /auth/logout` - 로그아웃
- `POST /auth/refresh` - 토큰 갱신

#### 게시글
- `POST /posts` - 게시글 작성
- `GET /posts` - 게시글 목록 조회
- `GET /posts/{id}` - 게시글 상세 조회
- `PUT /posts/{id}` - 게시글 수정
- `DELETE /posts/{id}` - 게시글 삭제
- `POST /posts/{id}/like` - 좋아요 토글

#### 알림
- `GET /notifications/subscribe` - SSE 알림 구독
- `GET /notifications` - 알림 목록 조회
- `GET /notifications/unread` - 읽지 않은 알림 조회
- `PATCH /notifications/{id}/read` - 알림 읽음 처리

## 배포

### ngrok으로 외부 공개
```bash
ngrok http 8080
```

자세한 배포 가이드는 [DEPLOYMENT.md](DEPLOYMENT.md) 파일을 참고하세요.

## 데이터베이스

H2 인메모리 데이터베이스를 사용합니다.

### H2 Console 접속
- URL: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: (빈 값)

## 개발 참고사항

### 인증
- 모든 API는 `/auth/signup`, `/auth/login`, `/auth/refresh`를 제외하고 JWT 인증이 필요합니다
- Authorization 헤더에 `Bearer {accessToken}` 형식으로 토큰을 전달해야 합니다

### SSE 알림
- EventSource API를 사용하여 실시간 알림을 받을 수 있습니다
- 타임아웃: 30분
- 본인 게시글에 본인이 좋아요를 누르면 알림이 생성되지 않습니다

### RBAC
- USER: 일반 사용자 (기본 권한)
- ADMIN: 관리자 권한

## 프론트엔드 연동

### SSE 연결 예제
```javascript
const eventSource = new EventSource('http://localhost:8080/notifications/subscribe', {
  headers: {
    'Authorization': 'Bearer ' + accessToken
  }
});

eventSource.addEventListener('notification', (e) => {
  const notification = JSON.parse(e.data);
  console.log('새 알림:', notification);
});
```

자세한 예제는 [API_SPEC.md](API_SPEC.md)의 5번 항목을 참고하세요.

## 라이선스

이 프로젝트는 학습 목적으로 제작되었습니다.
