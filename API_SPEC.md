# 게시판 API 명세서

## 기본 정보
- Base URL: `http://localhost:8080`
- 인증 방식: JWT Bearer Token
- Content-Type: `application/json`

---

## 1. 인증 API

### 1.1 회원가입
```http
POST /auth/signup
```

**Request Body**
```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}
```

**Response (201 Created)**
```json
{
  "userId": 1,
  "username": "testuser",
  "email": "test@example.com",
  "role": "USER",
  "message": "회원가입이 완료되었습니다"
}
```

### 1.2 로그인
```http
POST /auth/login
```

**Request Body**
```json
{
  "username": "testuser",
  "password": "password123"
}
```

**Response (200 OK)**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "testuser",
  "role": "USER"
}
```

### 1.3 로그아웃
```http
POST /auth/logout
Authorization: Bearer {accessToken}
```

**Response (200 OK)**
```json
"로그아웃되었습니다"
```

### 1.4 토큰 갱신
```http
POST /auth/refresh
```

**Request Body**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200 OK)**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 1.5 OAuth2 소셜 로그인 (Google)

#### 1.5.1 Google 로그인 시작
```http
GET /oauth2/authorization/google
```

**동작 흐름**
1. 사용자가 이 URL에 접근
2. 자동으로 Google 로그인 페이지로 리디렉션
3. 사용자가 Google 계정으로 로그인
4. Google이 `/login/oauth2/code/google`로 콜백
5. 백엔드가 JWT 토큰 발급 후 프론트엔드로 리디렉션

**프론트엔드에서 호출 방법**
```javascript
// 버튼 클릭 시 Google 로그인 페이지로 이동
window.location.href = 'http://localhost:8080/oauth2/authorization/google';
```

#### 1.5.2 OAuth2 콜백 처리 (자동)
```http
GET /login/oauth2/code/google?code={authorization_code}
```

**Response (자동 리디렉션)**
- 프론트엔드 URL로 자동 리디렉션: `http://localhost:3000/oauth2/redirect?token={accessToken}&refreshToken={refreshToken}`

**프론트엔드에서 토큰 받기**
```javascript
// oauth2/redirect 페이지에서 URL 파라미터로 토큰 추출
const urlParams = new URLSearchParams(window.location.search);
const accessToken = urlParams.get('token');
const refreshToken = urlParams.get('refreshToken');

// 로컬 스토리지에 저장
localStorage.setItem('accessToken', accessToken);
localStorage.setItem('refreshToken', refreshToken);

// 메인 페이지로 이동
window.location.href = '/';
```

#### 1.5.3 Kakao 로그인 (비활성화)
```http
GET /oauth2/authorization/kakao
```

**참고**
- Kakao OAuth2는 코드로 구현되어 있으나 비즈앱 등록이 필요하여 현재 비활성화 상태입니다
- 활성화 시 Google과 동일한 방식으로 동작합니다

---

## 2. 게시글 API

### 2.1 게시글 작성
```http
POST /posts
Authorization: Bearer {accessToken}
```

**Request Body**
```json
{
  "title": "게시글 제목",
  "content": "게시글 내용입니다."
}
```

**Response (201 Created)**
```json
{
  "id": 1,
  "title": "게시글 제목",
  "content": "게시글 내용입니다.",
  "authorName": "testuser",
  "likeCount": 0,
  "createdAt": "2026-02-10T10:30:00",
  "updatedAt": "2026-02-10T10:30:00"
}
```

### 2.2 게시글 목록 조회
```http
GET /posts
Authorization: Bearer {accessToken}
```

**Response (200 OK)**
```json
{
  "posts": [
    {
      "id": 2,
      "title": "두 번째 게시글",
      "content": "내용...",
      "authorName": "user2",
      "likeCount": 5,
      "createdAt": "2026-02-10T11:00:00",
      "updatedAt": "2026-02-10T11:00:00"
    },
    {
      "id": 1,
      "title": "첫 번째 게시글",
      "content": "내용...",
      "authorName": "testuser",
      "likeCount": 3,
      "createdAt": "2026-02-10T10:30:00",
      "updatedAt": "2026-02-10T10:30:00"
    }
  ],
  "totalCount": 2
}
```

### 2.3 게시글 상세 조회
```http
GET /posts/{postId}
Authorization: Bearer {accessToken}
```

**Response (200 OK)**
```json
{
  "id": 1,
  "title": "게시글 제목",
  "content": "게시글 내용입니다.",
  "authorName": "testuser",
  "likeCount": 3,
  "createdAt": "2026-02-10T10:30:00",
  "updatedAt": "2026-02-10T10:30:00"
}
```

### 2.4 게시글 수정
```http
PUT /posts/{postId}
Authorization: Bearer {accessToken}
```

**Request Body**
```json
{
  "title": "수정된 제목",
  "content": "수정된 내용입니다."
}
```

**Response (200 OK)**
```json
{
  "id": 1,
  "title": "수정된 제목",
  "content": "수정된 내용입니다.",
  "authorName": "testuser",
  "likeCount": 3,
  "createdAt": "2026-02-10T10:30:00",
  "updatedAt": "2026-02-10T12:00:00"
}
```

