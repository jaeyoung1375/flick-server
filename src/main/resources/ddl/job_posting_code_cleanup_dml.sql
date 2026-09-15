-- 채용공고(잡코리아/사람인) 연동 공통코드 삭제
-- JOB_CAREER_CD, JOB_LOCATION_CD: LNKG_DTL_CD_ID1/NM1, LNKG_DTL_CD_ID2/NM2 컬럼으로
-- 외부 채용 사이트 코드값을 보관하던 공통코드 그룹. 스트리밍 서비스 전환으로 불필요.

DELETE FROM CMM_CODE_DTL WHERE COM_CD_ID IN ('JOB_CAREER_CD', 'JOB_LOCATION_CD');
DELETE FROM CMM_CODE WHERE COM_CD_ID IN ('JOB_CAREER_CD', 'JOB_LOCATION_CD');

COMMIT;

-- 채용공고 도메인 테이블 자체 삭제 (스트리밍 서비스 전환으로 불필요)
-- CASCADE CONSTRAINTS: 다른 테이블에서 JOB_POSTING을 참조하는 FK가 있다면 함께 제거됨
DROP TABLE JOB_POSTING CASCADE CONSTRAINTS;
