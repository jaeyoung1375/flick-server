# 데이터 모델 상세 (flick-server Data Model)

> ⚠️ `USERS`·`SOCIAL_ACCOUNTS`·공통코드 테이블은 motive-server에서 이식된 인증/공통 인프라이고, flick-server(스트리밍 OTT 서비스, 2026-09-13 확정)의 실제 도메인 테이블(콘텐츠·구독·시청기록 등)은 아직 없다 — 구현되면 이 문서에 추가한다. motive-server의 운동 관련 테이블(`USER_FITNESS_PROFILE`·`EXERCISES`·`WORKOUT_RECORD*`)을 쓰던 코드는 2026-09-09 제거됐다 — 이 문서에서도 함께 제거했다(실제 DB 테이블 자체는 건드리지 않음, `MOTIVE` 스키마에 남아있을 수 있음). 그 뒤 시도한 채용정보 도메인의 `JOB_POSTING` 테이블과 공통코드 그룹(`JOB_CAREER_CD`·`JOB_LOCATION_CD`)은 2026-09-13 **테이블·데이터까지 실제로 삭제**했다(위 운동 테이블과 달리 DB에도 남아있지 않음) — 상세는 [`11_changelog`](../11_changelog/flick-server-changelog.md) 참고.
>
> ⚠️ **중요한 제약:** 이 저장소에는 `USERS`·`SOCIAL_ACCOUNTS`·`CMM_CODE`·`CMM_CODE_DTL`·`MENU` 등 대부분 테이블의 **원본 DDL이 없다**(다른 곳에서 이미 생성된 스키마로 추정, `MOTIVE` 스키마). 아래 컬럼 정보는 **MyBatis Mapper XML의 SELECT/INSERT 절에서 실제로 관찰된 컬럼만** 정리한 것이며, 이 목록에 없는 컬럼이 실제 DB에 더 있을 수 있다. 스키마 전체가 필요하면 DB를 직접 조회해서 확인하고, 이 문서를 추측으로 채우지 않는다.

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

## 3. 공통코드 (`Code-mapper.xml`)

**CMM_CODE**: `COM_CD_ID`(PK), `COM_CD_NM`, `USE_YN`, `SORT_SEQ`
**CMM_CODE_DTL**: `COM_CD_ID`(FK)+`DTL_CD_ID`(복합 PK), `DTL_CD_NM`, `DTL_CD_EXPLN`, `LNKG_DTL_CD_ID1`, `LNKG_DTL_CD_NM1`, `LNKG_DTL_CD_ID2`, `LNKG_DTL_CD_NM2`, `SORT_SEQ`

## 4. 미확인 테이블

`MENU`(메뉴 조회용) 테이블 컬럼은 `Menu-mapper.xml`을 확인해 채울 것 — 이 문서 작성 시점엔 조회 SQL이 단순 목록 조회라 컬럼명이 `MenuDto` 필드에 의존적이었다. 새로 다룰 때 매퍼 XML을 먼저 확인한다.