### 2.5 게시글 삭제
```http
DELETE /posts/{postId}
Authorization: Bearer {accessToken}
```

**Response (204 No Content)**

### 2.6 좋아요 토글
```http
POST /posts/{postId}/like
Authorization: Bearer {accessToken}
```

**Response (200 OK)**
- 좋아요가 추가되거나 취소됩니다
- 본인 글이 아닐 경우 작성자에게 알림이 전송됩니다

---

## 3. 알림 API (SSE)

### 3.1 알림 구독
```http
GET /notifications/subscribe
Authorization: Bearer {accessToken}
Accept: text/event-stream
```

**SSE Stream Response**
```
event: connect
data: 알림 구독 완료

event: notification
data: {"id":1,"message":"user2님이 회원님의 게시글을 좋아합니다","postId":1,"isRead":false,"createdAt":"2026-02-10T13:00:00"}
```

### 3.2 알림 목록 조회
```http
GET /notifications
Authorization: Bearer {accessToken}
```

**Response (200 OK)**
```json
[
  {
    "id": 2,
    "message": "user3님이 회원님의 게시글을 좋아합니다",
    "postId": 1,
    "isRead": false,
    "createdAt": "2026-02-10T14:00:00"
  },
  {
    "id": 1,
    "message": "user2님이 회원님의 게시글을 좋아합니다",
    "postId": 1,
    "isRead": true,
    "createdAt": "2026-02-10T13:00:00"
  }
]
```

### 3.3 읽지 않은 알림 조회
```http
GET /notifications/unread
Authorization: Bearer {accessToken}
```

**Response (200 OK)**
```json
[
  {
    "id": 2,
    "message": "user3님이 회원님의 게시글을 좋아합니다",
    "postId": 1,
    "isRead": false,
    "createdAt": "2026-02-10T14:00:00"
  }
]
```

### 3.4 알림 읽음 처리
```http
PATCH /notifications/{notificationId}/read
Authorization: Bearer {accessToken}
```

**Response (200 OK)**

---

## 4. 에러 응답

### 4.1 인증 에러 (401 Unauthorized)
```json
{
  "error": "인증이 필요합니다"
}
```

```json
{
  "error": "유효하지 않은 토큰입니다"
}
```

### 4.2 권한 에러 (403 Forbidden)
```json
{
  "error": "게시글 수정 권한이 없습니다"
}
```

### 4.3 리소스 없음 (404 Not Found)
```json
{
  "error": "게시글을 찾을 수 없습니다"
}
```

### 4.4 유효성 검증 에러 (400 Bad Request)
```json
{
  "error": "제목은 필수입니다"
}
```

---

## 5. 프론트엔드 SSE 연결 예제

### JavaScript
```javascript
const eventSource = new EventSource('http://localhost:8080/notifications/subscribe', {
  headers: {
    'Authorization': 'Bearer ' + accessToken
  }
});

eventSource.addEventListener('connect', (e) => {
  console.log('연결됨:', e.data);
});

eventSource.addEventListener('notification', (e) => {
  const notification = JSON.parse(e.data);
  console.log('새 알림:', notification);
  // UI 업데이트 로직
});

eventSource.onerror = (error) => {
  console.error('SSE 에러:', error);
  eventSource.close();
};
```

### React 예제
```javascript
useEffect(() => {
  const eventSource = new EventSource('http://localhost:8080/notifications/subscribe', {
    headers: {
      'Authorization': `Bearer ${accessToken}`
    }
  });

  eventSource.addEventListener('notification', (e) => {
    const notification = JSON.parse(e.data);
    setNotifications(prev => [notification, ...prev]);
    toast.success(notification.message);
  });

  return () => eventSource.close();
}, [accessToken]);
```

---

## 6. 주의사항

1. **인증 필수**: `/auth/signup`, `/auth/login`, `/auth/refresh`, `/oauth2/**`, `/login/oauth2/code/**`를 제외한 모든 API는 인증이 필요합니다
2. **Authorization 헤더**: `Bearer {accessToken}` 형식으로 전달
3. **SSE 연결**: 브라우저의 EventSource API 사용, 30분 타임아웃 설정됨
4. **게시글 수정/삭제**: 작성자 본인만 가능
5. **좋아요 중복 방지**: 한 사용자당 하나의 게시글에 한 번만 좋아요 가능
6. **알림**: 본인 글에 본인이 좋아요를 누르면 알림이 생성되지 않음
7. **OAuth2 로그인**: Google OAuth2만 활성화 상태, Kakao는 비즈앱 등록 후 사용 가능
8. **OAuth2 리디렉션**: 로그인 성공 시 `http://localhost:3000/oauth2/redirect`로 자동 리디렉션 (환경변수로 변경 가능)

---

## 7. RBAC (역할 기반 접근 제어)

현재 시스템에는 두 가지 역할이 있습니다:
- **USER**: 일반 사용자 (게시글 작성, 좋아요, 알림 등 기본 기능 사용 가능)
- **ADMIN**: 관리자 (모든 기능 사용 가능)

관리자 전용 기능이 필요한 경우 `/admin` 엔드포인트를 통해 확장 가능합니다.
