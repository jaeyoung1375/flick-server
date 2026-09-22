# 도메인 용어 사전 상세 (flick-server Domain Glossary)

이 문서는 flick-server 전반에서 쓰는 **용어·약어·상태값의 단일 출처**입니다. 실측(코드·매퍼) 기반이며, 추측으로 채우지 않았습니다.

> ⚠️ flick-server(스트리밍 OTT 서비스, 2026-09-13 확정)의 실제 도메인 용어(콘텐츠·구독·시청기록 등)는 아직 코드에 없다 — 구현되면 이 문서에 새 절로 추가한다. motive-server에서 이식된 운동 관련 도메인(운동 프로필·운동 기록·운동 마스터·AI 챗봇)은 2026-09-09 제거, 그 뒤 시도한 채용정보 도메인(공고·크롤러 등)도 2026-09-13 제거되어 아래 용어 사전에는 남아있지 않다. 프로젝트 목적은 [`01_project_overview/guide.md`](../01_project_overview/guide.md) 참고.

## 1. 사용자/인증

| 용어 | 코드 식별자 | 정의 |
|------|-------------|------|
| 회원 | User / `USERS` | 서비스 가입자. `USER_ID` PK(`USERS_SEQ` 채번) |
| 계정상태 | `STATUS` | 실측된 값: `'ACTIVE'`(조회 쿼리 WHERE 조건으로 사용). 그 외 값 존재 여부는 코드상 확인 안 됨 — 탈퇴/정지 상태값이 필요하면 먼저 확인 후 등록 |
| 권한 | `Role` enum | `USER`, `ADMIN`. JWT의 `role` claim에 담기지만 **인가 로직에서 아직 검증되지 않음**([`12_known_issues`](../12_known_issues/flick-server-known-issues.md)) |
| 소셜 제공자 | `Provider` enum / `SOCIAL_ACCOUNTS.PROVIDER` | `KAKAO`, `GOOGLE`, `GITHUB` |
| 소셜 연동 | `SOCIAL_ACCOUNTS` | `USER_ID` + `PROVIDER` + `PROVIDER_USER_ID` 조합으로 소셜 계정을 회원에 연결(`SOCIAL_SEQ` 채번) |

## 2. 공통코드

| 용어 | 코드 식별자 | 정의 |
|------|-------------|------|
| 공통코드(그룹) | `CMM_CODE` / `COM_CD_ID` | 코드 분류 단위 |
| 상세코드 | `CMM_CODE_DTL` / `DTL_CD_ID` | 그룹에 속한 개별 코드값 |
| 연결상세코드 | `LNKG_DTL_CD_ID1`, `LNKG_DTL_CD_ID2` | 다른 코드 그룹의 상세코드와 매핑(용도는 사용처마다 다름 — 신규 활용 전 기존 사용 예 확인) |
| 사용여부 | `USE_YN` | 공개 조회(`getCodeList`)는 `'Y'`만 노출 |

## 3. 공통

| 용어 | 정의 |
|------|------|
| `ApiResponse<T>` | 표준 응답 래퍼. `code`(예: 정상 `"0000"`)·`message`·`data` |
| `ResponseCode` | 모든 `*ErrorCode` enum이 구현하는 인터페이스(`code`/`httpStatus`/`message`) |
| REG_DT / MOD_DT | 등록일시 / 수정일시 (테이블 공통 컬럼 관례, `SYSDATE` 기본) |

> ⚠️ 이 저장소는 A-RMS(루트 워크스페이스)의 "제품/버전/요구사항" 용어 체계와 무관하다. 그쪽 용어를 여기 끌어오지 않는다.