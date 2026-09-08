# 플레이북 · 운동 기록 기반 AI 챗봇 (AI Chat)

> 이 문서는 **실측(구현됨)**과 **계획(미구현)**을 구분해서 적는다. 2026-09-08 기준 **개인화 추천(Function Calling) 경로는 구현됨.** 일반 지식 RAG·벡터스토어는 여전히 계획 단계다. 관련 패키지: `kr.co.jobmoa.aichat.{controller,service,mapper,tool,dto}`.

## 1. 왜 만드는가 (개인화가 핵심 가치) — 설계 원칙(불변)

일반 LLM(ChatGPT/Claude 등)은 "오늘 어디 운동하지?" 같은 질문에 답할 수 없다 — 사용자의 실제 운동 기록을 모르기 때문이다. jobmoa는 이미 사용자의 운동 기록(`WORKOUT_RECORD` 3단 구조, [`workout.md`](./workout.md) 참고)을 갖고 있으므로, **그 기록을 근거로 답하는 것 자체가 차별점**이다.

## 2. 역할 분리: 비즈니스 로직 vs RAG vs Function Calling

| 질문 유형 | 예시 | 처리 방식 | 상태 |
|-----------|------|-----------|------|
| 개인화 조회 | "오늘 어디 운동하지?" | **일반 SQL 조회 + 비즈니스 로직**(RAG 아님) — LLM Function Calling으로 `getNextWorkoutPart` 도구 호출 | **구현됨** |
| 일반 지식 | "스쿼트 자세 어떻게 잡아?" | 도구 호출 없이 LLM 자체 지식으로 답변(RAG 미사용) | **구현됨(임시 방식, 4절 참고)** |
| 지식 기반 RAG | (위와 동일 질문, 사내 지식 문서 근거) | 벡터 검색 기반 RAG | **계획(미구현)**, 5절 참고 |

## 3. 개인화 추천 — 구현 내용 (실측)

### 3-1. 엔드포인트

`POST /ai-chat/ask` (로그인 필요) — [`09_api_contract`](../09_api_contract/jobmoa-server-api-contract.md) 참고. 컨트롤러는 `SecurityUtil.getUserId()`로 얻은 유저 ID만 서비스에 전달한다(다른 도메인과 동일한 소유권 관례).

### 3-2. 오케스트레이션 (`AiChatService`)

`kr.co.jobmoa.configuration.ChatClientConfig`가 만든 공용 `ChatClient` 빈에 시스템 프롬프트로 "개인화 질문이면 반드시 도구를 호출하라"를 지시하고, `WorkoutRecommendationTool` 인스턴스를 `.tools(...)`로 등록, `.toolContext(Map.of("userId", userId))`로 인증된 유저 ID를 도구에 안전하게 전달한다(LLM이 임의의 userId를 파라미터로 넣을 수 없게 — Tool 파라미터가 아니라 서버가 직접 주입하는 `ToolContext`로 전달, 다른 도메인의 IDOR 방지 관례와 동일한 목적).

Spring AI 2.0.1 실측 API(학습 데이터의 옛 API명과 다를 수 있어 소스 확인함):
```java
chatClient.prompt()
    .system(SYSTEM_PROMPT)
    .user(question)
    .tools(workoutRecommendationTool)                 // 평범한 POJO에 @Tool 메서드
    .toolContext(Map.of("userId", userId))             // ToolContext로 서버가 직접 주입
    .call()
    .content();
```
- 도구 애노테이션: `org.springframework.ai.tool.annotation.Tool`(`spring-ai-model` 모듈).
- 도구 메서드가 `org.springframework.ai.chat.model.ToolContext` 타입 파라미터를 받으면 프레임워크가 LLM에게 노출되는 JSON 스키마에서 제외하고 직접 주입한다(`MethodToolCallback.buildMethodArguments` 실측 확인) — 그래서 `getNextWorkoutPart(ToolContext toolContext)`처럼 LLM이 넘기는 인자가 하나도 없어도 동작한다.

