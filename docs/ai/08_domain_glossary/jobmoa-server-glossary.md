# 도메인 용어 사전 상세 (jobmoa-server Domain Glossary)

이 문서는 jobmoa-server 전반에서 쓰는 **용어·약어·상태값의 단일 출처**입니다. 실측(코드·매퍼) 기반이며, 추측으로 채우지 않았습니다.

> ⚠️ **레거시 도메인:** 아래 용어는 전부 motive-server(운동 기록 서비스) 시절 코드에서 실측한 것이다. jobmoa(채용정보 통합 서비스)의 실제 도메인 용어(채용공고·채용사이트·스크랩 등)는 아직 코드에 없다 — 구현되면 이 문서에 새 절로 추가한다. 프로젝트 목적은 [`01_project_overview/guide.md`](../01_project_overview/guide.md) 참고.

## 1. 사용자/인증

| 용어 | 코드 식별자 | 정의 |
|------|-------------|------|
| 회원 | User / `USERS` | 서비스 가입자. `USER_ID` PK(`USERS_SEQ` 채번) |
| 계정상태 | `STATUS` | 실측된 값: `'ACTIVE'`(조회 쿼리 WHERE 조건으로 사용). 그 외 값 존재 여부는 코드상 확인 안 됨 — 탈퇴/정지 상태값이 필요하면 먼저 확인 후 등록 |
| 권한 | `Role` enum | `USER`, `ADMIN`. JWT의 `role` claim에 담기지만 **인가 로직에서 아직 검증되지 않음**([`12_known_issues`](../12_known_issues/jobmoa-server-known-issues.md)) |
| 소셜 제공자 | `Provider` enum / `SOCIAL_ACCOUNTS.PROVIDER` | `KAKAO`, `GOOGLE`, `GITHUB` |
| 소셜 연동 | `SOCIAL_ACCOUNTS` | `USER_ID` + `PROVIDER` + `PROVIDER_USER_ID` 조합으로 소셜 계정을 회원에 연결(`SOCIAL_SEQ` 채번) |
| 온보딩 완료 | (계산값, 컬럼 아님) | `USER_FITNESS_PROFILE`에 해당 `USER_ID` 행이 존재하는지로 판정(`existsProfile`) |

## 2. 운동 프로필

| 용어 | 코드 식별자 | 정의 |
|------|-------------|------|
| 운동경력 | `EXPERIENCE_CD` | 공통코드 그룹 참조(코드값 상세는 `CMM_CODE_DTL` 데이터에 있음 — 이 문서에 임의 나열하지 않음) |
| 운동레벨 | `LEVEL_CD` | 공통코드 그룹 참조 |
| 보유장비 | `EQUIPMENT_CD` | 공통코드 그룹 참조 (운동마스터의 `EXERCISE_EQUIPMENT_CD`와 별개 그룹일 가능성 — 코드 그룹명 확인 필요) |
| 스쿼트/벤치프레스 가능여부 | `SQUAT_YN`, `BENCH_PRESS_YN` | `'Y'`/`'N'` — 등록 시 미입력이면 서버가 `'N'`으로 기본값 처리(`NVL(..., 'N')`) |

## 3. 운동 기록

| 용어 | 코드 식별자 | 정의 |
|------|-------------|------|
| 운동기록(헤더) | `WORKOUT_RECORD` | 특정 날짜·카테고리의 운동 세션 1건 |
| 카테고리 | `CATEGORY_CD` | 실측 값: 헬스/홈트/러닝/등산/야외 (VARCHAR2, 현재 공통코드 미편입) |
| 상세기록 | `WORKOUT_RECORD_EXERCISE` | 세션에 포함된 개별 운동(순서=`SORT_NO`) |
| 세트기록 | `WORKOUT_RECORD_SET` | 상세기록의 세트 단위 기록(`SET_NO`, `WEIGHT`(kg), `REPS`) |
| 운동시간 | `DURATION_MIN` | 시/분 입력을 분 단위로 합산 저장 |

## 4. 운동 마스터

| 용어 | 코드 식별자 | 정의 |
|------|-------------|------|
| 운동 | `EXERCISES` | 운동 마스터 데이터(`EXERCISE_ID` PK) |
| 대표부위 | `BODY_PART_CD` | 공통코드 그룹 `BODY_PART_CD` 참조 |
| 요구장비 | `EQUIPMENT_CD`(EXERCISES 컬럼) | 공통코드 그룹 `EXERCISE_EQUIPMENT_CD` 참조 |

## 5. 공통코드

| 용어 | 코드 식별자 | 정의 |
|------|-------------|------|
| 공통코드(그룹) | `CMM_CODE` / `COM_CD_ID` | 코드 분류 단위 |
| 상세코드 | `CMM_CODE_DTL` / `DTL_CD_ID` | 그룹에 속한 개별 코드값 |
| 연결상세코드 | `LNKG_DTL_CD_ID1`, `LNKG_DTL_CD_ID2` | 다른 코드 그룹의 상세코드와 매핑(용도는 사용처마다 다름 — 신규 활용 전 기존 사용 예 확인) |
| 사용여부 | `USE_YN` | 공개 조회(`getCodeList`)는 `'Y'`만 노출 |

## 6. 공통

| 용어 | 정의 |
|------|------|
| `ApiResponse<T>` | 표준 응답 래퍼. `code`(예: 정상 `"0000"`)·`message`·`data` |
| `ResponseCode` | 모든 `*ErrorCode` enum이 구현하는 인터페이스(`code`/`httpStatus`/`message`) |
| REG_DT / MOD_DT | 등록일시 / 수정일시 (테이블 공통 컬럼 관례, `SYSDATE` 기본) |

## 7. AI 챗봇 ([`06_domain_playbooks/ai-chat.md`](../06_domain_playbooks/ai-chat.md) 참고)

| 용어 | 정의 |
|------|------|
| 개인화 조회 | 사용자의 실제 운동 기록(`WORKOUT_RECORD` 등)을 근거로 답하는 질문 유형. RAG가 아니라 일반 SQL 조회 + 비즈니스 로직으로 처리(**구현됨**) |
| Function Calling / Tool Calling | LLM에게 서버 함수(`getNextWorkoutPart`)를 도구로 알려주고, 질문 의미를 보고 LLM이 스스로 호출 여부를 판단하게 하는 방식. 키워드 매칭과 구분되는 개념(**구현됨** — `org.springframework.ai.tool.annotation.Tool`) |
| 분할(스플릿) | 하루에 같이 훈련하는 신체부위 묶음(예: "등/이두"). `WorkoutRecommendationTool.SPLIT_ORDER`에 고정 4단계로 정의(**구현됨**, 사용자별 커스텀은 계획) |
| RAG(Retrieval-Augmented Generation) | 일반 지식 질문(예: 자세법)에 한해 지식 문서를 벡터 검색해 근거로 삼는 방식(**계획**, 지식베이스 위치 미정 — 현재는 LLM 자체 지식으로 임시 응답) |
| 벡터스토어 | RAG용 지식 문서 임베딩 저장소. Oracle `VECTOR` 타입 vs 별도 Postgres/pgvector — **미정**(계획) |

> ⚠️ 이 저장소는 A-RMS(루트 워크스페이스)의 "제품/버전/요구사항" 용어 체계와 무관하다. 그쪽 용어를 여기 끌어오지 않는다.