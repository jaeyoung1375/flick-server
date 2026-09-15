-- =============================================================================
-- FLICK-SERVER: M1 콘텐츠 카탈로그 DDL (설계 초안, 아직 미확정)
-- 로드맵: docs/ai/01_project_overview/flick-server-streaming-roadmap.md §1 참고
-- =============================================================================
--
-- 설계 결정 (대화로 확정된 것)
--   - 장르, 출연진 역할구분은 CMM_CODE/CMM_CODE_DTL 공통코드로 관리 (신규 CMM_CODE 그룹:
--     GENRE, CAST_ROLE — 실제 코드 값은 관리자 화면에서 CMM_CODE 등록 API로 나중에 채움)
--   - 개봉일은 시간 불필요 -> DATE 타입
--   - 출연진(CONTENT_CAST)은 N:M -> 별도 매핑 테이블, PK에 역할코드까지 포함
--     (같은 사람이 한 작품에서 배우+감독 등 복수 역할을 가질 수 있어서)
--   - 포스터/프로필 이미지는 기존 CMM_FILE 재사용 (FK만 연결, 별도 이미지 테이블 없음)
--   - CONTENT_TYPE(MOVIE/SERIES), PUBLISH_STATUS는 기존 USERS.STATUS/SOCIAL_ACCOUNTS.PROVIDER와
--     같은 패턴으로 공통코드화하지 않고 단순 VARCHAR로 둠 (값 종류가 적고 거의 안 바뀜)
--   - CMM_CODE_DTL의 PK가 (COM_CD_ID, DTL_CD_ID) 복합키라서, GENRE/CAST_ROLE을 참조하는
--     매핑 테이블에는 DB 레벨 FK를 걸지 않고 애플리케이션에서 유효성 검증한다
--     (COM_CD_ID까지 컬럼으로 중복 저장하지 않기 위한 단순화 — 필요해지면 나중에 추가 가능)
--
-- =============================================================================

CREATE SEQUENCE IF NOT EXISTS content_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS season_seq  START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS episode_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS person_seq  START WITH 1 INCREMENT BY 1;

-- -----------------------------------------------------------------------------
-- CONTENT (작품)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS content (
    content_id       BIGINT       NOT NULL,
    title            VARCHAR(200) NOT NULL,
    synopsis         TEXT         NULL,
    thumbnail_file_id BIGINT      NULL,
    release_date     DATE         NULL,
    age_rating       VARCHAR(10)  NULL,               -- 예: ALL, 12, 15, 19 (자유 값, 공통코드 아님)
    content_type     VARCHAR(10)  NOT NULL,            -- MOVIE, SERIES
    publish_status   VARCHAR(10)  NOT NULL DEFAULT 'DRAFT', -- DRAFT, PUBLISHED, HIDDEN
    reg_id           VARCHAR(50)  NOT NULL,
    reg_dt           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    mod_id           VARCHAR(50)  NULL,
    mod_dt           TIMESTAMP    NULL,
    CONSTRAINT pk_content PRIMARY KEY (content_id),
    CONSTRAINT fk_content_thumbnail FOREIGN KEY (thumbnail_file_id) REFERENCES cmm_file (file_id)
);

COMMENT ON TABLE  content IS '작품 (영화 또는 시리즈)';
COMMENT ON COLUMN content.content_id        IS '작품아이디';
COMMENT ON COLUMN content.title             IS '제목';
COMMENT ON COLUMN content.synopsis          IS '줄거리';
COMMENT ON COLUMN content.thumbnail_file_id IS '포스터이미지 (FK -> cmm_file)';
COMMENT ON COLUMN content.release_date      IS '개봉일';
COMMENT ON COLUMN content.age_rating        IS '연령등급';
COMMENT ON COLUMN content.content_type      IS '타입 (MOVIE/SERIES)';
COMMENT ON COLUMN content.publish_status    IS '공개상태 (DRAFT/PUBLISHED/HIDDEN)';
COMMENT ON COLUMN content.reg_id            IS '등록아이디';
COMMENT ON COLUMN content.reg_dt            IS '등록일시';
COMMENT ON COLUMN content.mod_id            IS '변경아이디';
COMMENT ON COLUMN content.mod_dt            IS '변경일시';

-- -----------------------------------------------------------------------------
-- SEASON (시즌) — SERIES 타입에서만 사용, MOVIE는 시즌 없이 EPISODE 1건
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS season (
    season_id  BIGINT       NOT NULL,
    content_id BIGINT       NOT NULL,
    season_no  INTEGER      NOT NULL,
    title      VARCHAR(200) NULL,
    reg_dt     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_season PRIMARY KEY (season_id),
    CONSTRAINT fk_season_content FOREIGN KEY (content_id) REFERENCES content (content_id),
    CONSTRAINT uq_season_content_no UNIQUE (content_id, season_no)
);

COMMENT ON TABLE  season IS '시즌 (시리즈 전용)';
COMMENT ON COLUMN season.season_id  IS '시즌아이디';
COMMENT ON COLUMN season.content_id IS '작품아이디 (FK)';
COMMENT ON COLUMN season.season_no  IS '시즌번호';
COMMENT ON COLUMN season.title      IS '시즌 제목 (예: 시즌 1)';
COMMENT ON COLUMN season.reg_dt     IS '등록일시';

