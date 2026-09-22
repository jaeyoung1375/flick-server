# 코딩 규칙 상세 (flick-server Coding Standards)

실측 코드 패턴 기반 (2026-08-09). 기존 코드에 없는 규칙을 새로 강제하지 않는다 — 여기 적힌 것은 이미 저장소에 존재하는 관례다.

## 1. 계층 분리

* `controller` → `service` → `mapper` 3계층. 컨트롤러의 직접 DB 접근 금지.
* 컨트롤러는 `SecurityUtil.getUserId()`로 인증 사용자 ID를 얻어 서비스에 넘긴다 — 서비스가 `SecurityContextHolder`를 직접 참조하지 않는다.
* `@RequiredArgsConstructor` + `private final` 생성자 주입. 필드 `@Autowired` 없음(실측 전 컨트롤러/서비스 동일 패턴).

## 2. URL·매핑

* `@RestController` 클래스에 `@RequestMapping` prefix를 직접 쓰지 않는다 — `/api/v1`은 `WebConfig`가 자동 부여한다. 예외: `AuthController`는 `@RequestMapping("/auth")`로 하위 경로만 지정(전체 prefix는 여전히 WebConfig가 앞에 붙임 → 최종 `/api/v1/auth/...`).
* 관리자 전용 엔드포인트는 `/admin/...`, 공개(비로그인) 엔드포인트는 `/public/...` 경로 세그먼트를 쓴다(`/admin/codes`, `/public/codes` 등). 별도 인가 로직은 없고 관례상 구분이다 — [`12_known_issues`](../12_known_issues/flick-server-known-issues.md) 참고.
* REST 동사: 조회 GET · 생성 POST · 수정 PUT · 삭제 DELETE. `*.do` 접미사 없음(A-RMS와 다른 점).

## 3. 응답·에러

* 모든 컨트롤러 메서드는 `ApiResponse<T>`를 반환한다. 성공은 `ApiResponse.ok(data)`/`ApiResponse.ok()`.
* 실패는 절대 컨트롤러에서 직접 `ResponseEntity.badRequest()` 등을 만들지 않는다 — `throw new CustomException(XxxErrorCode.YYY)`를 던지고 `GlobalExceptionHandler`가 변환한다.
* 도메인별 에러코드는 `common/code/<Domain>ErrorCode.java`에 `ResponseCode` 구현 enum으로 둔다(코드 접두어: 공통 없음 `CommonErrorCode`, 사용자 `UserErrorCode`, 파일 `FileErrorCode`, 공통코드 `CodeErrorCode`(`C0xxx`)). 새 도메인 추가 시 이 패턴을 따라 새 enum을 만든다.

## 4. MyBatis

* `map-underscore-to-camel-case: true` — DB 컬럼 `SNAKE_CASE` ↔ Java `camelCase` 자동 매핑, resultType/property에 별도 매핑 불필요.
* `null` 파라미터는 기본적으로 `jdbcType=VARCHAR`(전역 설정)지만 **NUMBER 컬럼에 null을 바인딩할 때는 반드시 `#{param, jdbcType=NUMERIC}`을 명시**한다(전역 기본이 VARCHAR라 숫자 컬럼에서 타입 불일치 오류 발생 가능).
* SQL에 `<`, `>`, `&` 등 XML 특수문자가 필요하면 CDATA로 감싼다.
* Oracle 예약어와 겹치는 컬럼(`"ROLE"` 등)은 큰따옴표로 감싼다(`Auth-mapper.xml` 실측).
* 컨트롤러 파라미터는 `@RequestParam`이 아니라 서비스단에서 MyBatis `@Param`을 쓴다(멀티 파라미터 매퍼).
* Mapper XML 파일명 규칙: `<Domain>-mapper.xml`, 경로 `resources/mapper/<domain 소문자>/`.
* 부분 수정(PUT)은 `COALESCE(#{dto.field}, EXISTING_COLUMN)` 패턴으로 "요청에 없는 필드는 기존 값 유지"를 구현한다 — 새 부분수정 API도 이 패턴을 따른다. (⚠️ 이 패턴의 실측 근거였던 `FitnessProfile-mapper.xml`은 fitness 도메인 제거로 삭제됨 — 현재 코드베이스에 실제 예시 없음, 규칙만 구두 전승.)

## 5. 시퀀스 채번

* Oracle IDENTITY가 아니라 `<테이블명>_SEQ` 시퀀스 + `<selectKey keyProperty="..." resultType="long" order="BEFORE">SELECT ..._SEQ.NEXTVAL FROM DUAL</selectKey>` 패턴으로 채번한다(`USERS_SEQ`, `SOCIAL_SEQ` 등). 새 테이블도 이 패턴을 따른다.

## 6. 로깅

* `@Slf4j` + `log.info/debug/warn/error`. `System.out.println` 금지.
* 예외 로깅은 `GlobalExceptionHandler`에서 종류별로 레벨을 구분한다: `CustomException`은 `warn`, `MyBatisSystemException`/`DataAccessException`/기타 `Exception`은 `error`.

## 7. Validation

* 요청 DTO에 `@Valid` + Bean Validation 애노테이션. 검증 실패 시 `MethodArgumentNotValidException` → `GlobalExceptionHandler`가 첫 번째 필드 에러 메시지를 400으로 변환(`CommonErrorCode.INVALID_INPUT`).

## 8. Swagger/OpenAPI

* 컨트롤러 클래스에 `@Tag(name=..., description=...)`, 메서드에 `@Operation(summary=..., description=...)`을 단다(실측: 대부분 컨트롤러 적용, `CodeController`/`AuthController`는 클래스 `@Tag` 누락 — 신규 컨트롤러는 반드시 붙인다).

## 9. 네이밍

* 클래스 PascalCase, 메서드/변수 camelCase, DB 컬럼 `UPPER_SNAKE_CASE`(Oracle 관례), 패키지 소문자.
* DTO 접미사: 요청은 `*InsertDto`/`*UpdateDto`/`*RequestDto`, 응답은 `*ResponseDto`.

## 10. 시크릿

* `@Value`로 주입하고 실제 값은 `application-secret.yml`(gitignore 대상)에 둔다. 코드·다른 yml에 평문 크리덴셜을 넣지 않는다.