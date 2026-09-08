# API 계약 상세 (jobmoa-server API Contract)

실측: 전체 컨트롤러의 `@GetMapping`/`@PostMapping`/`@PutMapping`/`@DeleteMapping` (2026-08-09 기준). 모든 경로 앞에 `/api/v1`이 자동으로 붙는다(`WebConfig`). 아래는 컨트롤러에 적힌 경로 그대로 표기.

## 공통 응답 형태

```json
{ "code": "0000", "message": "정상 처리되었습니다.", "data": { ... } }
```
`ApiResponse.ok(data)` / `ApiResponse.ok()`(data 없음) / `ApiResponse.error(code[, message])`. 에러 코드 목록은 [`common/code/*ErrorCode.java`](../08_domain_glossary/jobmoa-server-glossary.md) 참고.

## 인증 (`/auth`, 실제 경로 `/api/v1/auth/...`)

| 메서드 | 경로 | 인증 | 설명 |
|--------|------|------|------|
| POST | `/auth/refresh` | refreshToken 쿠키 | Access/Refresh 재발급. 응답에 `onboardingCompleted` 포함 |
| GET | `/auth/me` | 필요 | 내 프로필 조회 |

> OAuth2 로그인 자체는 Spring Security 표준 진입점 `/oauth2/authorization/{registrationId}`를 쓴다(별도 커스텀 컨트롤러 없음).

## 운동 프로필 (`/fitness-profile`)

| 메서드 | 경로 | 인증 | 설명 |
|--------|------|------|------|
| GET | `/fitness-profile` | 필요 | 내 운동 프로필 조회 |
| POST | `/fitness-profile` | 필요 | 최초 등록 |
| PUT | `/fitness-profile` | 필요 | 부분 수정(COALESCE) |

## 운동 기록 (`/workout-records`)

| 메서드 | 경로 | 인증 | 설명 |
|--------|------|------|------|
| GET | `/workout-records` | 필요 | 내 운동기록 목록 (상세 미포함) |
| GET | `/workout-records/{workoutRecordId}` | 필요 | 상세(운동+세트 포함) |
| POST | `/workout-records` | 필요 | 등록 |
| PUT | `/workout-records/{workoutRecordId}` | 필요 | 수정 |
| DELETE | `/workout-records/{workoutRecordId}` | 필요 | 삭제 |

## 운동 마스터 — 공개 (`/exercises`)

| 메서드 | 경로 | 인증 | 설명 |
|--------|------|------|------|
| GET | `/exercises` | 불필요 | 목록 |
| GET | `/exercises/{exerciseId}` | 불필요 | 상세 |

## 운동 마스터 — 관리자 (`/admin/exercises`)

| 메서드 | 경로 | 인증 | 설명 |
|--------|------|------|------|
| GET | `/admin/exercises` | 로그인만(role 인가 없음) | 목록 (검색: `name`·`bodyPartCd`·`equipmentCd`, 페이징: `pageNum`(기본 1)·`pageSize`(기본 15)) → `data`는 `PageResponseDto`(`data`·`pageSize`·`pages`·`pageNum`·`total`·`startRow`) |
| GET | `/admin/exercises/{exerciseId}` | 〃 | 상세 |
| POST | `/admin/exercises` | 〃 | 등록 |
| PUT | `/admin/exercises/{exerciseId}` | 〃 | 수정 |
| DELETE | `/admin/exercises/{exerciseId}` | 〃 | 삭제 |

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

## AI 챗봇 (`/ai-chat`)

| 메서드 | 경로 | 인증 | 설명 |
|--------|------|------|------|
| POST | `/ai-chat/ask` | 필요 | 요청 `{ "question": string }` → 응답 `{ "answer": string }`. 개인화 질문("오늘 어디 운동하지?" 등)이면 서버가 LLM Function Calling으로 `getNextWorkoutPart` 도구를 호출해 실제 운동기록 기반으로 답한다. 일반 헬스 지식 질문은 도구 호출 없이 LLM 자체 지식으로 답한다(RAG 미구현, [`06_domain_playbooks/ai-chat.md`](../06_domain_playbooks/ai-chat.md) 참고). |

## 메뉴 (`/public/menus`)

| 메서드 | 경로 | 인증 | 설명 |
|--------|------|------|------|
| GET | `/public/menus` | 불필요 | 전체 메뉴 조회 |

## 파일 (`/file`)

| 메서드 | 경로 | 인증 | 설명 |
|--------|------|------|------|
| POST | `/file/editor-image` | 컨트롤러상 인증 강제 없음(경로도 `/public/**` 아님 — [`12_known_issues`](../12_known_issues/jobmoa-server-known-issues.md) 참고) | 에디터 이미지 업로드(`multipart/form-data`: `file`, `tempKey`) |

## 문서화

* Swagger UI: `/swagger-ui.html`, OpenAPI JSON: `/v3/api-docs` (둘 다 활성화 상태, `springdoc`)