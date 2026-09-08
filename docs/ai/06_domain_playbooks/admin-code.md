# 플레이북 · 공통코드 체계 / 관리자 CRUD (Admin & Code)

관련 패키지: `kr.co.jobmoa.code`(공개 조회), `kr.co.jobmoa.admin.code`(관리자 CRUD), `kr.co.jobmoa.admin.exercise`(운동마스터 관리), `kr.co.jobmoa.menu`(메뉴)

## 1. 공통코드 2계층 구조

```
CMM_CODE (그룹, PK=COM_CD_ID)
  └─ CMM_CODE_DTL (상세, PK=COM_CD_ID+DTL_CD_ID, LNKG_DTL_CD_ID1/2로 다른 그룹 상세코드와 연결 가능)
```

* 예: `BODY_PART_CD`(부위), `EXERCISE_EQUIPMENT_CD`(장비) 그룹이 `EXERCISES` 테이블 조회 시 조인되어 이름을 채운다(`Exercise-mapper.xml`).
* 새 구분값·상태값이 필요하면 **하드코딩 대신 이 공통코드 그룹 등록을 우선 검토**한다(`08_domain_glossary`에도 등록).

## 2. 공개 조회 (`CodeController`, `/public/codes`, `/public/code`)

* `getCodeList` — `CMM_CODE`+`CMM_CODE_DTL` INNER JOIN, `USE_YN='Y'`만, `COM_CD_ID`로 필터 가능. 다건 조회는 그룹별로 묶인 `Map<String, List<CodeResponseDto>>` 형태로 응답(컨트롤러 시그니처 기준).
* 연결상세코드(`LNKG_DTL_CD_ID1/2`)도 함께 응답에 포함 — 다른 코드 그룹과의 매핑이 필요한 화면(예: 부위별 추천 장비)에서 활용.

## 3. 관리자 CRUD (`AdminCodeController`, `/admin/codes/**`)

* 공통코드: `GET /admin/codes`(목록), `POST`(등록), `PUT /{comCdId}`(수정), `DELETE /{comCdId}`(**상세코드까지 함께 삭제** — cascade 동작이므로 삭제 전 사용처 확인 필요).
* 상세코드: `GET /admin/codes/{comCdId}/details`, `POST .../details`, `PUT .../details/{dtlCdId}`, `DELETE .../details/{dtlCdId}`.
* 관리자 전용이지만 **`SecurityConfig`에 role 기반 인가가 없다** — `/admin/**`도 `/api/v1/**`에 포함되어 permitAll이며, 로그인 사용자면 누구나 호출 가능한 상태다. `Role.ADMIN` enum은 정의돼 있으나 인가 로직에서 아직 쓰이지 않는다([`12_known_issues`](../12_known_issues/jobmoa-server-known-issues.md) 참고 — 관리자 화면을 실사용하기 전 반드시 확인).

## 4. 운동마스터 관리 (`AdminExerciseController`, `/admin/exercises/**`)

* `GET`(목록/단건), `POST`(등록), `PUT /{exerciseId}`(수정), `DELETE /{exerciseId}`(삭제) — 표준 CRUD 5종.
* `EXERCISES` 테이블 FK를 `WORKOUT_RECORD_EXERCISE`가 참조한다 — 삭제 시 기존 운동 기록에 남아있는 FK 위반 가능성 확인(DDL은 `ON DELETE CASCADE` 미지정, [`10_data_model`](../10_data_model/jobmoa-server-data-model.md) 참고).

## 5. 메뉴 (`MenuController`, `/public/menus`)

* 전체 메뉴를 조회하는 단일 공개 엔드포인트. CRUD 없음(현재는 조회 전용, 등록/수정 API 미구현).