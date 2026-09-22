# 배포 런북 상세 (flick-server Deploy Runbook)

실측: `.github/workflows/backend.yml` (2026-08-09 기준)

## 1. 로컬 실행

```bash
./gradlew bootRun   # local 프로파일, 포트 9090
./gradlew test --tests "kr.co.flick.SomeServiceTest"
```

사전 조건: `C:/wallet`에 Oracle TNS wallet 존재(`application-local.yml`), 로컬 Redis(`localhost:6379`) 기동.

## 2. 빌드

```bash
./gradlew build              # 테스트 포함
./gradlew build -x test      # CI와 동일 (테스트 스킵)
```

## 3. CI/CD 파이프라인 (`.github/workflows/backend.yml`)

* **트리거:** `workflow_dispatch` (수동 실행만 — push/PR 자동 트리거 없음)
* **러너:** `self-hosted`
* **단계:**
  1. `actions/checkout@v4`
  2. `chmod +x gradlew`
  3. `./gradlew build -x test --build-cache`
  4. `cp build/libs/flick-server-0.0.1-SNAPSHOT.jar /opt/app/app.jar`
  5. `sudo systemctl restart app.service`

**주의:** 산출물 jar 이름(`flick-server-0.0.1-SNAPSHOT.jar`)이 `build.gradle`의 `version`에 의존한다 — 버전을 바꾸면 workflow의 `cp` 경로도 함께 갱신해야 한다(현재 하드코딩).

## 4. 배포 환경

* 대상 서버에 systemd 서비스 `app.service`가 사전 구성되어 있고, `/opt/app/app.jar`를 실행하는 것으로 추정(서비스 유닛 파일은 이 저장소에 없음 — 서버 쪽 구성).
* 활성 프로파일은 `application.yml`의 `profiles.active: local` 기본값을 배포 시 어떻게 오버라이드하는지 이 저장소만으로는 확인 불가(`dev` 프로파일 파일은 존재) — 실제 배포 환경 변수/커맨드라인 인자는 서버 설정을 직접 확인할 것.

## 5. 롤백

이 저장소·워크플로우에 자동 롤백 절차가 없다. 이전 커밋으로 workflow를 재실행(재빌드+재배포)하는 수동 방식만 가능하다.

## 6. 배포 전 체크

- [ ] `application-secret.yml`이 대상 서버에 이미 배치되어 있는지 확인 (git에 없으므로 CI가 배포하지 않음 — 서버에 별도 유지되는 파일로 추정)
- [ ] Oracle wallet·Redis 접속 정보가 배포 대상 프로파일(`local`/`dev`)에 맞는지 확인
- [ ] `build.gradle`의 `version`이 workflow의 jar 파일명과 일치하는지 확인