# 하네스 엔지니어링 (AI Harness Engineering)

이 문서는 `flick-server` 저장소(넷플릭스·티빙류의 스트리밍(OTT) 서비스 백엔드, 2026-09-13 정체성 확정)에서 **AI를 활용해 개발할 때 따르는 하네스(harness) 시스템**의 안내 허브입니다. AI에게 작업을 시키기 전에, 그리고 새 문서를 추가할 때 이 문서부터 확인하세요.

> ⚠️ **현재 코드베이스는 인프라 골격뿐:** 이 저장소는 개인 운동 기록 서비스 `motive-server`를 복제한 초기 스캐폴딩이다. 운동 도메인은 2026-09-09, 그 뒤 시도했던 채용정보(잡 어그리게이터) 도메인(`jobposting`)은 2026-09-13 코드+DB까지 전부 제거했다. `06_domain_playbooks`·`09_api_contract`·`10_data_model`·`08_domain_glossary`는 남아있는 인프라(인증·공통코드·메뉴·파일업로드)만 다루며, **스트리밍 도메인은 아직 설계·구현 전**이다. 프로젝트의 실제 목적·경위는 [`01_project_overview/guide.md`](./01_project_overview/guide.md)·[`11_changelog`](./11_changelog/flick-server-changelog.md) 참고.

> 📌 **이 문서의 역할:** 개별 사실(스택·도메인·API 등)은 `docs/ai/01~13` 각 문서가 단일 출처(Single Source of Truth)입니다. 이 문서는 그 문서들을 **어떤 순서로·어떤 상황에 참조하는지**를 안내하는 지도(map)이며, 사실을 중복 기재하지 않습니다. 사실이 바뀌면 해당 번호 문서를 고치고, 이 문서는 구조가 바뀔 때만 갱신합니다.

---

## 1. 하네스란 무엇인가

하네스는 AI가 이 저장소를 **매번 처음 보는 상태에서도 일관되게 작업하도록** 잡아주는 문서 체계입니다.

* **컨텍스트 주입:** AI는 세션마다 저장소 맥락을 모릅니다. `docs/ai/`의 문서를 컨텍스트로 제공해 프로젝트 사실·규칙·관례를 주입합니다.
* **단일 출처:** 같은 사실을 여러 곳에 적으면 반드시 어긋납니다. 각 주제는 정해진 한 문서에만 적고, 나머지는 그 문서를 링크로 가리킵니다.
* **검증된 패턴 축적:** 한 번 겪은 실수·해결책은 `12_known_issues`에 남겨 재발을 막습니다.
* **도메인 단위 플레이북:** 반복 작업하는 도메인(인증, 운동기록 등)은 데이터·엔드포인트 규칙을 `06_domain_playbooks`에 적어 일관성을 유지합니다.

---

## 2. 문서 디렉토리 지도 (docs/ai/01~13)

각 디렉토리는 `guide.md`(역할·작성 규칙)와 `flick-server-*.md`(실제 내용) 또는 도메인별 문서로 구성됩니다.

| # | 디렉토리 | 담는 내용 | 단일 출처 파일 |
|---|----------|-----------|----------------|
| 01 | `01_project_overview` | 프로젝트 정체성·목적 | `flick-server-overview.md` |
| 02 | `02_tech_stack` | 기술 스택·라이브러리 버전 | `flick-server-tech-stack.md` |
| 03 | `03_directory_structure` | 패키지 구조 (도메인 3계층) | `flick-server-directory.md` |
| 04 | `04_coding_standards` | 네이밍·계층·응답 규칙 등 코딩 표준 | `flick-server-coding-standards.md` |
| 05 | `05_prompt_templates` | AI 작업 요청용 표준 프롬프트 양식 | `guide.md` |
| 06 | `06_domain_playbooks` | 도메인별 데이터·엔드포인트 규칙 (auth, admin-code) | 도메인별 `*.md` |
| 06 | `06_test_strategy` | 테스트 전략 (현재 공백 상태 포함) | `flick-server-test-strategy.md` |
| 07 | `07_review_checklist` | 코드 리뷰 체크리스트 | `flick-server-review-checklist.md` |
| 08 | `08_domain_glossary` | 도메인 용어 사전 | `flick-server-glossary.md` |
| 09 | `09_api_contract` | API 계약 (컨트롤러 경로·요청/응답) | `flick-server-api-contract.md` |
| 10 | `10_data_model` | 데이터 모델 (USERS·SOCIAL_ACCOUNTS·공통코드 등) | `flick-server-data-model.md` |
| 11 | `11_changelog` | 변경 이력 | `flick-server-changelog.md` |
| 12 | `12_known_issues` | 알려진 이슈·함정·해결책 | `flick-server-known-issues.md` |
| 13 | `13_deploy_runbook` | 빌드·배포 런북 | `flick-server-deploy-runbook.md` |

