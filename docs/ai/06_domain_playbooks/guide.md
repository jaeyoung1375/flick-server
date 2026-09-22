# 도메인 플레이북 (Domain Playbooks)

반복 작업하는 도메인의 데이터·엔드포인트 규칙을 정리합니다. 새 플레이북 추가 시 이 표에 한 줄 추가하세요.

> ⚠️ **인프라만 남은 상태:** 아래 플레이북(`auth`, `admin-code`)은 motive-server(운동 기록 서비스) 시절 인프라 도메인이다. 인증 메커니즘(JWT·소셜로그인)과 공통코드 체계는 서비스 도메인과 무관해 그대로 재사용 가능하다. 운동 도메인 플레이북(`workout.md`·`ai-chat.md`, 2026-09-09 삭제)과 그 뒤 시도했던 채용정보 도메인(`jobposting`, 2026-09-13 삭제)은 남아있지 않다. flick-server의 실제 목표 도메인(스트리밍 콘텐츠·구독 등)의 플레이북은 해당 기능이 구현된 뒤 새로 작성한다.

| 파일 | 도메인 | 컨트롤러 |
|------|--------|----------|
| [`auth.md`](./auth.md) | 인증·소셜 로그인·JWT | `AuthController` |
| [`admin-code.md`](./admin-code.md) | 공통코드 체계 + 관리자 CRUD | `CodeController`, `AdminCodeController` |

## 작성 규칙

* 도메인이 안정된 뒤(요구사항이 반복적으로 들어오는 시점) 작성한다 — 최초 구현 단계에서 미리 만들지 않는다.
* 실제 코드에서 관찰한 사실만 적는다. 계획·희망은 [`01_project_overview/guide.md`](../01_project_overview/guide.md)에.
* 용어는 [`../08_domain_glossary/flick-server-glossary.md`](../08_domain_glossary/flick-server-glossary.md)와 통일한다.