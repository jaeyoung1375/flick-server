# 변경 이력 (flick-server Changelog)

형식: `YYYY-MM-DD — 변경 내용 (근거: 커밋/작업)`

- 2026-08-09 — `docs/ai/` 하네스 문서 체계 신설 (기존에 잘못 섞여 있던 A-RMS/Backend-Core용 문서를 제거하고 flick-server 실제 스택 기준으로 01~13 재작성). 커밋 이력상 코드 변경 자체는 아래 참고.
- 2026-08-13 — `GET /admin/exercises` 서버 사이드 페이징 추가: `pageNum`(기본 1)·`pageSize`(기본 15) 파라미터 신설, `PageHelper.startPage()` 최초 사용, 응답 `data`를 `List<ExerciseResponseDto>` → `PageResponseDto<ExerciseResponseDto>`로 변경. Mapper/XML은 변경 없음(PageHelper가 다음 select를 인터셉트).
- 2026-08-17 — 구글 소셜 로그인 분기 구현: `SocialOauth2SuccessHandler`의 `case GOOGLE -> {}`가 비어 있어 `providerUserId`/`email`/`name`이 빈 값으로 넘어가던 문제 수정. `auth/dto/GoogleUserResponse`(구글 OIDC 표준 클레임 `sub`/`email`/`name`) 신설, 카카오 분기와 동일하게 `ObjectMapper.convertValue()`로 매핑. `case GITHUB`는 이번 범위 아님(여전히 빈 값).
- 2026-09-08 — Spring AI(OpenAI 연동) 의존성·최소 설정 추가. **컨트롤러/서비스/매퍼는 이번 범위 아님(다음 작업).**
  - `build.gradle`: `org.springframework.ai:spring-ai-bom:2.0.1`을 `dependencyManagement`로 임포트, `org.springframework.ai:spring-ai-starter-model-openai` 추가.
  - `application.yml`: `spring.ai.model.*`로 `chat`만 활성화(나머지 embedding/image/moderation/audio는 `none`), `spring.ai.openai.chat.options.model: gpt-4o-mini`(임시 기본값) 추가.
  - `application-secret.yml`(gitignore 대상): `spring.ai.openai.api-key: ${OPENAI_API_KEY:sk-not-set}` 플레이스홀더 추가. 실제 키 값 없음.
  - 신규 문서: [`06_domain_playbooks/ai-chat.md`](../06_domain_playbooks/ai-chat.md)(계획 단계 — 개인화 원칙, 비즈니스로직/RAG/Function Calling 역할 분리, 벡터스토어 위치 미정).
  - `02_tech_stack`·`12_known_issues`에 Spring AI 관련 항목 반영(Boot 4.1 POM 메타데이터 이슈, 기동 시 API 키 필수 이슈).
  - 검증: `./gradlew build`(테스트 포함) 통과 확인.
- 2026-09-08 — AI 챗봇 개인화 추천(Function Calling) 엔드포인트 구현. **일반 지식 RAG·벡터스토어는 이번 범위 아님(계획 유지).**
  - 신규 도메인 `kr.co.flick.aichat.{controller,service,mapper,tool,dto}`: `POST /ai-chat/ask`(로그인 필요, 요청 `{question}` → 응답 `{answer}`).
  - `AiChatService`가 공용 `ChatClient` 빈(`kr.co.flick.configuration.ChatClientConfig` 신설)에 시스템 프롬프트 + `WorkoutRecommendationTool`을 `.tools(...)`/`.toolContext(Map.of("userId", ...))`로 등록해 Function Calling 오케스트레이션(Spring AI 2.0.1 실측 API: `org.springframework.ai.tool.annotation.Tool`, `ChatClient.ChatClientRequestSpec.tools/toolContext`).
  - `WorkoutRecommendationTool.getNextWorkoutPart`: `AiChatMapper`(신규, `resources/mapper/aichat/AiChat-mapper.xml`)로 최근 `WORKOUT_RECORD`의 `EXERCISES.BODY_PART_CD` 조회 → 고정 4단계 분할(`하체→등/이두→어깨/코어→가슴/삼두`, `RecommendedExercise-mapper.xml`에 이미 문서화된 부위코드 조합 재사용)로 다음 추천 부위 계산. 운동기록이 없는 신규 유저는 에러가 아니라 첫 분할 추천으로 처리.
  - 일반 지식 질문은 도구 호출 없이 LLM 자체 지식으로 답변(RAG 미구현 — 시스템 프롬프트로 분기 지시). 신규 `AiChatErrorCode`는 만들지 않음(근거는 [`06_domain_playbooks/ai-chat.md`](../06_domain_playbooks/ai-chat.md) 3-3절).
  - 문서 갱신: `06_domain_playbooks/ai-chat.md`(실측/계획 구분 갱신), `09_api_contract`(`/ai-chat/ask` 추가), `02_tech_stack`(ChatClient/Tool API 실측), `03_directory_structure`(`aichat/` 패키지 추가).
  - 검증: `./gradlew build`(테스트 포함, 더미 API 키로 컨텍스트 로딩 확인) 통과. 실제 OpenAI 호출 테스트는 실제 키 설정 후 사용자가 직접 확인 필요.