> ⚠️ `06`이 두 개(`06_domain_playbooks`, `06_test_strategy`)입니다. 번호가 중복되어 있으니 디렉토리명으로 구분하세요. (A-RMS 저장소의 하네스 관례를 그대로 따름 — `Java-Service-Tree-Framework` 루트 `.claude/agents/backend-expert.md` 참조.)

---

## 3. 작업 상황별 참조 순서

* **항상 먼저:** `01_project_overview`(맥락) → `02_tech_stack`(스택 확인)
* **도메인 로직 작업:** 해당 `06_domain_playbooks/<domain>.md` + `03_directory_structure`(계층 위치)
* **새 용어·상태가 나오면:** `08_domain_glossary` 확인 후 통일
* **API 작업:** `09_api_contract` + `10_data_model`
* **코드 리뷰:** `04_coding_standards` + `07_review_checklist`
* **막히거나 이상하면:** `12_known_issues`에 같은 함정이 기록돼 있는지 먼저 확인
* **작업 후:** 변경은 `11_changelog`, 새로 발견한 함정은 `12_known_issues`에 기록

작업 요청 프롬프트 양식은 [`05_prompt_templates/guide.md`](./05_prompt_templates/guide.md)를 따릅니다.

---

## 4. 저장소 성격 (작업 전 반드시 확인)

| 항목 | 내용 |
|------|------|
| 역할(목표) | 스트리밍(OTT) 서비스의 백엔드 API 서버 (2026-09-13 확정) |
| 역할(현재 코드) | 콘텐츠·구독·시청기록 등 스트리밍 도메인은 미구현. 남은 건 motive-server에서 재사용하는 인프라뿐 — 소셜 로그인(JWT)·공통코드 관리·메뉴·파일업로드 ([`01_project_overview/guide.md`](./01_project_overview/guide.md) 참고) |
| 스택 | Spring Boot 4.0.5 · Java 17 · MyBatis 4.0.1 · Oracle |
| 인증 | Spring Security 7 + JWT(HS256) + Redis(refresh token 저장) + 소셜 로그인(카카오/구글/깃허브 OAuth2) |
| 프론트엔드 | 아직 미정. CORS 설정에 남은 `http://localhost:3000` 등은 motive-server 시절 잔재 |

상세는 [`02_tech_stack/flick-server-tech-stack.md`](./02_tech_stack/flick-server-tech-stack.md)가 단일 출처이며, 위는 요약만 적었습니다.

---

## 5. 도메인 작업의 핵심 관례

* **3계층 분리:** `controller` → `service` → `mapper`(MyBatis) + `dto`. 트리형 상속 구조 없음(단순 3계층, TreeFramework 미사용 — A-RMS와 다른 점).
* **URL 프리픽스 자동화:** `/api/v1`은 `WebConfig`가 `@RestController` 전체에 자동 부여 — 컨트롤러에 직접 쓰지 않는다.
* **인증 경계:** `SecurityConfig`가 `/public/**`·`/api/v1/**`·`/swagger-ui/**`를 permitAll로 열어두고, 실제 인가는 `JwtAuthenticationFilter` + 컨트롤러 내부의 `SecurityUtil.getUserId()` 호출로 처리한다(Redmine형 role 기반 인가 미구현 — [`12_known_issues`](./12_known_issues/flick-server-known-issues.md) 참고).
* **에러 응답:** `throw new CustomException(ErrorCode)` → `GlobalExceptionHandler` → `ApiResponse.error()`. 항상 이 경로를 따른다.
* **공통코드:** `CMM_CODE`/`CMM_CODE_DTL` 테이블 기반. 신규 상태값·구분값은 하드코딩 대신 공통코드로 등록을 우선 검토한다.

---

## 6. 문서 작성·갱신 원칙

1. **단일 출처 유지:** 같은 사실을 두 문서에 쓰지 않습니다. 다른 곳에서 필요하면 링크로 가리킵니다.
2. **사실이 바뀌면 번호 문서를 고친다:** 이 허브 문서는 디렉토리 구조나 작업 흐름이 바뀔 때만 갱신합니다.
3. **변경은 changelog, 함정은 known_issues:** 작업 후 `11_changelog`에 변경을 남기고, 새로 발견한 실수·해결책은 `12_known_issues`에 기록합니다.
4. **플레이북은 도메인이 안정되면 작성:** 반복 작업하는 도메인은 `06_domain_playbooks`에 정리해 다음 작업의 컨텍스트로 씁니다.
5. **시크릿 금지:** `application-secret.yml`은 gitignore 대상이며 이 문서 체계 어디에도 실제 크리덴셜 값을 옮겨 적지 않습니다.

---

**문서 성격:** 하네스 시스템 안내 허브 (사실은 01~13 문서가 단일 출처)
**프로젝트:** flick-server — 스트리밍(OTT) 서비스 백엔드 (목표. 현재 코드는 motive-server 유래 인프라 골격뿐)
**저장소:** flick-server (원격 URL은 확인 필요 — 로컬에 remote 미설정)