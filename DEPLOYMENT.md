# 배포 가이드

## 1. 로컬에서 실행하기

### 애플리케이션 실행
```bash
# Gradle 사용
./gradlew bootRun

# 또는 Windows에서
gradlew.bat bootRun
```

서버가 `http://localhost:8080`에서 실행됩니다.

---

## 2. ngrok으로 외부 공개하기

ngrok을 사용하면 로컬 서버를 외부에서 접근 가능한 URL로 공개할 수 있습니다.

### ngrok 설치
1. https://ngrok.com/ 에서 회원가입
2. ngrok 다운로드 및 설치
3. 인증 토큰 설정
```bash
ngrok config add-authtoken <your-authtoken>
```

### ngrok 실행
로컬 서버(8080 포트)를 실행한 후:

```bash
ngrok http 8080
```

다음과 같은 화면이 나타납니다:
```
Forwarding  https://xxxx-xxx-xxx-xxx.ngrok-free.app -> http://localhost:8080
```

이 URL을 사용하여 외부에서 접근할 수 있습니다.

### 주의사항
- ngrok 무료 버전은 세션이 종료되면 URL이 변경됩니다
- 실행 중인 ngrok을 종료하지 마세요 (Ctrl+C로 종료)
- 새로운 ngrok URL이 생성되면 프론트엔드에 새 URL을 전달하세요

---

## 3. 실제 배포 (선택사항)

### 3.1 Heroku 배포
```bash
# Heroku CLI 설치 후
heroku login
heroku create your-app-name
git push heroku main
```

### 3.2 AWS EC2 배포
1. EC2 인스턴스 생성
2. Java 17 설치
3. 애플리케이션 빌드 및 실행
```bash
# 빌드
./gradlew build

# 실행
java -jar build/libs/backend-core-lab-0.0.1-SNAPSHOT.jar
```

### 3.3 Docker 배포
```dockerfile
# Dockerfile 예시
FROM openjdk:17-jdk-slim
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```bash
# 빌드 및 실행
docker build -t backend-core-lab .
docker run -p 8080:8080 backend-core-lab
```

---

## 4. 프론트엔드 연동

프론트엔드에서 사용할 Base URL:
- 로컬: `http://localhost:8080`
- ngrok: `https://xxxx-xxx-xxx-xxx.ngrok-free.app`
- 배포: `https://your-domain.com`

API 명세서는 `API_SPEC.md` 파일을 참고하세요.

---

## 5. 테스트

### API 테스트 (Postman/Thunder Client)

1. **회원가입**
```http
POST http://localhost:8080/auth/signup
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}
```

2. **로그인**
```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

3. **게시글 작성** (로그인 후 받은 accessToken 사용)
```http
POST http://localhost:8080/posts
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "title": "테스트 게시글",
  "content": "테스트 내용입니다."
}
```

4. **SSE 알림 구독** (브라우저 콘솔에서 테스트)
```javascript
const eventSource = new EventSource('http://localhost:8080/notifications/subscribe', {
  headers: {
    'Authorization': 'Bearer ' + accessToken
  }
});

eventSource.addEventListener('notification', (e) => {
  console.log('새 알림:', JSON.parse(e.data));
});
```

---

## 6. 문제 해결

### CORS 에러 발생 시
프론트엔드 도메인을 CORS 설정에 추가해야 합니다.
`WebConfig.java`에서 허용할 origin을 추가하세요.

### JWT 토큰 만료
Access Token이 만료되면 Refresh Token을 사용하여 갱신하세요.
```http
POST /auth/refresh
{
  "refreshToken": "your-refresh-token"
}
```

### H2 데이터베이스 초기화
서버를 재시작하면 인메모리 H2 데이터베이스가 초기화됩니다.
데이터를 유지하려면 `application.properties`에서 파일 기반 DB로 변경하세요.
```properties
spring.datasource.url=jdbc:h2:file:./data/testdb
```
