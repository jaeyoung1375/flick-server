-- =============================================================================
-- FLICK-SERVER: PostgreSQL 신규 스키마 DDL (public 스키마)
-- Oracle(MOTIVE 스키마) -> PostgreSQL 전면 전환 작업의 일부
-- =============================================================================
--
-- [작성 근거]
-- 실제 Oracle MOTIVE 스키마를 SQL Developer로 조회한 결과(user_tab_columns /
-- user_constraints / user_sequences, 2026-09-15)를 그대로 반영했다. 이전 초안은
-- Oracle MCP 접속 장애로 매퍼/DTO 추론에 의존한 [가정]이었으나, 이번 버전은 전부
-- 실측값이다.
--
-- [타입 매핑 규칙]
--   VARCHAR2(n)      -> VARCHAR(n)
--   CHAR(n)          -> CHAR(n)  (원본 타입 그대로 유지, VARCHAR로 바꾸지 않음)
--   DATE             -> TIMESTAMP (Oracle DATE는 날짜+시간을 포함)
--     예외: CMM_MENU.REG_DT는 실제 컬럼 타입이 DATE가 아니라 CHAR(8)이라 그대로 유지
--   CLOB             -> TEXT
--   NUMBER(*, 0) 및 정밀도 미지정 NUMBER -> BIGINT
--     (조회 결과 모든 NUMBER 컬럼이 scale=0, 즉 정수 전용이라 단순화함.
--      FILE_ID/USER_ID/SOCIAL_ID 등은 원본이 NUMBER(38,0)이라 BIGINT 범위를
--      초과할 수 있으나, 실제 시퀀스 현재값이 21~81 수준으로 극히 작아 문제 없음)
--   SYSDATE 기본값   -> CURRENT_TIMESTAMP
--
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 시퀀스 (매퍼 XML의 nextval() 호출과 이름 일치, START WITH는 신규/빈 테이블 기준.
-- 기존 Oracle 데이터를 이관할 경우 이관 후 실제 MAX(id)+1로 재조정할 것 —
-- 조회 시점 Oracle last_number: CMM_FILE_SEQ=21, USERS_SEQ=81, SOCIAL_SEQ=81)
-- -----------------------------------------------------------------------------
CREATE SEQUENCE IF NOT EXISTS cmm_file_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS users_seq    START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS social_seq   START WITH 1 INCREMENT BY 1;

-- -----------------------------------------------------------------------------
-- CMM_FILE (공통 파일)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cmm_file (
    file_id       BIGINT       NOT NULL,
    org_file_nm   VARCHAR(255) NOT NULL,
    save_file_nm  VARCHAR(255) NOT NULL,
    file_path     VARCHAR(100) NOT NULL,
    file_size     BIGINT       NULL,
    file_ext      VARCHAR(20)  NOT NULL,
    del_yn        CHAR(1)      NULL,
    del_dt        TIMESTAMP    NULL,
    temp_yn       CHAR(1)      NULL,
    temp_key      VARCHAR(200) NULL,
    CONSTRAINT cmm_file_pk PRIMARY KEY (file_id)
);

COMMENT ON TABLE  cmm_file IS '공통 파일';
COMMENT ON COLUMN cmm_file.file_id      IS '파일아이디';
COMMENT ON COLUMN cmm_file.org_file_nm  IS '원본파일명';
COMMENT ON COLUMN cmm_file.save_file_nm IS '저장파일명';
COMMENT ON COLUMN cmm_file.file_path    IS '파일경로';
COMMENT ON COLUMN cmm_file.file_size    IS '파일크기';
COMMENT ON COLUMN cmm_file.file_ext     IS '파일확장자';
COMMENT ON COLUMN cmm_file.del_yn       IS '삭제여부(Y/N) — 매퍼 미사용, 컬럼 존재만 확인';
COMMENT ON COLUMN cmm_file.del_dt       IS '삭제일시 — 매퍼 미사용, 컬럼 존재만 확인';
COMMENT ON COLUMN cmm_file.temp_yn      IS '임시파일여부(Y/N)';
COMMENT ON COLUMN cmm_file.temp_key     IS '임시파일키';

-- -----------------------------------------------------------------------------
-- CMM_CODE (공통코드)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cmm_code (
    com_cd_id       VARCHAR(100) NOT NULL,
    com_cd_nm       VARCHAR(100) NOT NULL,
    com_cd_expln    VARCHAR(100) NULL,
    use_yn          CHAR(1)      NULL DEFAULT 'Y',
    link_com_cd_id1 VARCHAR(100) NULL,
    link_com_cd_id2 VARCHAR(100) NULL,
    reg_id          VARCHAR(50)  NOT NULL,
    reg_dt          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    mod_id          VARCHAR(50)  NULL,
    mod_dt          TIMESTAMP    NULL,
    sort_seq        BIGINT       NULL,
    CONSTRAINT pk_cmm_code PRIMARY KEY (com_cd_id)
);

