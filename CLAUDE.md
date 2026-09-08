# CLAUDE.md

## 프로젝트 개요

### 명령어
```bash
./gradlew build / bootRun / test
./gradlew test --tests "kr.co.jobmoa.SomeServiceTest"
```
포트: 9090, 프로파일: local

### 기술 스택
Spring Boot 4.0.3 / Java 17 / MyBatis 4.0.1 / Oracle / Spring Security 7 + JWT / Redis / PageHelper / springdoc-openapi 2.5.0

### 주요 규칙

**URL**: `/api/v1` 접두사는 `WebConfig`를 통해 자동 적용됨 — `@RequestMapping`에 절대 직접 쓰지 말 것.

**보안**: `public/**` = permitAll

**JWT**: Access(30분) = userId+role, Refresh(14일) = userId만
Redis: `refresh:{userId}`

**MyBatis**:
- 언더스코어 → 카멜케이스 자동 변환, null → VARCHAR
- `<`, `>`, `&`는 CDATA로 감쌀 것
- `@RequestParam`이 아니라 `@Param`(MyBatis) 사용
- Oracle 예약어: `"COMMENT"`, `"ROLE"` 등은 따옴표 처리
- null NUMBER 값: `#{param, jdbcType=NUMERIC}`

**Oracle 스키마**: 사용자가 별도로 스키마를 명시하지 않는 한, 모든 DB 관련 요청(DDL/DML/조회 등)은 무조건 `MOTIVE` 스키마 기준으로 처리한다.

**에러 처리**: `throw new CustomException(ErrorCode)` → `GlobalExceptionHandler` → `ApiResponse.error()`

**파일**: `C:/upload`에 업로드, 프로필 이미지는 `/upload/profile/yyyy/MM/dd/` 경로 하위에 저장

### 패키지 구조
```
kr.co.jobmoa
├── admin/{dashboard,log,mapper,user}
├── auth/
├── common/{code,exception,file,interceptor,response}
├── configuration/
└── code/, menu/
```

---
