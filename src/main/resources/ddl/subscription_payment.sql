-- =============================================================================
-- FLICK-SERVER: 이용권 결제 DDL (설계 초안, dba-expert 자문 반영)
-- 로드맵: TODO.md §1 참고
-- =============================================================================
--
-- 설계 결정 (대화로 확정된 것)
--   - 결제대행사: 토스페이먼츠 테스트 모드 (사업자등록 없음, 실제 결제 아님)
--   - 이용권: 월권/연간권, 광고 유무(has_ads) 플래그 포함
--   - 자동 정기결제(빌링키) 없음 — 결제 시점 1회 승인 후 만료일 계산해서 저장하는 단순 방식
--     (자동갱신은 추후 확장, 이번 범위 아님)
--   - amount/price는 NUMERIC(12,0) — BigDecimal 매핑, 원화라 소수부 불필요
--   - raw_response는 JSONB — 추후 특정 필드 조회 대비
--   - FK에 CASCADE 없음 — Plan/Subscription은 물리삭제 대신 상태값으로만 관리
--   - TIMESTAMP(타임존 없음) — 기존 m1_content_catalog.sql 컨벤션 그대로 (해외 확장 시 재검토)
--
-- =============================================================================

CREATE SEQUENCE IF NOT EXISTS plan_seq             START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS subscription_seq     START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS payment_history_seq  START WITH 1 INCREMENT BY 1;

-- -----------------------------------------------------------------------------
-- PLAN (이용권 상품)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS plan (
    plan_id       BIGINT        NOT NULL,
    name          VARCHAR(50)   NOT NULL,               -- 예: 월권, 연간권
    price         NUMERIC(12,0) NOT NULL,                -- 정가 (원)
    duration_days INTEGER       NOT NULL,                -- 이용기간(일) 예: 30, 365
    has_ads       BOOLEAN       NOT NULL DEFAULT TRUE,   -- TRUE=광고있음, FALSE=광고없음
    reg_id        VARCHAR(50)   NOT NULL,
    reg_dt        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    mod_id        VARCHAR(50)   NULL,
    mod_dt        TIMESTAMP     NULL,
    CONSTRAINT pk_plan PRIMARY KEY (plan_id),
    CONSTRAINT uq_plan_name UNIQUE (name)                -- 이용권명 중복 방지 (값이 소수 고정이라 선택 사항)
);

COMMENT ON TABLE  plan IS '이용권 상품 (월권/연간권)';
COMMENT ON COLUMN plan.plan_id       IS '이용권아이디';
COMMENT ON COLUMN plan.name          IS '이용권명 (예: 월권, 연간권)';
COMMENT ON COLUMN plan.price         IS '정가 (원 단위)';
COMMENT ON COLUMN plan.duration_days IS '이용기간(일)';
COMMENT ON COLUMN plan.has_ads       IS '광고노출여부 (TRUE=광고있음/FALSE=광고없음)';
COMMENT ON COLUMN plan.reg_id        IS '등록아이디';
COMMENT ON COLUMN plan.reg_dt        IS '등록일시';
COMMENT ON COLUMN plan.mod_id        IS '변경아이디';
COMMENT ON COLUMN plan.mod_dt        IS '변경일시';

-- -----------------------------------------------------------------------------
-- SUBSCRIPTION (구독 - 사용자별 이용권 가입 이력)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS subscription (
    subscription_id BIGINT      NOT NULL,
    user_id         BIGINT      NOT NULL,
    plan_id         BIGINT      NOT NULL,
    start_at        TIMESTAMP   NOT NULL,
    end_at          TIMESTAMP   NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, EXPIRED, CANCELLED
    reg_id          VARCHAR(50) NOT NULL,
    reg_dt          TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    mod_id          VARCHAR(50) NULL,
    mod_dt          TIMESTAMP   NULL,
    CONSTRAINT pk_subscription PRIMARY KEY (subscription_id),
    CONSTRAINT fk_subscription_users FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_subscription_plan  FOREIGN KEY (plan_id) REFERENCES plan (plan_id)
);

