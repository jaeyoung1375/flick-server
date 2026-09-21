# 이용권 결제 (토스페이먼츠 테스트 모드) TODO

## 1. 데이터 모델
- [ ] `Plan`: id, name(월권/연간권), price, duration_days, has_ads
- [ ] `Subscription`: id, user_id, plan_id, start_at, end_at, status(ACTIVE/EXPIRED/CANCELLED)
- [ ] `PaymentHistory`: id, user_id, subscription_id, pg_transaction_id(orderId), amount, status, approved_at, raw_response

## 2. 토스페이먼츠 연동
- [ ] 테스트 API 키 발급 (클라이언트 키 / 시크릿 키)
- [ ] 결제창(또는 결제위젯) 연동 (프론트)
- [ ] 결제 승인 API 호출 (서버 → 토스 `confirm`), 금액은 클라이언트 값 신뢰하지 않고 서버가 `Plan` 가격으로 재검증
- [ ] 결제 실패/취소 처리 흐름

## 3. 백엔드 API
- [ ] `GET /plans` — 이용권 목록 조회
- [ ] `POST /payments/prepare` — 결제 준비(orderId 발급)
- [ ] `POST /payments/confirm` — 결제 승인 콜백 처리 (토스 confirm 호출 + Subscription 생성/갱신 + PaymentHistory 저장)
- [ ] `GET /subscriptions/me` — 내 구독 상태 + 광고 노출 여부 조회

## 4. 만료 처리
- [ ] 만료된 Subscription 상태 갱신 (스케줄러 배치 또는 조회 시점 lazy 계산 — 방식 선택 필요)
- [ ] (선택) 만료 임박 알림

## 5. 광고 노출 연동
- [ ] 활성 Subscription 존재 여부 + `Plan.has_ads` 기준으로 광고 노출 플래그를 API 응답에 포함
- [ ] 프론트(flick-ui)에서 플래그로 광고 조건부 렌더링 — 범위/우선순위는 별도 확인 필요

## 6. 보안 / 검증
- [ ] 결제 금액 서버 재검증 (클라이언트 조작 방지)
- [ ] 동일 orderId 중복 요청 방지 (멱등성)
- [ ] (선택) 토스 웹훅 수신 처리

## 7. 지금은 범위 제외 (추후)
- [ ] 빌링키 기반 자동 정기결제 전환
- [ ] 결제 실패 자동 재시도/알림
- [ ] 사업자등록 + 실 PG 계약 전환
