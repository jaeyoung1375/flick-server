# 도메인 플레이북 (Domain Playbooks)

반복 작업하는 도메인의 데이터·엔드포인트 규칙을 정리합니다. 새 플레이북 추가 시 이 표에 한 줄 추가하세요.

| 파일 | 도메인 | 컨트롤러 |
|------|--------|----------|
| [`auth.md`](./auth.md) | 인증·소셜 로그인·JWT·온보딩 판정 | `AuthController` |
| [`workout.md`](./workout.md) | 운동 프로필 온보딩 + 운동 기록 CRUD | `FitnessProfileController`, `WorkoutRecordController`, `ExerciseController` |
| [`admin-code.md`](./admin-code.md) | 공통코드 체계 + 관리자 CRUD(공통코드·운동마스터) | `CodeController`, `AdminCodeController`, `AdminExerciseController` |

## 작성 규칙

* 도메인이 안정된 뒤(요구사항이 반복적으로 들어오는 시점) 작성한다 — 최초 구현 단계에서 미리 만들지 않는다.
* 실제 코드에서 관찰한 사실만 적는다. 계획·희망은 [`01_project_overview/guide.md`](../01_project_overview/guide.md)에.
* 용어는 [`../08_domain_glossary/jobmoa-server-glossary.md`](../08_domain_glossary/jobmoa-server-glossary.md)와 통일한다.