-- -----------------------------------------------------------------------------
-- EPISODE (회차) — 실제 재생 단위. 영상 컬럼은 M2에서 VIDEO_ASSET으로 분리 연결
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS episode (
    episode_id       BIGINT       NOT NULL,
    content_id       BIGINT       NOT NULL,
    season_id        BIGINT       NULL,               -- MOVIE는 NULL
    episode_no       INTEGER      NULL,                -- MOVIE는 NULL 또는 1
    title            VARCHAR(200) NULL,
    running_time_sec BIGINT       NULL,
    reg_dt           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_episode PRIMARY KEY (episode_id),
    CONSTRAINT fk_episode_content FOREIGN KEY (content_id) REFERENCES content (content_id),
    CONSTRAINT fk_episode_season  FOREIGN KEY (season_id)  REFERENCES season (season_id)
);

COMMENT ON TABLE  episode IS '회차 (실제 재생 단위)';
COMMENT ON COLUMN episode.episode_id       IS '회차아이디';
COMMENT ON COLUMN episode.content_id       IS '작품아이디 (FK)';
COMMENT ON COLUMN episode.season_id        IS '시즌아이디 (FK, MOVIE는 NULL)';
COMMENT ON COLUMN episode.episode_no       IS '회차번호';
COMMENT ON COLUMN episode.title            IS '회차 제목';
COMMENT ON COLUMN episode.running_time_sec IS '러닝타임(초)';
COMMENT ON COLUMN episode.reg_dt           IS '등록일시';

-- -----------------------------------------------------------------------------
-- PERSON (배우/감독 등 인물)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS person (
    person_id        BIGINT      NOT NULL,
    name             VARCHAR(100) NOT NULL,
    profile_file_id  BIGINT      NULL,
    reg_dt           TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_person PRIMARY KEY (person_id),
    CONSTRAINT fk_person_profile_file FOREIGN KEY (profile_file_id) REFERENCES cmm_file (file_id)
);

COMMENT ON TABLE  person IS '배우/감독 등 인물';
COMMENT ON COLUMN person.person_id       IS '인물아이디';
COMMENT ON COLUMN person.name            IS '이름';
COMMENT ON COLUMN person.profile_file_id IS '프로필사진 (FK -> cmm_file)';
COMMENT ON COLUMN person.reg_dt          IS '등록일시';

-- -----------------------------------------------------------------------------
-- CONTENT_CAST (작품-인물 매핑, N:M) — 역할구분은 CMM_CODE_DTL('CAST_ROLE' 그룹) 값
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS content_cast (
    content_id     BIGINT       NOT NULL,
    person_id      BIGINT       NOT NULL,
    role_cd        VARCHAR(50)  NOT NULL,             -- cmm_code_dtl(com_cd_id='CAST_ROLE').dtl_cd_id 값, DB FK 없음(앱 검증)
    character_name VARCHAR(100) NULL,
    sort_seq       BIGINT       NULL,
    CONSTRAINT pk_content_cast PRIMARY KEY (content_id, person_id, role_cd),
    CONSTRAINT fk_content_cast_content FOREIGN KEY (content_id) REFERENCES content (content_id),
    CONSTRAINT fk_content_cast_person  FOREIGN KEY (person_id)  REFERENCES person (person_id)
);

COMMENT ON TABLE  content_cast IS '작품-인물 매핑 (N:M, 역할별)';
COMMENT ON COLUMN content_cast.content_id     IS '작품아이디 (FK)';
COMMENT ON COLUMN content_cast.person_id      IS '인물아이디 (FK)';
COMMENT ON COLUMN content_cast.role_cd        IS '역할구분 코드 (CMM_CODE_DTL: CAST_ROLE 그룹, 예: ACTOR/DIRECTOR/WRITER)';
COMMENT ON COLUMN content_cast.character_name IS '배역명 (배우인 경우)';
COMMENT ON COLUMN content_cast.sort_seq       IS '정렬순서';

-- -----------------------------------------------------------------------------
-- CONTENT_GENRE_MAP (작품-장르 매핑, N:M) — CMM_CODE_DTL('GENRE' 그룹) 값
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS content_genre_map (
    content_id BIGINT      NOT NULL,
    dtl_cd_id  VARCHAR(100) NOT NULL,                 -- cmm_code_dtl(com_cd_id='GENRE').dtl_cd_id 값, DB FK 없음(앱 검증)
    CONSTRAINT pk_content_genre_map PRIMARY KEY (content_id, dtl_cd_id),
    CONSTRAINT fk_content_genre_map_content FOREIGN KEY (content_id) REFERENCES content (content_id)
);

COMMENT ON TABLE  content_genre_map IS '작품-장르 매핑 (N:M)';
COMMENT ON COLUMN content_genre_map.content_id IS '작품아이디 (FK)';
COMMENT ON COLUMN content_genre_map.dtl_cd_id  IS '장르코드 (CMM_CODE_DTL: GENRE 그룹)';
