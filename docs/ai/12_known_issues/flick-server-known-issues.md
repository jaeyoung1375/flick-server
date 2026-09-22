# 알려진 이슈·함정 상세 (flick-server Known Issues)

실제 코드 실측 기반(2026-08-09). 확인되지 않은 추측은 "가정" 항목의 것 외에는 적지 않았다.

## 0. (가장 중요) 코드베이스에 목표 도메인이 아직 없음

flick-server는 2026-09-08 채용정보 통합(잡 어그리게이터) 서비스로 기획되어 개인 운동 기록 서비스 `motive-server`를 복제해서 시작했다. 운동 도메인 패키지(`exercise`/`workout`/`fitness`/`recommend`/`aichat`, 관련 mapper·DDL·`common/code/{Workout,Fitness,Exercise}ErrorCode`·Spring AI 연동 포함)는 2026-09-09 전부 제거했고, 실제로 구현했던 채용정보 도메인(`jobposting` — 잡코리아 크롤러·공고 조회, 관련 mapper·DDL·DB 데이터(`JOB_POSTING` 테이블, `JOB_CAREER_CD`/`JOB_LOCATION_CD` 공통코드) 포함)도 2026-09-13 서비스 정체성을 **스트리밍(OTT) 백엔드**로 최종 확정하면서 코드+DB까지 전부 제거했다. 남은 것은 인증·공통코드·메뉴·파일 업로드 등 재사용 가능한 인프라뿐이며, 스트리밍 도메인(콘텐츠·구독·시청기록 등) 코드는 아직 존재하지 않는다.
**영향:** 신규 기능(콘텐츠 카탈로그, 구독/결제, 시청기록 등)을 설계할 때 이 인프라(3계층 구조·에러 처리·MyBatis 패턴 등)는 기술적 예시로 참고하되, 스트리밍 도메인 패키지는 새로 만들어야 한다.

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

`application.yml`의 `jdbc-type-for-null: VARCHAR`는 Oracle에서 `JdbcType.OTHER` 오류를 막기 위한 전역 기본값이지만, **NUMBER 컬럼에 null을 바인딩하는 파라미터는 개별적으로 `jdbcType=NUMERIC`을 명시해야 한다**. 새 매퍼 작성 시 이걸 빠뜨리면 특정 null 값 입력에서만 재현되는 타입 오류가 날 수 있다. (⚠️ 이 패턴의 실측 예시였던 `FitnessProfile-mapper.xml`·`WorkoutRecord` 계열 매퍼는 운동 도메인 제거로 삭제됨 — 현재 코드베이스에 실제 예시 없음, 규칙만 구두 전승.)

## 6. Oracle Wallet 경로가 로컬 절대경로로 하드코딩

`application-local.yml`의 datasource URL이 `TNS_ADMIN=C:/wallet`을 직접 참조한다. 이 경로가 없는 환경(다른 개발 PC, CI)에서는 로컬 프로파일 실행이 즉시 실패한다. 새 개발 환경에서 로컬 실행이 안 될 때 가장 먼저 확인할 지점.

## 7. 카카오 OAuth2 PKCE 커스터마이징이 Spring Security 내부 API에 의존

`SecurityConfig.authorizationRequestResolver`가 `DefaultOAuth2AuthorizationRequestResolver`의 커스터마이저로 카카오 요청에서만 `code_challenge`/`code_challenge_method`를 제거한다. Spring Security 버전이 올라가면서 이 API가 바뀌면 조용히 깨질 수 있다(카카오만 로그인 실패). Spring Boot/Security 업그레이드 작업 시 반드시 카카오 로그인을 회귀 테스트할 것.

## 8. `CorsConfig`가 `app.frontend-url`과 별개로 오리진을 하드코딩

`CorsConfig.corsConfigurationSource()`는 `http://localhost:3000` 하나만 허용 오리진으로 하드코딩한다. 반면 `SocialOauth2SuccessHandler`가 쓰는 `app.frontend-url`은 프로파일별로 이미 externalize돼 있다(`application-local.yml`은 `http://localhost:3000`, `application-dev.yml`은 `http://168.107.63.120:3000`). 즉 **`dev` 프로파일로 띄우면 OAuth2 리다이렉트는 `168.107.63.120:3000`으로 가지만 CORS는 그 오리진을 허용하지 않아 프론트에서 API 호출이 막힌다.**
**대응 전) 참고 사항:** CORS 오리진도 `app.frontend-url`(또는 별도 프로퍼티)로 externalize하는 방향을 검토할 것(요청받지 않은 한 임의로 고치지 않는다 — 발견 사실만 보고).

## 9. 테스트가 사실상 없음 / CI가 테스트를 건너뜀

[`06_test_strategy`](../06_test_strategy/flick-server-test-strategy.md) 참고. `./gradlew build -x test`로 배포하므로 회귀는 수동 확인에 의존한다.

## 10. `AuthMapper`에 호출부 없는 메서드가 남아있음

`updateProfileInfo`(닉네임/성별/생년월일 갱신)·`updateProfileFileId`(프로필 파일아이디 갱신)는 `Auth-mapper.xml`과 `AuthMapper` 인터페이스에 둘 다 정의돼 있지만, 원래 이걸 호출하던 fitness(운동 프로필) 도메인이 2026-09-09 제거되면서 **현재 코드베이스 어디서도 호출되지 않는다**(`grep` 실측 확인, 2026-09-13). 컴파일 에러는 아니지만 죽은 코드다.
**영향:** 신규 "내 정보 수정" 류 기능을 만들 때 이 메서드를 그대로 재사용할 수 있는지 검토 후 사용하거나(쿼리 자체는 유효), 정리가 필요하면 별도로 요청할 것 — 이번 문서 갱신 범위에서는 삭제하지 않았다.

