# Render + Supabase 조합으로 개인 프로젝트 배포하기

## 왜 이 조합인가

인프라 경험이 거의 없는 상태에서 1인 개발로 서비스를 배포해야 했다. 서버를 직접 관리(EC2 등)하는 건 처음부터 선택지에서 뺐다 — OS 패치, 방화벽, 프로세스 감시까지 혼자 챙길 자신이 없었다. 결론은 "코드만 push하면 알아서 빌드하고 띄워주는" PaaS(Render)와 "DB를 직접 운영하지 않아도 되는" BaaS 성격의 관리형 Postgres(Supabase)의 조합이었다.

## 구성 — Dockerfile 2단계 빌드

Render는 저장소에 `Dockerfile`이 있으면 그걸 그대로 빌드해서 배포한다. 별도의 `render.yaml` 같은 IaC 파일은 안 쓰고, 대시보드에서 직접 서비스를 만들고 환경변수를 등록하는 방식으로 시작했다 — 서비스가 하나뿐인 규모에서는 코드로 인프라를 관리하는 오버헤드가 오히려 손해라고 판단했다.

```dockerfile
# 1단계: 빌드
FROM gradle:8.7-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# 2단계: 실행
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/build/libs/knou-cbt-0.0.1-SNAPSHOT.jar app.jar
RUN useradd --system --no-create-home appuser
USER appuser
ENTRYPOINT ["java", "-jar", "app.jar"]
```

빌드용 이미지(Gradle + JDK 풀세트)와 실행용 이미지(JDK만)를 분리했다. 빌드 도구가 실제 운영 이미지에 남아있을 이유가 없기 때문이다. 나중에 컨테이너 하드닝을 하면서 `USER appuser`도 추가했다 — 기본값인 root로 컨테이너를 돌리는 건 굳이 감수할 필요 없는 리스크였다.

## 헬스체크 — Render가 "이 서비스가 살아있나?"를 판단하는 기준

Render는 배포 후 헬스체크 엔드포인트에 주기적으로 요청을 보내서, 실패하면 새 배포를 롤백하거나 인스턴스를 재시작한다. 이 프로젝트에서는 `/health` 엔드포인트가 단순히 "서버 프로세스가 떠 있나"가 아니라 **실제로 DB에 쿼리를 날려보고** 응답하도록 만들었다.

```java
// HealthCheckController
// HealthCheckMapper.healthCheck()로 DB에 실제 쿼리를 날려보고
// 성공하면 200/UP, 실패하면 503/DOWN 반환
```

이렇게 하면 "애플리케이션은 떠 있지만 DB 연결이 끊긴" 상태도 장애로 감지된다. 프로세스만 살아있는 것과 서비스가 실제로 동작하는 것은 다른 얘기라서, 헬스체크는 후자를 확인해야 의미가 있다.

## 데이터베이스 전환 — MySQL에서 PostgreSQL/Supabase로

초기엔 MySQL로 시작했다가, Phase 0/1 정리 시점에 PostgreSQL 기반 Supabase로 옮겼다. 로컬 개발용 프로필(`application-local.yml`)도 이때 새로 만들어서, 로컬 Postgres(`localhost:5432`)와 운영 Supabase를 프로필로 명확히 분리했다.

커넥션 풀 설정도 신경 썼다.

```yaml
hikari:
  maximum-pool-size: 5
  minimum-idle: 0
  initialization-fail-timeout: -1
```

무료/저가형 티어의 관리형 DB는 동시 커넥션 수 제한이 빡빡한 경우가 많다. `maximum-pool-size`를 낮게 잡아둔 것도 이런 제약을 의식한 선택이다. (참고: Supabase는 요금제에 따라 Direct Connection과 Transaction Pooler 연결 방식이 다르고, 이 둘을 헷갈리면 별도의 커넥션 관련 이슈로 이어질 수 있다 — 이 부분은 별도 글에서 다룬다.)

## 파일 저장 — Supabase Storage

이미지 업로드(문제 지문, 보기, 공지사항 첨부파일)는 서버의 로컬 디스크가 아니라 Supabase Storage로 보낸다. Render 같은 PaaS는 배포될 때마다 컨테이너가 새로 뜨는 구조라, 로컬 디스크에 저장한 파일은 다음 배포에서 통째로 사라진다. 처음부터 "서버는 상태를 갖지 않는다(stateless)"는 전제를 세워야 이런 사고를 피할 수 있다.

## 겪은 것들

- 서비스가 아무리 작아도 "빌드용 이미지와 실행용 이미지 분리", "non-root 유저 실행", "stateless 파일 저장"은 처음부터 챙겨두는 게 나중에 훨씬 편하다. 나중에 고치려면 이미 쌓인 데이터/설정을 마이그레이션해야 하는 비용이 생긴다.
- 관리형 서비스(Render, Supabase)를 쓰면 인프라 지식이 없어도 배포 자체는 어렵지 않다. 다만 "대시보드에서 수동으로 설정한 값들"(환경변수, 커넥션 문자열, 헬스체크 경로 등)은 코드에 안 남기 때문에, 별도로 운영 문서에 기록해두지 않으면 나중에 "이 값을 왜 이렇게 넣었더라" 하고 잊어버리기 쉽다.

## 교훈

인프라 경험이 없는 상태에서 배포를 시작하는 가장 현실적인 방법은, 관리해야 할 대상을 최대한 줄이는 것이었다. 서버 OS도, DB 서버도 직접 관리하지 않고, Dockerfile 하나와 환경변수 목록만 잘 챙기면 배포가 되는 구조를 만드는 데 집중했다. 그 대가로 "대시보드에만 있는 설정"들이 생기는데, 이건 코드 리뷰만으로는 못 잡으니 별도로 운영 체크리스트를 만들어서 관리해야 한다.
