# 테스트 전략 (jobmoa-server Test Strategy)

## 1. 현재 상태 (2026-08-09 실측)

* `src/test/java/kr/co/jobmoa/JobmoaServerApplicationTests.java` 1개뿐 — Spring Context 로드 확인용 기본 생성 테스트. **도메인 로직 테스트 없음.**
* `build.gradle`에 `jacoco` 플러그인이 설정되어 있고 `jacocoTestReport` 태스크가 존재하지만, 실질 커버리지를 낼 테스트가 없어 현재는 형식적 상태.
* CI(`backend.yml`)는 `./gradlew build -x test`로 **테스트를 건너뛰고 빌드**한다 — 배포 파이프라인이 테스트 결과에 의존하지 않는다.

## 2. 원칙 (테스트를 추가할 때)

* 새 서비스 로직을 추가/변경하면 최소한 해당 `*Service`에 대한 단위 테스트를 함께 작성하는 것을 권장한다(강제 게이트는 아직 없음 — 이 저장소 관례가 아직 없으므로 강제하지 않는다. 팀 방침이 서면 이 문서를 갱신).
* MyBatis mapper 테스트는 `testImplementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter-test'`가 이미 의존성에 있다 — 실제 Oracle 대신 테스트 프로파일/인메모리 대체 여부는 아직 정해지지 않았다(가정 금지, 실제로 필요하면 먼저 확인).
* 인증이 필요한 컨트롤러 테스트는 `SecurityUtil.getUserId()`가 `SecurityContextHolder`를 직접 참조하므로, MockMvc 테스트 시 `Authentication`을 수동으로 채워 넣어야 한다(`UsernamePasswordAuthenticationToken(userId, null, List.of())` 형태 — `JwtAuthenticationFilter` 실측 참고).

## 3. 갱신 규칙

* 테스트 스위트가 실제로 생기면 이 문서의 "현재 상태"를 갱신하고, CI의 `-x test` 플래그를 제거하는 논의를 함께 기록한다.