- 2026-09-09 — motive-server에서 이식된 운동 관련 도메인 전체 제거(flick는 채용정보 서비스라 무관한 패키지).
  - 삭제: `kr.co.flick.{workout,exercise,fitness,recommend,aichat}`, `admin/exercise`, `configuration/ChatClientConfig`, `common/code/{Workout,Fitness,Exercise}ErrorCode`, 대응 `resources/mapper/**`, `resources/ddl/{workout_record,recommended_exercise*}.sql`.
  - `build.gradle`: `spring-ai-starter-model-openai`·`spring-ai-bom` 의존성 제거(전적으로 aichat 전용이었음).
  - `application.yml`/`application-secret.yml`: `spring.ai.*` 설정 제거.
  - `AuthController`: 온보딩 여부(`FitnessProfileMapper.existsProfile`)로 판정하던 `onboardingCompleted` 필드를 `TokenResponseDto`/`MobileTokenResponseDto`에서 완전히 제거(flick 고유 온보딩 개념 아직 없음 — 필요해지면 새로 설계).
  - 문서 갱신: `02_tech_stack`(AI 절 제거), `03_directory_structure`, `04_coding_standards`, `08_domain_glossary`, `09_api_contract`, `10_data_model`, `12_known_issues`(#10·#11 제거, #0·#5 갱신). `06_domain_playbooks/workout.md`·`ai-chat.md` 삭제.
  - 검증: `./gradlew compileJava` 통과.

## 참고: git 커밋 이력 요약 (2026-08-09 기준, `git log --oneline`)

- `c1481c5` 프로필 운동기록 조회 서비스 추가
- `0886aa8` 온보딩 프로필 이미지 추가 및 운동 목록 조회 추가
- `4ae42e4` 관리자 공통코드 관리 서비스 및 온보딩 서비스 추가
- `739a1f4` 소셜로그인 구현
- `0fdf8e0` first commit

> 이후 변경부터는 이 문서에 직접 append한다. git log 재요약을 여기 반복하지 않는다(git이 정본).

## FLICK-SERVER 포크 이후

- 2026-09-08 — `motive-server`를 `FLICK-SERVER`로 복제해 새 프로젝트 시작(build/.gradle/.idea 제외, 패키지 `kr.co.motive`→`kr.co.flick`·프로젝트명 `motive-server`→`flick-server` 일괄 변경, 새 git 저장소로 초기화). Oracle 스키마명(`MOTIVE`)·`motive-ui`/`motive-toy` 등 외부 연동 식별자는 실제 인프라 값이라 변경하지 않음.
- 2026-09-08 — flick-server의 실제 목적이 "여러 채용사이트 공고를 통합 조회하는 서비스"임을 확인하고 `docs/ai` 하네스를 갱신: [`01_project_overview/guide.md`](../01_project_overview/guide.md)에 목적 기재, `flick-server-overview.md`·`harness_engineering.md`·`08_domain_glossary`·`06_domain_playbooks/guide.md`·`09_api_contract`·`10_data_model`에 "현재 코드는 motive-server 레거시이며 채용정보 도메인 미구현" 경고 추가, `12_known_issues`에 도메인 불일치를 0번 이슈로 등록. 채용정보 도메인 자체는 아직 설계·구현 전(이번 범위 아님).
- 2026-09-13 — 채용정보(잡 어그리게이터) 도메인 구현 → 같은 날 서비스 정체성을 **스트리밍(OTT) 백엔드**로 최종 확정하며 전부 제거.
  - 구현했던 것: `kr.co.flick.jobposting.{controller,crawler.jobkorea,dto,enums,mapper,service}`(잡코리아 크롤러 `JobKoreaFetcher`/`JobKoreaParser` 포함), `configuration/CrawlerRestClientConfig`, `resources/mapper/jobposting/{Crawler,JobPosting}-mapper.xml`, DDL 3종(`job_posting_career_cd_ddl.sql`·`job_posting_location_cd_ddl.sql`·`job_posting_company_name_nullable_ddl.sql`), `build.gradle`의 `org.jsoup:jsoup` 의존성, 테스트(`jobposting/crawler/jobkorea/JobKoreaParserTest` 등), DB: `JOB_POSTING` 테이블(+`CAREER_MIN_YEAR` 컬럼, `COMPANY_NAME` nullable 제약 완화), 공통코드 그룹 `JOB_CAREER_CD`·`JOB_LOCATION_CD`(`CMM_CODE`/`CMM_CODE_DTL`).
  - 같은 날 스트리밍 백엔드로 정체성 확정 후 위 전부를 삭제: 소스·매퍼 XML·DDL 파일·테스트·`jsoup` 의존성 제거, DB는 `CMM_CODE_DTL`→`CMM_CODE`(`JOB_CAREER_CD`/`JOB_LOCATION_CD`) 행 삭제 후 `JOB_POSTING` 테이블을 `DROP TABLE ... CASCADE CONSTRAINTS`로 삭제(사용자가 직접 SQL 실행, `resources/ddl/job_posting_code_cleanup_dml.sql`에 실행한 DML 보관).
  - 문서 갱신: `01_project_overview`(guide·overview), `harness_engineering.md`, `06_domain_playbooks/{guide,admin-code,auth}`, `08_domain_glossary`, `09_api_contract`(모바일 인증 엔드포인트 3종 `/auth/refresh/mobile`·`/auth/exchange`·`/auth/logout` 문서 누락도 함께 보강), `10_data_model`, `05_prompt_templates/guide.md`(motive-ui 잔재 표현 정리), `06_test_strategy`(테스트 파일 수 갱신), `12_known_issues`(#0 갱신, #8 정정 — CORS가 `app.frontend-url`과 별도로 하드코딩된 실제 버그 발견, #10 신규 — `AuthMapper.updateProfileInfo`/`updateProfileFileId` 죽은 코드).
  - 검증: 문서 갱신 위주 작업이라 별도 빌드 검증 없음(코드 변경분은 삭제뿐이라 컴파일에 영향 없음 확인 — `jobposting`/`jsoup` 참조 전체 grep으로 잔여 없음 확인).