# 플레이북 · 운동 프로필/운동 기록 (Workout)

관련 패키지: `kr.co.jobmoa.fitness`(프로필), `kr.co.jobmoa.workout`(기록), `kr.co.jobmoa.exercise`(운동 마스터 공개 조회)

## 1. 운동 프로필 (온보딩)

* `GET/POST/PUT /fitness-profile` — 전부 로그인 필요(`SecurityUtil.getUserId()`).
* `POST`는 최초 1회 등록 전제(테이블 `USER_FITNESS_PROFILE`, PK=`USER_ID`). 이미 존재하면 애플리케이션 레벨에서 `FitnessErrorCode.PROFILE_ALREADY_EXISTS` 판정이 필요 — 서비스 레이어에서 `existsProfile` 먼저 확인하는 패턴을 따른다.
* `PUT`은 부분 수정 — 요청 DTO에 없는 필드(null)는 `COALESCE`로 기존 값 유지(`FitnessProfile-mapper.xml`). 새 필드 추가 시 이 COALESCE 패턴을 따른다.
* `existsProfile`은 인증(`AuthController.refresh`)의 온보딩 완료 판정에도 재사용된다 — 이 매퍼를 다른 목적으로 바꿀 때 인증 플로우에 미치는 영향을 함께 확인한다.

## 2. 운동 기록 (헤더 → 상세 → 세트, 3단 구조)

```
WORKOUT_RECORD (1건 = 특정 날짜의 운동 세션)
  └─ WORKOUT_RECORD_EXERCISE (그 세션에 포함된 운동들, SORT_NO로 순서)
       └─ WORKOUT_RECORD_SET (운동별 세트: 중량 kg + 횟수)
```

* `GET /workout-records` — 목록은 헤더만(상세 미포함, 가벼운 조회).
* `GET /workout-records/{id}` — 상세는 운동·세트까지 포함한 전체 트리 반환.
* `POST/PUT /workout-records[/{id}]` — 등록/수정 모두 헤더+상세+세트를 한 번에 받는 것으로 설계됨(`WorkoutRecordInsertDto`/`UpdateDto`에 하위 리스트 포함). 부분 필드만 갱신하는 API가 아니다 — 상세 배열이 오면 전체 교체가 자연스러운 설계다(구현 시 기존 상세/세트 삭제 후 재삽입 여부를 서비스 코드에서 확인할 것).
* 소유권 검증: 모든 엔드포인트가 `SecurityUtil.getUserId()`로 얻은 유저와 `WORKOUT_RECORD.USER_ID`가 일치하는지 서비스 레이어에서 확인해야 한다 — 컨트롤러 시그니처만으로는 보장되지 않는다(다른 사용자의 `workoutRecordId`를 넣는 IDOR 가능성 — 신규/수정 작업 시 반드시 확인).
* `DURATION_MIN`은 프론트의 시/분 입력을 분 단위로 합산해 저장(원본 시/분 분리 값은 저장 안 함).
* `CATEGORY_CD`는 헬스/홈트/러닝/등산/야외 — 현재 단순 VARCHAR2, 공통코드(`CMM_CODE`) 편입 여부는 미정([`10_data_model`](../10_data_model/jobmoa-server-data-model.md) 가정 참고).

## 3. 운동 마스터 (공개 조회)

* `GET /exercises`, `GET /exercises/{id}` — 인증 불필요, `EXERCISES` 테이블 + `CMM_CODE_DTL` 조인(부위명·장비명 표시).
* 등록/수정/삭제는 관리자 전용 — [`admin-code.md`](./admin-code.md) 참고.