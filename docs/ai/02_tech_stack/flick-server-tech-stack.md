# 기술 스택 상세 (flick-server Tech Stack)

실측 근거: `build.gradle`, `src/main/resources/application*.yml` (2026-08-09 기준)

## 1. 언어·프레임워크

- **Java 17** (toolchain 고정)
- **Spring Boot 4.0.5** / `io.spring.dependency-management` 1.1.7
- **group:** `kr.co.flick`

## 2. 영속·DB

- **MyBatis** 4.0.1 (`mybatis-spring-boot-starter`) — JPA 미사용, 전량 MyBatis
- **Oracle** (`ojdbc11` 21.9.0.0 + `oraclepki`/`osdt_core`/`osdt_cert` — TNS Wallet 인증, `TNS_ADMIN=C:/wallet`)
- **PageHelper** 1.4.6 (`pagehelper-spring-boot-starter`, `helper-dialect: oracle`) — 페이징
- **P6Spy** 3.9.0 — SQL 로깅 (`CustomP6SpyLogger`, `P6spyPrettySqlFormatter`로 포맷)
- MyBatis 설정: `map-underscore-to-camel-case: true`

## 3. 인증·보안

- **Spring Security** (`spring-boot-starter-security`) + **OAuth2 Client**(`spring-boot-starter-oauth2-client`) — 카카오/구글/깃허브 소셜 로그인
- **JWT**: `io.jsonwebtoken:jjwt` 0.11.5 (api/impl/jackson). HS256, 커스텀 `JwtTokenUtil`/`JwtAuthenticationFilter`
- **Redis**: `spring-boot-starter-data-redis` — refresh token 저장(`refresh:{userId}`)
- **PasswordConfig** — 비밀번호 인코더 빈 (소셜 전용 가입자는 `PASSWORD_HASH`가 null)

## 4. 문서화·검증

- **springdoc-openapi-starter-webmvc-ui** 3.0.3 — `/v3/api-docs`, `/swagger-ui.html`
- **spring-boot-starter-validation** — `@Valid` + `MethodArgumentNotValidException` 전역 처리

## 5. 기타

- **Lombok** (compileOnly + annotationProcessor)
- **commons-lang3**
- **classmate** 1.7.3 (springdoc 의존성 충돌 회피용으로 명시)
- **jacoco** 0.8.12 — 커버리지 (제외 대상: Application, dto, configuration, common/response, common/code, common/interceptor, mapper)

## 6. 빌드·배포

- Gradle (wrapper 포함, Windows에서도 빌드 가능 — A-RMS와 달리 `wget` 의존 없음)
- 배포: `.github/workflows/backend.yml` — `workflow_dispatch` 수동 트리거, self-hosted runner, `./gradlew build -x test`, systemd(`app.service`) 재시작. 상세는 [`13_deploy_runbook`](../13_deploy_runbook/flick-server-deploy-runbook.md).

## 7. 설정 파일 구조

| 파일                     | 역할                                                                                                           |
| ------------------------ | -------------------------------------------------------------------------------------------------------------- |
| `application.yml`        | 공통 설정 (mybatis, pagehelper, jwt 만료시간, springdoc). `profiles.active: local`, `profiles.include: secret` |
| `application-local.yml`  | 로컬 프로파일 — DB 접속(Oracle wallet), 포트 9090, 파일 업로드 경로, 프론트 URL                                |
| `application-dev.yml`    | dev 프로파일                                                                                                   |
| `application-secret.yml` | **gitignore 대상.** OAuth2 클라이언트 시크릿 등 실 크리덴셜. 절대 이 문서 체계에 값을 옮겨 적지 않는다         |
