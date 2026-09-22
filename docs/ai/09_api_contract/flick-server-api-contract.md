# API 계약 상세 (flick-server API Contract)

> ⚠️ 인증 관련(`/auth/**`) · 공통코드(`/codes`, `/admin/codes`) · 메뉴 · 파일 엔드포인트는 motive-server에서 재사용 가능한 인프라이고, flick-server(스트리밍 OTT 서비스, 2026-09-13 확정)의 실제 도메인 API(콘텐츠·구독·시청기록 등)는 아직 없다 — 구현되면 이 문서에 추가한다. motive-server의 운동 관련 API(`/exercise/**`·`/workout-records/**`·`/fitness-profile`·`/ai-chat/**`)는 2026-09-09 제거됐고, 그 뒤 시도한 채용정보 API(`/job-postings/**` 등)도 2026-09-13 제거됐다.

실측: 전체 컨트롤러의 `@GetMapping`/`@PostMapping`/`@PutMapping`/`@DeleteMapping` (2026-08-09 기준). 모든 경로 앞에 `/api/v1`이 자동으로 붙는다(`WebConfig`). 아래는 컨트롤러에 적힌 경로 그대로 표기.

## 공통 응답 형태

```json
{ "code": "0000", "message": "정상 처리되었습니다.", "data": { ... } }
```
`ApiResponse.ok(data)` / `ApiResponse.ok()`(data 없음) / `ApiResponse.error(code[, message])`. 에러 코드 목록은 [`common/code/*ErrorCode.java`](../08_domain_glossary/flick-server-glossary.md) 참고.

## 인증 (`/auth`, 실제 경로 `/api/v1/auth/...`)

| 메서드 | 경로 | 인증 | 설명 |
|--------|------|------|------|
| POST | `/auth/refresh` | refreshToken 쿠키 | Access/Refresh 재발급(웹) |
| POST | `/auth/refresh/mobile` | refreshToken 바디 | Access/Refresh 재발급(모바일, 바디로 주고받음) |
| POST | `/auth/exchange` | 1회용 `code`(바디) | 모바일 OAuth 딥링크 콜백에서 받은 code를 토큰으로 교환 |
| POST | `/auth/logout` | 필요 | Redis의 `refresh:{userId}` 삭제(액세스 토큰 자체는 만료 전까지 유효) |
| GET | `/auth/me` | 필요 | 내 프로필 조회 |

> OAuth2 로그인 자체는 Spring Security 표준 진입점 `/oauth2/authorization/{registrationId}`를 쓴다(별도 커스텀 컨트롤러 없음). 모바일 흐름 상세는 [`06_domain_playbooks/auth.md`](../06_domain_playbooks/auth.md) 참고.

## 공통코드 — 공개 (`/public/codes`, `/public/code`)

| 메서드 | 경로 | 인증 | 설명 |
|--------|------|------|------|
| GET | `/public/codes` | 불필요 | 다건 조회 → `Map<groupId, List<CodeResponseDto>>` |
| GET | `/public/code` | 불필요 | 단건(그룹) 조회 |

## 공통코드 — 관리자 (`/admin/codes`)

| 메서드 | 경로 | 인증 | 설명 |
|--------|------|------|------|
| GET | `/admin/codes` | 로그인만 | 공통코드 목록 |
| GET | `/admin/codes/{comCdId}/details` | 〃 | 상세코드 목록 |
| POST | `/admin/codes` | 〃 | 공통코드 등록 |
| PUT | `/admin/codes/{comCdId}` | 〃 | 공통코드 수정 |
| POST | `/admin/codes/{comCdId}/details` | 〃 | 상세코드 등록 |
| PUT | `/admin/codes/{comCdId}/details/{dtlCdId}` | 〃 | 상세코드 수정 |
| DELETE | `/admin/codes/{comCdId}` | 〃 | 공통코드 삭제(상세코드 cascade) |
| DELETE | `/admin/codes/{comCdId}/details/{dtlCdId}` | 〃 | 상세코드 삭제 |

## 메뉴 (`/public/menus`)

| 메서드 | 경로 | 인증 | 설명 |
|--------|------|------|------|
| GET | `/public/menus` | 불필요 | 전체 메뉴 조회 |

## 파일 (`/file`)

| 메서드 | 경로 | 인증 | 설명 |
|--------|------|------|------|
| POST | `/file/editor-image` | 컨트롤러상 인증 강제 없음(경로도 `/public/**` 아님 — [`12_known_issues`](../12_known_issues/flick-server-known-issues.md) 참고) | 에디터 이미지 업로드(`multipart/form-data`: `file`, `tempKey`) |

## 문서화

* Swagger UI: `/swagger-ui.html`, OpenAPI JSON: `/v3/api-docs` (둘 다 활성화 상태, `springdoc`)