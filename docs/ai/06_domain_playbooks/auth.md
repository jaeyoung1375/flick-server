# 플레이북 · 인증 (Auth)

관련 패키지: `kr.co.flick.auth` (controller/service/mapper/util/filter/enums/dto)

## 1. 흐름

```
프론트 → GET /oauth2/authorization/{kakao|google|github} (Spring Security 표준 진입점)
      → 제공자 로그인 → SocialOauth2SuccessHandler
      → AuthService.socialLogin(provider, providerUserId, name, email)
          ├─ 이미 연동된 소셜 계정 → 기존 유저로 로그인
          ├─ 이메일은 있으나 다른 제공자로 최초 가입 → 소셜 계정만 추가 연동
          └─ 완전 신규 → USERS INSERT(Role.USER) + 소셜 계정 연동
      → JWT 발급(issueToken): Access(30분, role claim 포함) + Refresh(14일, Redis 저장)
```

* 카카오는 **PKCE 미지원** → `SecurityConfig.authorizationRequestResolver`에서 `code_challenge`/`code_challenge_method` 파라미터를 제거한다. 새 프로바이더 추가 시 PKCE 지원 여부를 먼저 확인할 것.
* `Provider` enum(`KAKAO`, `GOOGLE`, `GITHUB`)과 Spring Security의 `registrationId`(소문자)는 `Provider.fromRegistrationId()`로 변환한다 — 신규 프로바이더 추가 시 이 enum과 `application-secret.yml`의 OAuth2 클라이언트 등록을 함께 갱신한다.

## 2. 토큰 재발급 (`POST /auth/refresh`)

* `refreshToken` 쿠키(`httpOnly`, `sameSite=Lax`, 14일)를 읽어 `JwtTokenUtil.getUserId()`로 서명·만료만 검증한다.
* Redis에 저장된 `refresh:{userId}` 값과 쿠키 값이 **정확히 일치**해야 통과(`AuthService.refresh`) — 재사용 감지가 아니라 단순 일치 비교다. 불일치/미존재 시 `UserErrorCode.INVALID_REFRESH_TOKEN`.
* (2026-09-09 제거) 응답에 있던 `onboardingCompleted` 필드는 `FitnessProfileMapper.existsProfile(userId)`(운동 프로필 존재 여부)로 판정하던 값이었으나, fitness 도메인 제거와 함께 완전히 삭제했다. flick 고유 온보딩 개념이 필요해지면 새로 설계한다.

## 3. 모바일 전용 흐름

* 네이티브 앱은 `refreshToken`을 httpOnly 쿠키로 저장·전송할 수 없으므로 별도 경로를 쓴다.
* **소셜 로그인 진입:** 웹과 동일하게 `/oauth2/authorization/{provider}`로 가되, 세션에 모바일 여부를 마킹해서 `SocialOauth2SuccessHandler`가 처리 방식을 분기한다(`MobileAwareOAuth2AuthorizationRequestResolver.MOBILE_SESSION_ATTR`).
* **콜백 처리:** 모바일이면 `SocialOauth2SuccessHandler`가 `accessToken|refreshToken|isNew`를 Redis에 `oauth:exchange:{code}` 키로 60초 TTL 저장하고, 앱 딥링크(`app.mobile-redirect-scheme` + `?code=`)로 리다이렉트한다. 웹이면 기존처럼 `refreshToken` 쿠키 세팅 후 프론트 URL로 리다이렉트.
* **`POST /auth/exchange`** — 앱이 딥링크로 받은 1회용 `code`를 이 코드로 토큰과 교환한다(`AuthService.exchangeCode`). 조회 즉시 Redis 키를 삭제해 재사용을 막는다. 코드가 없거나 형식이 깨졌으면 `UserErrorCode.INVALID_EXCHANGE_CODE`.
* **`POST /auth/refresh/mobile`** — `/auth/refresh`와 동일 로직(`AuthService.refresh`)이나 `refreshToken`을 쿠키가 아니라 요청 바디로 받고, 응답 바디에도 `refreshToken`을 그대로 돌려준다(웹은 쿠키 갱신, 모바일은 시큐어 스토리지에 직접 보관).
* **`POST /auth/logout`** — 인증 필요. `SecurityUtil.getUserId()`로 식별한 사용자의 `refresh:{userId}` Redis 키만 삭제한다(액세스 토큰 자체를 무효화하지는 않음 — 만료 전까지는 여전히 유효).

## 4. 인증 사용자 식별 (`GET /auth/me` 및 전체 인증 필요 API)

* `JwtAuthenticationFilter`가 모든 요청에서 `Authorization: Bearer <token>` 헤더를 읽어 `userId`만 `SecurityContextHolder`에 `Authentication`의 principal(Long)로 저장한다 — **authorities는 항상 빈 리스트**(role 기반 인가 미구현, [`12_known_issues`](../12_known_issues/flick-server-known-issues.md) 참고).
* 컨트롤러/서비스에서 로그인 사용자 ID가 필요하면 `SecurityUtil.getUserId()`를 쓴다. 인증 안 된 상태로 호출하면 `UserErrorCode.UNAUTHORIZED`를 던진다.
* 토큰 검증 실패는 필터 안에서 `CustomException`을 잡고 그냥 다음 필터로 넘긴다(로그인 안 된 상태로 진행) — permitAll 경로(`/public/**`)는 이 상태로도 통과하고, 인증 필요 경로는 이후 `SecurityUtil.getUserId()`에서 걸린다.

## 5. 신규 작업 시 체크

* 새 인증 필요 API를 만들 때 `SecurityConfig.authorizeHttpRequests`의 `/public/**`·`/api/v1/**` permitAll 규칙과 충돌하지 않는지 확인 — 현재 `/api/v1/**` 전체가 permitAll이므로 **실제 인가는 URL 매처가 아니라 각 서비스의 `SecurityUtil.getUserId()` 호출 유무로 결정된다.** 인증이 필요한 신규 엔드포인트는 반드시 서비스/컨트롤러에서 `SecurityUtil.getUserId()`를 호출하도록 구현한다.