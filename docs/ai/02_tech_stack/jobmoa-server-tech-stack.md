# 기술 스택 상세 (jobmoa-server Tech Stack)

실측 근거: `build.gradle`, `src/main/resources/application*.yml` (2026-08-09 기준)

## 1. 언어·프레임워크

- **Java 17** (toolchain 고정)
- **Spring Boot 4.0.5** / `io.spring.dependency-management` 1.1.7
- **group:** `kr.co.jobmoa`

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

## 4-1. AI (Spring AI — OpenAI 연동)

실측 근거: `build.gradle`, `application.yml`, `application-secret.yml`, `kr.co.jobmoa.aichat.*` (2026-09-08 기준). **개인화 추천(Function Calling) 엔드포인트까지 구현됨. 일반 지식 RAG·벡터스토어는 아직 계획 단계.** 상세는 [`06_domain_playbooks/ai-chat.md`](../06_domain_playbooks/ai-chat.md) 참고.

- **Spring AI** `2.0.1` — `org.springframework.ai:spring-ai-bom:2.0.1`을 `dependencyManagement { imports { mavenBom ... } }`로 임포트, `org.springframework.ai:spring-ai-starter-model-openai` 스타터 적용. Spring Boot 4.0.x/4.1.x 호환 라인(GA 2026-06월경, Boot 4.x용 메이저 버전 — Boot 3.x 시절 `1.x` 라인과 구분됨).
  - ⚠️ **버전 확인 시 주의:** `spring-ai-starter-model-openai`의 실제 POM은 `spring-boot-starter-webclient`/`spring-boot-starter-restclient`를 `4.1.1`로 명시(Spring AI 2.0.x가 발행 시점 최신 Boot 4.1 기준으로 빌드됨 — [spring-ai#6465](https://github.com/spring-projects/spring-ai/issues/6465)). jobmoa-server는 `org.springframework.boot` Gradle 플러그인이 걸어두는 버전 정렬 규칙 덕분에 `./gradlew dependencyInsight --dependency spring-boot-starter-webclient` 기준 실제로는 프로젝트 Boot 버전(`4.0.5`)으로 정상 다운그레이드됨을 확인함(`4.1.1 -> 4.0.5 (selected by rule)`). Maven이나 `platform()`만 쓰는 순수 Gradle 구성이면 버전이 섞일 수 있으니 다른 프로젝트에 이식 시 재검증할 것.
  - 아티팩트명 주의: 구버전 네이밍 `spring-ai-openai-spring-boot-starter`/`spring-ai-starter-openai`는 2.x에서 존재하지 않음(404) — 반드시 `spring-ai-starter-model-openai` 사용.
- **모델 종류 선택적 활성화**: `application.yml`의 `spring.ai.model.*`로 `chat`만 `openai`로 켜고 `embedding`/`image`/`moderation`/`audio.speech`/`audio.transcription`은 전부 `none`으로 꺼둠 — 이 프로젝트는 채팅 모델만 쓸 예정이라 불필요한 모델 빈 생성(및 그에 따른 API 키 요구)을 막기 위함.
- **API 키**: `application-secret.yml`에 `spring.ai.openai.api-key: ${OPENAI_API_KEY:sk-not-set}` 형태로 플레이스홀더만 존재(gitignore 대상, 실제 키 값 없음). Spring AI의 `OpenAiChatModel`은 애플리케이션 컨텍스트 기동 시점에 즉시(eager) 생성되며 API 키가 비어 있으면 `IllegalStateException`으로 기동 자체가 실패한다 — 그래서 환경변수 미설정 시에도 로컬 빌드/테스트가 깨지지 않도록 `sk-not-set`(명백히 가짜 값)을 기본값으로 둠. 실제 키는 배포 환경에서 `OPENAI_API_KEY` 환경변수로 주입해야 실제 호출이 동작한다.
- **공통 모델 설정**: `application.yml`의 `spring.ai.openai.chat.options.model: gpt-4o-mini` — 임시 기본값, 실제 채팅 기능 구현 시 재검토.
- **`ChatClient` 빈**: `kr.co.jobmoa.configuration.ChatClientConfig`가 오토컨피그된 `ChatClient.Builder`로 앱 전역 공용 `ChatClient` 빈을 만든다(`.build()`만 호출하는 최소 구성).
- **Function Calling(Tool)**: `org.springframework.ai.tool.annotation.Tool` + `ChatClient.tools(Object...)`/`toolContext(Map)` 사용(2.0.1 기준 API 실측). 도구는 `kr.co.jobmoa.aichat.tool.WorkoutRecommendationTool` 참고, 상세 설계는 [`06_domain_playbooks/ai-chat.md`](../06_domain_playbooks/ai-chat.md).

## 5. 기타

- **Lombok** (compileOnly + annotationProcessor)
- **commons-lang3**
- **classmate** 1.7.3 (springdoc 의존성 충돌 회피용으로 명시)
- **jacoco** 0.8.12 — 커버리지 (제외 대상: Application, dto, configuration, common/response, common/code, common/interceptor, mapper)

## 6. 빌드·배포

- Gradle (wrapper 포함, Windows에서도 빌드 가능 — A-RMS와 달리 `wget` 의존 없음)
- 배포: `.github/workflows/backend.yml` — `workflow_dispatch` 수동 트리거, self-hosted runner, `./gradlew build -x test`, systemd(`app.service`) 재시작. 상세는 [`13_deploy_runbook`](../13_deploy_runbook/jobmoa-server-deploy-runbook.md).

## 7. 설정 파일 구조

| 파일                     | 역할                                                                                                           |
| ------------------------ | -------------------------------------------------------------------------------------------------------------- |
| `application.yml`        | 공통 설정 (mybatis, pagehelper, jwt 만료시간, springdoc). `profiles.active: local`, `profiles.include: secret` |
| `application-local.yml`  | 로컬 프로파일 — DB 접속(Oracle wallet), 포트 9090, 파일 업로드 경로, 프론트 URL                                |
| `application-dev.yml`    | dev 프로파일                                                                                                   |
| `application-secret.yml` | **gitignore 대상.** OAuth2 클라이언트 시크릿 등 실 크리덴셜. 절대 이 문서 체계에 값을 옮겨 적지 않는다         |