### 3-3. 도구 구현 (`WorkoutRecommendationTool`) — 분할 순서 계산

**데이터 조회** (`AiChatMapper`, `resources/mapper/aichat/AiChat-mapper.xml`):
1. `selectLatestWorkoutRecordId(userId)` — 유저의 가장 최근 `WORKOUT_RECORD` 1건(`RECORD_DT DESC`). 없으면 `null`(신규 유저).
2. `selectBodyPartCodesByWorkoutRecordId(workoutRecordId)` — 그 기록에 포함된 `WORKOUT_RECORD_EXERCISE` → `EXERCISES.BODY_PART_CD`를 조인해 중복 제거한 코드 목록.

`WORKOUT_RECORD`/`WORKOUT_RECORD_EXERCISE`/`EXERCISES` 구조 자체는 [`workout.md`](./workout.md) 2·3절, [`10_data_model`](../10_data_model/jobmoa-server-data-model.md)이 단일 출처다 — 이 문서에서 컬럼을 다시 나열하지 않는다. `WorkoutRecordMapper`를 재사용하지 않고 별도 매퍼를 새로 둔 이유: 기존 `getWorkoutRecordList`는 부위를 **이름 문자열**(`BODY_PART_NMS`, CMM_CODE_DTL 조인)로만 반환해 분할 매칭에 쓰기 어렵고, 분할 계산에는 **코드값**이 필요했기 때문이다.

**분할 순서**(비즈니스 로직, `WorkoutRecommendationTool`의 Java 상수 `SPLIT_ORDER`):

```
하체(03) → 등/이두(02,06) → 어깨/코어(04,07) → 가슴/삼두(01,05) → (반복)
```

이 부위코드 조합은 새로 지어낸 것이 아니라 **이미 존재하는 `RecommendedExercise-mapper.xml`의 주석에 문서화된 "헬스장에서 같이 하는 부위 조합"을 그대로 재사용**한 것이다(등+이두, 가슴+삼두, 어깨+코어, 하체 — 오늘의 추천 운동 기능이 무작위 노출 그룹을 나눌 때 쓰는 것과 동일한 코드 조합). 4일 분할로 재배열한 순서(하체→등/이두→어깨/코어→가슴/삼두)만 이번에 새로 정한 것이다. **사용자별 커스텀 분할은 이번 범위 아님** — 고정 상수 배열로 충분하다고 판단했다(운동 요일 수·순서를 사용자가 바꾸는 기능이 생기면 그때 테이블화 검토).

매칭 로직: 최근 기록의 부위코드 집합과 `SPLIT_ORDER`의 각 분할 코드 집합이 겹치는(교집합이 비어있지 않은) 첫 항목을 "최근 분할"로 판정하고, 그 다음 항목(순환)을 추천한다. 여러 분할에 걸치는 기록(예: 하체+가슴을 한 번에 기록)은 배열 순서상 먼저 매칭되는 쪽을 채택한다(단순화 — 실사용 데이터로 이상하면 재검토).

**신규 유저(운동기록 없음) 처리**: 에러로 취급하지 않는다. `getNextWorkoutPart`가 `hasWorkoutHistory=false`와 함께 첫 분할("하체")을 추천값으로 반환하고, LLM이 이를 "아직 기록이 없으니 하체부터 시작해보세요" 같은 자연스러운 문장으로 만든다. 최근 기록은 있지만 부위코드가 `SPLIT_ORDER` 어디에도 매칭 안 되는 경우도 동일하게 처리(`recentSplitDay=null`, 첫 분할 추천).

