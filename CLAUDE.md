# CLAUDE.md

## Commands
```bash
./gradlew build / bootRun / test
./gradlew test --tests "kr.co.teamo.SomeServiceTest"
```
Port: 9090, profile: local

## Stack
Spring Boot 4.0.3 / Java 17 / MyBatis 4.0.1 / Oracle / Spring Security 7 + JWT / Redis / PageHelper / springdoc-openapi 2.5.0

## Key Rules

**URL**: `/api/v1` prefix auto-applied via `WebConfig` — never write it in `@RequestMapping`.

**Security**: `public/**` = permitAll, `admin/**` = ROLE_ADMIN, `api/**` = authenticated

**JWT**: Access(30m) = userId+role, Refresh(14d) = userId only
Redis: `blacklist:{token}`, `refresh:{userId}`, `force-logout:{userId}` (checked by userId in filter)

**MyBatis**:
- underscore→camelCase, null→VARCHAR
- CDATA wrap for `<`, `>`, `&`
- `@Param` (MyBatis) not `@RequestParam`
- Oracle reserved words: `"COMMENT"`, `"ROLE"` etc.
- Null NUMBER: `#{param, jdbcType=NUMERIC}`

**Oracle**: Table = `USERS`. Duplicate rows from `SOCIAL_ACCOUNTS` join → use `LISTAGG()`.

**Error**: `throw new CustomException(ErrorCode)` → `GlobalExceptionHandler` → `ApiResponse.error()`

**Admin**: New tab = subpackage under `admin/`. Shared mapper = `admin/mapper/AdminMapper.java`.

**Files**: Upload to `C:/upload`, profile images under `/upload/profile/yyyy/MM/dd/`

## Package
```
kr.co.teamo
├── admin/{dashboard,log,mapper,user}
├── auth/
├── common/{code,exception,file,interceptor,response}
├── configuration/
├── notification/
└── post/, comment/, apply/, code/, menu/
```
