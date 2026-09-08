# 제출 전 자가검토 체크리스트 (jobmoa-server)

## 계층·구조
- [ ] `controller`→`service`→`mapper` 3계층을 지켰는가 (컨트롤러/서비스에서 직접 SQL·JPA 없음)
- [ ] `@RestController`에 `/api/v1` prefix를 직접 쓰지 않았는가 (WebConfig가 자동 부여)
- [ ] 새 도메인이면 `controller/service/mapper/dto` 4개 패키지를 갖췄는가

## 인증
- [ ] 로그인 필요 API에서 `SecurityUtil.getUserId()`로 사용자를 식별했는가
- [ ] 다른 사용자의 리소스 ID(예: `workoutRecordId`)를 받는 API라면 소유권(`USER_ID` 일치)을 서비스 레이어에서 검증했는가 (IDOR 방지)
- [ ] 관리자 전용 API(`/admin/**`)를 추가했다면, 현재 role 인가가 없다는 한계([`12_known_issues`](../12_known_issues/jobmoa-server-known-issues.md))를 인지하고 있는가

## MyBatis
- [ ] NUMBER 컬럼에 null을 바인딩할 가능성이 있는 파라미터에 `jdbcType=NUMERIC`을 명시했는가
- [ ] 신규 테이블 PK 채번을 `<테이블명>_SEQ` + `<selectKey order="BEFORE">` 패턴으로 했는가
- [ ] Mapper XML을 `resources/mapper/<domain>/`에 두고 네임스페이스가 Java 인터페이스와 일치하는가

## 응답·에러
- [ ] 모든 컨트롤러 메서드가 `ApiResponse<T>`를 반환하는가
- [ ] 실패를 `CustomException(ErrorCode)`로만 던지고 `GlobalExceptionHandler` 경로를 따르는가
- [ ] 신규 에러코드가 `common/code/<Domain>ErrorCode.java` 패턴(기존 코드 접두어와 충돌하지 않는 새 접두어)을 따르는가

## 시크릿·설정
- [ ] 시크릿·OAuth2 클라이언트 값을 코드나 `application.yml`/`application-local.yml`에 평문으로 넣지 않았는가 (`application-secret.yml`만)
- [ ] 새 의존성을 추가했다면 [`02_tech_stack`](../02_tech_stack/jobmoa-server-tech-stack.md)에도 반영했는가

## 문서 동기화
- [ ] API 계약이 바뀌었다면 [`09_api_contract`](../09_api_contract/jobmoa-server-api-contract.md)를 갱신했는가
- [ ] 테이블/컬럼이 바뀌었다면 [`10_data_model`](../10_data_model/jobmoa-server-data-model.md)을 갱신했는가
- [ ] 변경 사항을 [`11_changelog`](../11_changelog/jobmoa-server-changelog.md)에 남겼는가
- [ ] 새로 발견한 함정이 있다면 [`12_known_issues`](../12_known_issues/jobmoa-server-known-issues.md)에 남겼는가