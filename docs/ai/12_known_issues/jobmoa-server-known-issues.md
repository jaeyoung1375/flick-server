# 알려진 이슈·함정 상세 (jobmoa-server Known Issues)

실제 코드 실측 기반(2026-08-09). 확인되지 않은 추측은 "가정" 항목의 것 외에는 적지 않았다.

## 0. (가장 중요) 코드베이스 도메인이 프로젝트 목적과 다름

jobmoa-server는 채용정보 통합(잡 어그리게이터) 서비스로 기획되었으나(2026-09-08, [`01_project_overview/guide.md`](../01_project_overview/guide.md)), 현재 코드는 개인 운동 기록 서비스 `motive-server`를 그대로 복제한 것이다. 아래 1~11번 이슈를 포함해 `06_domain_playbooks`·`09_api_contract`·`10_data_model`·`08_domain_glossary`는 전부 이 레거시 운동 기록 도메인을 실측한 내용이며, 채용정보 도메인 코드는 아직 존재하지 않는다.
**영향:** 신규 기능(채용사이트 연동, 공고 검색 등)을 설계할 때 기존 `exercise`/`workout`/`fitness`/`recommend`/`aichat` 패키지를 도메인 참고용으로 오인하지 말 것 — 이들은 3계층 구조·에러 처리·MyBatis 패턴 등 **기술적 예시**로만 참고하고, 실제로는 채용정보 도메인 패키지로 교체/신설해야 한다.

## 1. 관리자(`/admin/**`) 엔드포인트에 role 기반 인가가 없음

`SecurityConfig`는 `/public/**`·`/api/v1/**`·`/swagger-ui/**`를 전부 permitAll로 열어두고, 그 외는 `authenticated()`만 요구한다. `/admin/**`도 `/api/v1/**`에 포함되므로 **permitAll**이다 — 즉 로그인만 하면(`USER` role이어도) 관리자 API(공통코드 CRUD, 운동마스터 CRUD)를 호출할 수 있다. `Role.ADMIN` enum은 존재하지만 `@PreAuthorize` 등으로 검증되는 곳이 코드베이스 어디에도 없다.
**영향:** 관리자 화면을 실제 사용자에게 노출하기 전 반드시 role 검증을 추가해야 한다. 그 전까지는 관리자 API를 신뢰 경계 안(내부망/미공개)에서만 호출한다고 가정할 것.

## 2. Access/Refresh 토큰에 타입 구분 claim이 없음

`JwtTokenUtil.createToken`은 access와 refresh 모두 같은 서명 키로 만들고, access에만 `role` claim을 추가로 붙일 뿐 **토큰 종류를 구분하는 claim(`type` 등)이 없다.** `JwtAuthenticationFilter`는 `Authorization: Bearer` 헤더의 토큰을 `getUserId()`로만 검증하므로, **refresh token을 access token 대신 헤더에 넣어도 인증이 통과한다.** refresh token은 만료(14일)가 access(30분)보다 길기 때문에, 탈취 시 예상보다 오래 일반 API 인증에 악용될 수 있다.
**대응 전) 참고 사항:** 새 인증 관련 작업 시 이 구조를 전제로 하고, 개선하려면 claim에 토큰 타입을 추가하고 필터에서 검증하는 방향을 검토한다(요청받지 않은 한 임의로 고치지 않는다 — 발견 사실만 보고).

## 3. `/file/editor-image` 업로드가 사실상 완전 공개

`FileController.uploadEditorImage`는 `SecurityUtil.getUserId()`를 호출하지 않는다. 경로도 `/public/**`가 아니지만 `/api/v1/**`가 permitAll이라 결과적으로 **인증 없이 누구나 파일을 업로드**할 수 있다(용량 제한만 `multipart.max-file-size: 10MB`로 존재). 악용(무제한 업로드) 방지 로직 없음.

## 4. `GlobalExceptionHandler`가 예외 메시지·클래스명을 그대로 응답에 노출

`handleException`/`handleMyBatisException`/`handleDataAccessException`이 `e.getMessage()`·`e.getClass().getSimpleName()`을 응답 `message`에 그대로 담는다. 개발 편의를 위한 설계로 보이나, 프로덕션에서는 내부 SQL·클래스 구조가 클라이언트에 노출될 수 있다. 신규 작업 시 이 동작을 전제로 하되, 운영 환경 노출 여부는 별도 확인이 필요하다(코드만으로는 프로파일별 분기가 없음을 확인함).

## 5. MyBatis NUMBER 컬럼 + null 파라미터 = 전역 기본값(VARCHAR)과 충돌 가능

`application.yml`의 `jdbc-type-for-null: VARCHAR`는 Oracle에서 `JdbcType.OTHER` 오류를 막기 위한 전역 기본값이지만, **NUMBER 컬럼에 null을 바인딩하는 파라미터는 개별적으로 `jdbcType=NUMERIC`을 명시해야 한다**(`FitnessProfile-mapper.xml`·`WorkoutRecord` 관련 매퍼 다수가 이 패턴을 이미 따름). 새 매퍼 작성 시 이걸 빠뜨리면 특정 null 값 입력에서만 재현되는 타입 오류가 날 수 있다.