COMMENT ON TABLE  subscription IS '구독 (사용자별 이용권 가입 이력)';
COMMENT ON COLUMN subscription.subscription_id IS '구독아이디';
COMMENT ON COLUMN subscription.user_id         IS '회원아이디 (FK)';
COMMENT ON COLUMN subscription.plan_id         IS '이용권아이디 (FK)';
COMMENT ON COLUMN subscription.start_at        IS '구독시작일시';
COMMENT ON COLUMN subscription.end_at          IS '구독만료일시';
COMMENT ON COLUMN subscription.status          IS '구독상태 (ACTIVE/EXPIRED/CANCELLED)';
COMMENT ON COLUMN subscription.reg_id          IS '등록아이디';
COMMENT ON COLUMN subscription.reg_dt          IS '등록일시';
COMMENT ON COLUMN subscription.mod_id          IS '변경아이디';
COMMENT ON COLUMN subscription.mod_dt          IS '변경일시';

-- 동시성 가드: 사용자당 ACTIVE 구독은 항상 1건만 존재하도록 DB 레벨에서 강제 (부분 유니크 인덱스)
CREATE UNIQUE INDEX IF NOT EXISTS uq_subscription_user_active
    ON subscription (user_id)
    WHERE status = 'ACTIVE';

-- -----------------------------------------------------------------------------
-- PAYMENT_HISTORY (결제 이력 - 토스페이먼츠 승인 요청/결과)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS payment_history (
    payment_id        BIGINT        NOT NULL,
    user_id           BIGINT        NOT NULL,
    subscription_id   BIGINT        NULL,                 -- prepare 단계엔 구독 미생성 -> NULL, confirm 성공 시 채움
    pg_transaction_id VARCHAR(64)   NOT NULL,              -- 토스 orderId
    amount            NUMERIC(12,0) NOT NULL,              -- 실결제금액 (원), plan.price 스냅샷
    status            VARCHAR(20)   NOT NULL,              -- READY, DONE, FAILED, CANCELED
    approved_at       TIMESTAMP     NULL,                  -- 승인 성공 시각 (READY/FAILED는 NULL)
    raw_response      JSONB         NULL,                  -- 토스 API 응답 원문
    reg_id            VARCHAR(50)   NOT NULL,
    reg_dt            TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    mod_id            VARCHAR(50)   NULL,
    mod_dt            TIMESTAMP     NULL,
    CONSTRAINT pk_payment_history PRIMARY KEY (payment_id),
    CONSTRAINT fk_payment_history_users        FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_payment_history_subscription FOREIGN KEY (subscription_id) REFERENCES subscription (subscription_id),
    CONSTRAINT uq_payment_history_pg_transaction_id UNIQUE (pg_transaction_id)
);

COMMENT ON TABLE  payment_history IS '결제 이력 (토스페이먼츠 승인 요청/결과)';
COMMENT ON COLUMN payment_history.payment_id        IS '결제이력아이디';
COMMENT ON COLUMN payment_history.user_id           IS '회원아이디 (FK)';
COMMENT ON COLUMN payment_history.subscription_id   IS '구독아이디 (FK, 승인 성공 전에는 NULL)';
COMMENT ON COLUMN payment_history.pg_transaction_id IS 'PG 거래아이디 (토스 orderId, 멱등키)';
COMMENT ON COLUMN payment_history.amount            IS '결제금액 (원 단위, 승인 시점 스냅샷)';
COMMENT ON COLUMN payment_history.status            IS '결제상태 (READY/DONE/FAILED/CANCELED)';
COMMENT ON COLUMN payment_history.approved_at       IS '결제승인일시';
COMMENT ON COLUMN payment_history.raw_response      IS '토스 API 응답 원문 (JSON)';
COMMENT ON COLUMN payment_history.reg_id            IS '등록아이디';
COMMENT ON COLUMN payment_history.reg_dt            IS '등록일시';
COMMENT ON COLUMN payment_history.mod_id            IS '변경아이디';
COMMENT ON COLUMN payment_history.mod_dt            IS '변경일시';
