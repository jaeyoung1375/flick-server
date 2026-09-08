# 데이터 모델 상세 (jobmoa-server Data Model)

> ⚠️ **중요한 제약:** 이 저장소에는 `USERS`·`SOCIAL_ACCOUNTS`·`EXERCISES`·`CMM_CODE`·`CMM_CODE_DTL`·`MENU` 등 대부분 테이블의 **원본 DDL이 없다**(다른 곳에서 이미 생성된 스키마로 추정, `MOTIVE` 스키마). 아래 컬럼 정보는 **MyBatis Mapper XML의 SELECT/INSERT 절에서 실제로 관찰된 컬럼만** 정리한 것이며, 이 목록에 없는 컬럼이 실제 DB에 더 있을 수 있다. 스키마 전체가 필요하면 DB를 직접 조회해서 확인하고, 이 문서를 추측으로 채우지 않는다.
>
> `docs/ai/../ddl/workout_record_ddl.sql`만 이 저장소에 포함된 유일한 DDL이며, 그마저 하단에 "확인/조정이 필요한 가정"이 명시돼 있다(원본 주석 그대로 아래에 인용).

## 1. USERS (관찰된 컬럼 — `Auth-mapper.xml` 기준)

| 컬럼 | 비고 |
|------|------|
| `USER_ID` | PK, `USERS_SEQ`로 채번 |
| `EMAIL` | |
| `PASSWORD_HASH` | 소셜 전용 가입자는 NULL |
| `STATUS` | 관찰된 값 `'ACTIVE'` |
| `NAME` | |
| `PHONE` | |
| `"ROLE"` | Oracle 예약어라 쿼리에서 따옴표 처리 |
| `REG_DT` / `MOD_DT` | |
| `PROFILE_FILE_ID` | |
| `LAST_LOGIN_DT` | |
| `GENDER` | |
| `BIRTH` | `updateProfileInfo`에서 `TO_DATE(#{birth}, 'YYYYMMDD')`로 변환 — 파라미터는 문자열(YYYYMMDD), 컬럼은 DATE로 추정 |
| `NICKNAME` | |

## 2. SOCIAL_ACCOUNTS (`Auth-mapper.xml`)

| 컬럼 | 비고 |
|------|------|
| `SOCIAL_ID` | PK, `SOCIAL_SEQ`로 채번 |
| `USER_ID` | FK → USERS |
| `PROVIDER` | `KAKAO`/`GOOGLE`/`GITHUB` |
| `PROVIDER_USER_ID` | |
| `REG_DT` | |

## 3. USER_FITNESS_PROFILE (`FitnessProfile-mapper.xml`)

PK = `USER_ID`(1:1, USERS와). 컬럼: `EXPERIENCE_CD`, `LEVEL_CD`, `EQUIPMENT_CD`, `HEIGHT`, `WEIGHT`, `GOAL_WEIGHT`, `GYM_ID`, `SQUAT_YN`, `BENCH_PRESS_YN`, `REG_DT`, `MOD_DT`.

## 4. EXERCISES (`Exercise-mapper.xml` + `workout_record_ddl.sql`의 ALTER)

| 컬럼 | 비고 |
|------|------|
| `EXERCISE_ID` | PK |
| `NAME` | |
| `BODY_PART_CD` | 공통코드 `BODY_PART_CD` 그룹 참조 |
| `EQUIPMENT_CD` | 공통코드 `EXERCISE_EQUIPMENT_CD` 그룹 참조 |
| `IMAGE_FILE_ID` | |
| `REG_DT` / `MOD_DT` | **`workout_record_ddl.sql`에서 뒤늦게 ALTER TABLE로 추가**(운동기록 기능 개발 시점에 소급 추가) |

## 5. 공통코드 (`Code-mapper.xml`)

**CMM_CODE**: `COM_CD_ID`(PK), `COM_CD_NM`, `USE_YN`, `SORT_SEQ`
**CMM_CODE_DTL**: `COM_CD_ID`(FK)+`DTL_CD_ID`(복합 PK), `DTL_CD_NM`, `DTL_CD_EXPLN`, `LNKG_DTL_CD_ID1`, `LNKG_DTL_CD_NM1`, `LNKG_DTL_CD_ID2`, `LNKG_DTL_CD_NM2`, `SORT_SEQ`

## 6. 운동 기록 3단 (신규 기능 — 원본 DDL 있음: `src/main/resources/ddl/workout_record_ddl.sql`)

```
WORKOUT_RECORD (WORKOUT_RECORD_ID PK, USER_ID FK→USERS, CATEGORY_CD, RECORD_DT, DURATION_MIN, REG_DT, MOD_DT)
  └─ WORKOUT_RECORD_EXERCISE (WORKOUT_RECORD_EXERCISE_ID PK, WORKOUT_RECORD_ID FK ON DELETE CASCADE, EXERCISE_ID FK, SORT_NO)
       └─ WORKOUT_RECORD_SET (WORKOUT_RECORD_SET_ID PK, WORKOUT_RECORD_EXERCISE_ID FK ON DELETE CASCADE, SET_NO, WEIGHT NUMBER(6,2), REPS)
```

인덱스: `IX_WORKOUT_RECORD_USER(USER_ID, RECORD_DT)`, `IX_WRE_RECORD(WORKOUT_RECORD_ID)`, `IX_WRS_EXERCISE(WORKOUT_RECORD_EXERCISE_ID)`.

시퀀스: `WORKOUT_RECORD_SEQ`, `WORKOUT_RECORD_EXERCISE_SEQ`, `WORKOUT_RECORD_SET_SEQ` (각 1부터 NOCACHE).

### 원본 DDL에 명시된 "확인/조정이 필요한 가정" (그대로 인용 — 아직 미해소일 수 있음)

1. PK/시퀀스는 IDENTITY가 아니라 `<테이블명>_SEQ` 방식(기존 `USERS_SEQ`/`SOCIAL_SEQ`와 동일 패턴)이라고 가정.
2. `USERS` FK 컬럼은 `USER_ID`(반영 완료로 표시됨).
3. `EXERCISES` FK 컬럼은 `EXERCISE_ID`(반영 완료로 표시됨).
4. `CATEGORY_CD`/`BODY_PART_CD`/`EQUIPMENT_CD`가 지금은 단순 VARCHAR2인데, 공통코드 그룹으로 옮기는 게 나을 수 있음 — **미정, 옮기기 전 확인 필요**.
5. `DURATION_MIN`은 시/분을 합쳐 분 단위로 저장 — 분리 저장이 필요하면 컬럼 분리 가능(현재는 미분리).

## 7. 미확인 테이블

`MENU`(메뉴 조회용) 테이블 컬럼은 `Menu-mapper.xml`을 확인해 채울 것 — 이 문서 작성 시점엔 조회 SQL이 단순 목록 조회라 컬럼명이 `MenuDto` 필드에 의존적이었다. 새로 다룰 때 매퍼 XML을 먼저 확인한다.