## 6. Oracle Wallet 경로가 로컬 절대경로로 하드코딩

`application-local.yml`의 datasource URL이 `TNS_ADMIN=C:/wallet`을 직접 참조한다. 이 경로가 없는 환경(다른 개발 PC, CI)에서는 로컬 프로파일 실행이 즉시 실패한다. 새 개발 환경에서 로컬 실행이 안 될 때 가장 먼저 확인할 지점.

## 7. 카카오 OAuth2 PKCE 커스터마이징이 Spring Security 내부 API에 의존

`SecurityConfig.authorizationRequestResolver`가 `DefaultOAuth2AuthorizationRequestResolver`의 커스터마이저로 카카오 요청에서만 `code_challenge`/`code_challenge_method`를 제거한다. Spring Security 버전이 올라가면서 이 API가 바뀌면 조용히 깨질 수 있다(카카오만 로그인 실패). Spring Boot/Security 업그레이드 작업 시 반드시 카카오 로그인을 회귀 테스트할 것.

## 8. CORS 허용 오리진에 특정 IP가 하드코딩

`CorsConfig`가 `http://localhost:3000` 외에 `http://168.107.63.120:3000`을 명시적으로 허용한다. 배포 환경이 바뀌면 이 목록을 갱신해야 한다(환경변수화되어 있지 않음).

## 9. 테스트가 사실상 없음 / CI가 테스트를 건너뜀

[`06_test_strategy`](../06_test_strategy/jobmoa-server-test-strategy.md) 참고. `./gradlew build -x test`로 배포하므로 회귀는 수동 확인에 의존한다.

## 10. Spring AI `OpenAiChatModel`이 앱 기동 시점에 API 키를 즉시 요구함

Spring AI 2.x의 OpenAI 오토컨피그(`OpenAiChatAutoConfiguration` 등)는 `ChatModel` 빈을 애플리케이션 컨텍스트 기동 시 즉시(eager) 생성하며, 이때 `spring.ai.openai.api-key`가 비어 있으면 `IllegalStateException: At least one credential source must be specified`로 **컨텍스트 로딩 자체가 실패**한다(`@SpringBootTest`를 쓰는 테스트도 전부 같이 깨짐 — 실제로 `OPENAI_API_KEY` 미설정 상태에서 재현 확인함). 활성화된 모델 타입(`spring.ai.model.*`, 기본은 openai가 있으면 전부 활성화)마다 이 문제가 개별적으로 발생할 수 있다(예: audio speech 모델이 chat보다 먼저 걸려서 실패한 사례 있음).
**대응:** `application-secret.yml`에 `spring.ai.openai.api-key: ${OPENAI_API_KEY:sk-not-set}`처럼 **비어있지 않은 더미 기본값**을 둬서 로컬 빌드/테스트가 실키 없이도 통과하도록 함. 실제 API 호출은 여전히 실패하지만(401), 그건 실제 사용 시점의 문제이지 기동 문제는 아니다. 또한 `application.yml`의 `spring.ai.model.*`로 이 프로젝트가 실제 쓸 `chat`만 켜고 나머지(`embedding`/`image`/`moderation`/`audio.*`)는 `none`으로 꺼서 불필요한 빈 생성 자체를 줄여뒀다. 새 모델 타입을 켤 때는 이 패턴을 다시 확인할 것.

## 11. Spring AI 2.0.x 스타터가 Spring Boot 4.1 기준 의존성을 명시(POM 메타데이터)

`spring-ai-starter-model-openai:2.0.1`의 POM은 `spring-boot-starter-webclient`/`spring-boot-starter-restclient` 버전을 `4.1.1`로 **직접 명시**한다(jobmoa-server는 Boot `4.0.5`). Spring AI 메인테이너가 공식적으로 "2.0.x 스타터는 발행 시점 최신 지원 Boot 버전(4.1)으로 발행하며, 하위 Boot 버전에서도 런타임 호환은 유지한다"는 정책을 밝힘([spring-ai#6465](https://github.com/spring-projects/spring-ai/issues/6465)) — 문서 오기재가 아니라 의도된 정책이다.
**jobmoa-server에서 실제 영향 없음 확인:** `org.springframework.boot` Gradle 플러그인이 걸어두는 전역 버전 정렬 규칙 덕분에 `./gradlew dependencyInsight --configuration compileClasspath --dependency spring-boot-starter-webclient` 결과 `4.1.1 -> 4.0.5 (selected by rule)`로 정상 다운그레이드되고, `./gradlew build`도 통과함. 단, Maven이나 Gradle `platform()`만 쓰고 Spring Boot Gradle 플러그인을 안 쓰는 구성이면 같은 앱 안에서 Boot 모듈 버전이 섞일 수 있다고 이슈에서 재현됨 — 다른 프로젝트(예: 순수 Gradle `platform()` 구성)에 이식할 때는 반드시 재검증할 것. 향후 다른 Spring AI 스타터(임베딩·이미지 등) 추가 시 매번 `dependencyInsight`로 재확인 권장.