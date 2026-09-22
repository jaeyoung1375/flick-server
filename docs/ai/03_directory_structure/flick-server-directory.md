# 패키지 구조 상세 (flick-server Directory)

실측: `src/main/java/kr/co/flick/**` (75개 .java 파일, 2026-09-09 기준 — motive-server에서 이식된 workout/exercise/fitness/recommend/aichat 도메인 제거 후)

```
kr.co.flick
├── FlickServerApplication.java     # 진입점
├── admin/
│   └── code/{controller,service,mapper,dto}      # 관리자 공통코드 CRUD
├── auth/
│   ├── controller/AuthController.java              # /auth/refresh, /auth/me
│   ├── service/AuthService.java                    # 소셜 로그인·토큰 발급·재발급
│   ├── mapper/AuthMapper.java
│   ├── filter/JwtAuthenticationFilter.java
│   ├── util/JwtTokenUtil.java
│   ├── enums/{Role,Provider}.java
│   ├── dto/                                        # User, UserInsertDto, TokenResponseDto 등
│   └── SocialOauth2SuccessHandler.java
├── code/
│   └── {controller,service,mapper,dto}             # 공개 공통코드 조회
├── common/
│   ├── code/                                       # *ErrorCode enum (도메인별)
│   ├── constant/{EncodingEnum,Env}
│   ├── exception/{CustomException,GlobalExceptionHandler}
│   ├── file/{controller,service,mapper,dto}        # 파일 업로드 (에디터 이미지)
│   ├── response/ApiResponse.java                   # 표준 응답 래퍼
│   └── util/                                       # SecurityUtil, DateUtil, HashUtil, WebUtil,
│                                                     #   ClientIpProvider, ViewerKeyProvider,
│                                                     #   PageResponseDto, StringListTypeHandler
├── configuration/
│   ├── SecurityConfig.java                         # 필터체인·OAuth2·인가 규칙
│   ├── WebConfig.java                              # /api/v1 프리픽스, 업로드 리소스 핸들러
│   ├── CorsConfig.java
│   ├── MybatisConfig.java
│   ├── PasswordConfig.java
│   ├── SwaggerConfig.java
│   └── P6Spy*.java                                 # SQL 로깅 설정 3종
└── menu/
    └── {controller,service,mapper,dto}             # 메뉴 공개 조회
```

## 계층 규약

각 도메인은 `controller` → `service` → `mapper`(MyBatis 인터페이스, XML은 `resources/mapper/<domain>/`) 3계층 + `dto`로 구성된다. **JPA Entity·Repository 계층은 없다** — DB 접근은 전량 MyBatis Mapper(XML)를 경유한다.

`admin/` 하위는 일반 도메인과 같은 패키지를 관리자 전용으로 한 벌 더 두는 구조다(`code`↔`admin/code`). 공개용은 조회만, 관리자용은 CRUD 전체를 제공한다.

## 리소스 매핑

```
src/main/resources/
├── mapper/<domain>/<Domain>-mapper.xml   # MyBatis SQL — 패키지의 mapper/ 인터페이스와 1:1
├── ddl/                                   # 기능 추가 시 작성하는 DDL (전체 스키마 DDL 아님 — 10_data_model 참고)
├── application*.yml
└── logback-spring.xml
```