COMMENT ON TABLE  cmm_code IS '공통코드';
COMMENT ON COLUMN cmm_code.com_cd_id       IS '공통코드아이디';
COMMENT ON COLUMN cmm_code.com_cd_nm       IS '공통코드명';
COMMENT ON COLUMN cmm_code.com_cd_expln    IS '공통코드설명';
COMMENT ON COLUMN cmm_code.use_yn          IS '사용여부(Y/N)';
COMMENT ON COLUMN cmm_code.link_com_cd_id1 IS '연결공통코드아이디1';
COMMENT ON COLUMN cmm_code.link_com_cd_id2 IS '연결공통코드아이디2';
COMMENT ON COLUMN cmm_code.reg_id          IS '등록아이디';
COMMENT ON COLUMN cmm_code.reg_dt          IS '등록일시';
COMMENT ON COLUMN cmm_code.mod_id          IS '변경아이디';
COMMENT ON COLUMN cmm_code.mod_dt          IS '변경일시';
COMMENT ON COLUMN cmm_code.sort_seq        IS '정렬순서';

-- -----------------------------------------------------------------------------
-- CMM_MENU (메뉴)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cmm_menu (
    menu_id    VARCHAR(30)  NOT NULL,
    menu_nm    VARCHAR(200) NULL,
    menu_url   VARCHAR(200) NULL,
    menu_level BIGINT       NULL,
    menu_ord   BIGINT       NULL,
    up_menu_id VARCHAR(30)  NULL,
    top_yn     CHAR(1)      NULL DEFAULT 'N',
    use_yn     CHAR(1)      NULL DEFAULT 'Y',
    reg_id     VARCHAR(30)  NULL,
    reg_dt     CHAR(8)      NULL,
    CONSTRAINT pk_cmm_menu PRIMARY KEY (menu_id)
);

COMMENT ON TABLE  cmm_menu IS '메뉴';
COMMENT ON COLUMN cmm_menu.menu_id    IS '메뉴아이디';
COMMENT ON COLUMN cmm_menu.menu_nm    IS '메뉴명';
COMMENT ON COLUMN cmm_menu.menu_url   IS '메뉴URL';
COMMENT ON COLUMN cmm_menu.menu_level IS '메뉴레벨';
COMMENT ON COLUMN cmm_menu.menu_ord   IS '메뉴정렬순서';
COMMENT ON COLUMN cmm_menu.up_menu_id IS '상위메뉴아이디';
COMMENT ON COLUMN cmm_menu.top_yn     IS '상위여부(Y/N)';
COMMENT ON COLUMN cmm_menu.use_yn     IS '사용여부(Y/N) — 매퍼 미사용, 컬럼 존재만 확인';
COMMENT ON COLUMN cmm_menu.reg_id     IS '등록아이디 — 매퍼 미사용, 컬럼 존재만 확인';
COMMENT ON COLUMN cmm_menu.reg_dt     IS '등록일시(YYYYMMDD 문자열) — 매퍼 미사용, 컬럼 존재만 확인';

-- -----------------------------------------------------------------------------
-- USERS (회원)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    user_id          BIGINT       NOT NULL,
    email            VARCHAR(255) NULL,                 -- 실측: UNIQUE 제약 없음 (findByEmail은 애플리케이션 레벨에서만 유일성 가정)
    password_hash    VARCHAR(255) NULL,
    status           VARCHAR(20)  NOT NULL,
    name             VARCHAR(50)  NOT NULL,
    phone            VARCHAR(20)  NULL,
    "ROLE"           VARCHAR(20)  NOT NULL,              -- Oracle 예약어, 매퍼에서 "ROLE"로 대문자 인용 -> 동일하게 유지
    reg_dt           TIMESTAMP    NULL,
    mod_dt           TIMESTAMP    NULL,
    profile_file_id  BIGINT       NULL,
    last_login_dt    TIMESTAMP    NULL,
    gender           CHAR(2)      NULL,
    birth            TIMESTAMP    NULL,
    nickname         VARCHAR(50)  NULL,
    CONSTRAINT users_pk PRIMARY KEY (user_id),
    CONSTRAINT users_cmm_file_fk FOREIGN KEY (profile_file_id) REFERENCES cmm_file (file_id)
);

COMMENT ON TABLE  users IS '회원';
COMMENT ON COLUMN users.user_id         IS '회원아이디';
COMMENT ON COLUMN users.email           IS '이메일';
COMMENT ON COLUMN users.password_hash   IS '비밀번호 해시 (소셜 전용 가입자는 NULL)';
COMMENT ON COLUMN users.status          IS '계정상태';
COMMENT ON COLUMN users.name            IS '이름';
COMMENT ON COLUMN users.phone           IS '휴대폰번호';
COMMENT ON COLUMN users."ROLE"          IS '권한';
COMMENT ON COLUMN users.reg_dt          IS '등록일시';
COMMENT ON COLUMN users.mod_dt          IS '수정일시';
COMMENT ON COLUMN users.profile_file_id IS '프로필 파일아이디';
COMMENT ON COLUMN users.last_login_dt   IS '마지막 로그인일시';
COMMENT ON COLUMN users.gender          IS '성별';
COMMENT ON COLUMN users.birth           IS '생년월일';
COMMENT ON COLUMN users.nickname        IS '닉네임';