**에러 코드**: 이번 도메인에는 신규 `AiChatErrorCode`를 만들지 않았다. 유효성 검증 실패(질문 공백 등)는 기존 `@Valid`+`CommonErrorCode.INVALID_INPUT` 공통 경로로 충분하고, "기록 없음"은 위처럼 에러가 아니라 정상 응답 경로로 처리했기 때문이다. OpenAI 호출 자체가 실패하면(예: 키 미설정, 네트워크 오류) 별도 처리 없이 `GlobalExceptionHandler`의 범용 `Exception` 핸들러로 떨어진다 — 이는 기존에도 문서화된 동작이다([`12_known_issues`](../12_known_issues/jobmoa-server-known-issues.md) 4번, 예외 메시지 노출).

## 4. 일반 지식 질문 — 현재 처리 방식 (임시, RAG 아님)

RAG를 넣지 않기로 한 이번 범위 결정에 따라, 시스템 프롬프트로 "개인 기록과 무관한 일반 헬스 지식 질문은 도구를 호출하지 말고 네 지식으로 답하라"고 지시했다. 즉 **"스쿼트 자세 어떻게 잡아?" 같은 질문은 LLM이 사내 지식 문서 근거 없이 스스로 아는 대로 답한다** — jobmoa 고유 데이터에 근거하지 않으므로 사실과 다를 수 있다(할루시네이션 위험, RAG 도입 전까지 감수하는 트레이드오프). "지금은 개인 기록 기반 질문만 지원합니다"처럼 명시적으로 범위를 좁히는 대신 이 방식을 택한 이유는 사용자 경험상 완전히 답을 거부하는 것보다 낫다고 판단했기 때문 — RAG 도입 시 이 프롬프트 지시를 "지식 문서 도구를 우선 호출하라"로 교체할 것.

## 5. 일반 지식 RAG — 벡터스토어 위치 미정 (계획, 변경 없음)

지식베이스(운동 자세·이론 등 문서)를 어디에 둘지 **결정되지 않았다**. 후보:

1. **Oracle `VECTOR` 타입** — jobmoa-server가 이미 Oracle을 쓰므로 별도 인프라 추가 없이 통합 가능. Spring AI의 Oracle VectorStore 지원 여부·버전은 조사하지 않았다 — 채택 전 확인 필요.
2. **별도 Postgres + pgvector** — 사용자의 별도 학습 프로젝트 `rag-chatbot-api`(Python/FastAPI)에서 이미 검증한 이력이 있음.

**채택 시**: `02_tech_stack`에 `spring-ai-starter-vector-store-*` 스타터 추가를 기록하고, 이 절을 "실측"으로 갱신하며, `AiChatService`의 시스템 프롬프트/도구 목록에 지식 검색 도구를 추가한다.

## 6. 향후 구현 계획 (남은 것)

- 벡터스토어 위치 결정 및 RAG 파이프라인 구현(5절)
- 분할 매칭이 여러 분할에 걸치는 케이스(하체+가슴을 한 세션에 기록 등) 실사용 데이터로 재검토
- 사용자별 커스텀 분할(현재는 고정 4일 분할)
- OpenAI 호출 실패 시 사용자 친화적 에러 응답(현재는 범용 500 노출 경로를 그대로 씀 — [`12_known_issues`](../12_known_issues/jobmoa-server-known-issues.md) 4번과 동일한 한계)
- `RECOMMENDED_EXERCISE`(오늘의 추천 운동, 무작위 노출)와 이 챗봇의 "다음 추천 부위"는 **서로 다른 기능**이다 — 전자는 운영자가 등록한 운동 풀에서 무작위 노출, 후자는 사용자의 실제 운동 이력 기반 분할 계산. 부위코드 조합만 재사용했을 뿐 로직은 독립적이므로 혼동하지 말 것.

---

**문서 성격:** 도메인 플레이북(구현 부분 실측, RAG는 계획).
**관련 문서:** [`workout.md`](./workout.md), [`10_data_model`](../10_data_model/jobmoa-server-data-model.md), [`02_tech_stack`](../02_tech_stack/jobmoa-server-tech-stack.md), [`09_api_contract`](../09_api_contract/jobmoa-server-api-contract.md), [`12_known_issues`](../12_known_issues/jobmoa-server-known-issues.md)
