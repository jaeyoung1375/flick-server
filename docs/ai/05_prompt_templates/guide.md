# AI 전용 프롬프트 템플릿 (Prompt Templates)

개발자가 AI에게 백엔드 작업을 요청할 때 사용하는 표준 프롬프트 양식입니다. 이 형식을 복사해서 AI에게 제공하세요.

> 📌 **이 저장소는 백엔드(Spring Boot) 전용입니다.** 화면(Next.js) 코드는 `motive-ui` 저장소에 있으므로 여기서 프론트엔드 코드를 생성하지 않습니다. 모든 템플릿은 3계층(controller/service/mapper) + MyBatis를 전제로 합니다. 스택은 [`02_tech_stack/jobmoa-server-tech-stack.md`](../02_tech_stack/jobmoa-server-tech-stack.md) 참고.

---

## 템플릿 1: 도메인 로직 작업 요청 (가장 자주 쓰는 양식)

기존 도메인을 수정·확장할 때는 해당 도메인의 **플레이북**(`06_domain_playbooks/`)을 컨텍스트로 제공합니다.

> **[복사용 프롬프트]**
> 너는 이 저장소(jobmoa-server)의 백엔드 작업자야. 아래 문서 규칙을 지켜서 작업해 줘.
> * `docs/ai/06_domain_playbooks/<domain>.md` — 도메인 데이터·엔드포인트 규칙
> * `docs/ai/04_coding_standards/jobmoa-server-coding-standards.md` — 계층·MyBatis·응답 규칙
>
> * **대상 도메인/패키지:** [예: `kr/co/jobmoa/workout`]
> * **요구사항:** [추가/변경할 엔드포인트·필드]
> * **인증 필요 여부:** [로그인 필요 / public]
>
> **[준수사항]**
> 1. 3계층(controller/service/mapper)을 지키고, DB 접근은 MyBatis Mapper XML만 경유할 것(JPA 사용 금지).
> 2. 실패는 `CustomException(ErrorCode)`로 던지고 `GlobalExceptionHandler` 경로를 따를 것.
> 3. 용어는 `08_domain_glossary`·플레이북 정의와 통일할 것.
> 4. 요구 범위 밖 필드·엔드포인트를 추측으로 채우지 말 것(가정이 필요하면 명시).
> 5. Java 17 / Boot 4 문법만 사용할 것.

---

## 템플릿 2: 새 도메인(패키지) 신규 구현 요청

> **[복사용 프롬프트]**
> 너는 이 저장소(jobmoa-server)의 백엔드 작업자야. `docs/ai/`의 개요·기술 스택·코딩 규칙을 기반으로 아래 도메인을 새로 만들어 줘.
>
> * **도메인명/패키지:** [예: `kr/co/jobmoa/<domain>`]
> * **역할:** [한 줄 정의]
> * **엔드포인트:** [메서드·경로 — 예: `GET /api/v1/<domain>`]
> * **인증 필요 여부:** [로그인 필요 / public]
> * **테이블:** [기존 테이블 사용 / 신규 테이블 — 신규면 DDL도 함께]
>
> **[요구사항]**
> 1. `<domain>/controller`, `<domain>/service`, `<domain>/mapper`, `<domain>/dto` 4계층으로 생성해 줘.
> 2. Mapper XML은 `resources/mapper/<domain>/<Domain>-mapper.xml`에 두고, 시퀀스 채번은 `<테이블명>_SEQ` 패턴을 따라 줘.
> 3. 신규 도메인이면 `common/code/<Domain>ErrorCode.java`도 함께 만들어 줘.
> 4. 상태/용어는 `08_domain_glossary` 정의와 통일해 줘.
> 5. 작업 후 도메인 플레이북(`06_domain_playbooks`)·변경 이력(`11_changelog`)을 함께 갱신해 줘.

---

## 템플릿 3: 코드 리뷰 및 리팩토링 요청

> **[복사용 프롬프트]**
> 내가 작성한 아래 백엔드 코드를 리뷰하고 리팩토링해 줘.
> `docs/ai/04_coding_standards/jobmoa-server-coding-standards.md`의 코딩 규칙과 `docs/ai/07_review_checklist/jobmoa-server-review-checklist.md`의 체크리스트를 지켜야 해.
>
> **[대상]** [클래스/패키지 — 예: `WorkoutRecordServiceImpl`]
>
> **[기존 코드]**
> ```java
> [여기에 코드를 붙여넣으세요]
> ```
>
> **[리팩토링 방향]**
> * 계층 위반, MyBatis null 바인딩(NUMBER 컬럼 jdbcType 누락), 트랜잭션 경계, 예외 처리 경로, 인증 사용자 ID 취득 방식(`SecurityUtil`)을 점검하고 수정된 코드와 이유를 설명해 줘.
> * Java 17 / Boot 4 호환 문법만 사용해 줘.

---

## 템플릿 4: 인증/보안 관련 작업 요청

JWT·소셜 로그인·Redis 세션 등 인증 인프라 작업에 사용합니다.

> **[복사용 프롬프트]**
> 아래 작업을 해 줘. `docs/ai/06_domain_playbooks/auth.md`·`docs/ai/09_api_contract/jobmoa-server-api-contract.md`의 계약과 어긋나지 않아야 해.
>
> * **대상:** [예: `AuthService` / `JwtAuthenticationFilter` / 신규 소셜 프로바이더 추가]
> * **목적/변경 내용:** [추가·수정할 로직]
>
> **[준수사항]**
> 1. JWT 시크릿·OAuth2 클라이언트 시크릿을 코드·YAML에 평문으로 두지 말 것 (`application-secret.yml`만 사용).
> 2. Refresh 토큰의 Redis 저장 키 패턴(`refresh:{userId}`)을 유지할 것.
> 3. 알려진 함정(`12_known_issues`) — 카카오 PKCE 미지원, `JwtAuthenticationFilter`가 권한(authorities)을 채우지 않는 점 — 을 확인할 것.
> 4. 변경을 `11_changelog`에 기록할 것.

---

> 📌 작업 전 항상 `01_project_overview`(맥락)와 `02_tech_stack`(스택)을 먼저 확인하세요. 전체 하네스 사용법은 [`harness_engineering.md`](../harness_engineering.md) 참고.