-- -----------------------------------------------------------------------------
-- CMM_CODE_DTL (상세코드)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cmm_code_dtl (
    com_cd_id       VARCHAR(100) NOT NULL,
    dtl_cd_id       VARCHAR(100) NOT NULL,
    dtl_cd_nm       VARCHAR(200) NOT NULL,
    dtl_cd_expln    VARCHAR(1000) NULL,
    use_yn          VARCHAR(1)   NULL DEFAULT 'Y',       -- 실측: CMM_CODE.USE_YN과 달리 CHAR(1)이 아니라 VARCHAR2(1)
    sort_seq        BIGINT       NULL,
    lnkg_dtl_cd_id1 VARCHAR(100) NULL,
    lnkg_dtl_cd_nm1 VARCHAR(100) NULL,
    lnkg_dtl_cd_id2 VARCHAR(100) NULL,
    lnkg_dtl_cd_nm2 VARCHAR(100) NULL,
    reg_id          VARCHAR(50)  NOT NULL,
    reg_dt          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    mod_id          VARCHAR(50)  NULL,
    mod_dt          TIMESTAMP    NULL,
    CONSTRAINT pk_cmm_code_dtl PRIMARY KEY (com_cd_id, dtl_cd_id),
    CONSTRAINT fk_cmm_code_dtl_01 FOREIGN KEY (com_cd_id) REFERENCES cmm_code (com_cd_id)
);

COMMENT ON TABLE  cmm_code_dtl IS '상세코드';
COMMENT ON COLUMN cmm_code_dtl.com_cd_id       IS '공통코드아이디 (FK)';
COMMENT ON COLUMN cmm_code_dtl.dtl_cd_id       IS '상세코드아이디';
COMMENT ON COLUMN cmm_code_dtl.dtl_cd_nm       IS '상세코드명';
COMMENT ON COLUMN cmm_code_dtl.dtl_cd_expln    IS '상세코드설명';
COMMENT ON COLUMN cmm_code_dtl.use_yn          IS '사용여부(Y/N)';
COMMENT ON COLUMN cmm_code_dtl.sort_seq        IS '정렬순서';
COMMENT ON COLUMN cmm_code_dtl.lnkg_dtl_cd_id1 IS '연결상세코드아이디1';
COMMENT ON COLUMN cmm_code_dtl.lnkg_dtl_cd_nm1 IS '연결상세코드명1';
COMMENT ON COLUMN cmm_code_dtl.lnkg_dtl_cd_id2 IS '연결상세코드아이디2';
COMMENT ON COLUMN cmm_code_dtl.lnkg_dtl_cd_nm2 IS '연결상세코드명2';
COMMENT ON COLUMN cmm_code_dtl.reg_id          IS '등록아이디';
COMMENT ON COLUMN cmm_code_dtl.reg_dt          IS '등록일시';
COMMENT ON COLUMN cmm_code_dtl.mod_id          IS '변경아이디';
COMMENT ON COLUMN cmm_code_dtl.mod_dt          IS '변경일시';

-- -----------------------------------------------------------------------------
-- SOCIAL_ACCOUNTS (소셜 로그인 연동)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS social_accounts (
    social_id               BIGINT       NOT NULL,
    user_id                 BIGINT       NULL,
    provider                VARCHAR(100) NOT NULL,
    provider_user_id        VARCHAR(100) NOT NULL,
    reg_dt                  TIMESTAMP    NULL,
    mod_dt                  TIMESTAMP    NULL,
    profile_file_id         BIGINT       NULL,
    provider_access_token   TEXT         NULL,
    expires_at              TIMESTAMP    NULL,
    CONSTRAINT social_accounts_pk PRIMARY KEY (social_id),
    CONSTRAINT social_accounts_users_fk FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT social_accounts_cmm_file_fk FOREIGN KEY (profile_file_id) REFERENCES cmm_file (file_id),
    CONSTRAINT social_accounts_unique_new UNIQUE (provider, provider_user_id)
);

COMMENT ON TABLE  social_accounts IS '소셜 로그인 연동';
COMMENT ON COLUMN social_accounts.social_id             IS '소셜계정아이디';
COMMENT ON COLUMN social_accounts.user_id               IS '회원아이디 (FK)';
COMMENT ON COLUMN social_accounts.provider              IS '소셜 로그인 제공자';
COMMENT ON COLUMN social_accounts.provider_user_id      IS '제공자측 사용자 고유아이디';
COMMENT ON COLUMN social_accounts.reg_dt                IS '등록일시';
COMMENT ON COLUMN social_accounts.mod_dt                IS '수정일시 — 매퍼 미사용, 컬럼 존재만 확인';
COMMENT ON COLUMN social_accounts.profile_file_id       IS '프로필 파일아이디 (FK) — 매퍼 미사용, 컬럼 존재만 확인';
COMMENT ON COLUMN social_accounts.provider_access_token IS '소셜 제공자 액세스 토큰 — 매퍼 미사용, 컬럼 존재만 확인';
COMMENT ON COLUMN social_accounts.expires_at            IS '토큰 만료일시 — 매퍼 미사용, 컬럼 존재만 확인';
