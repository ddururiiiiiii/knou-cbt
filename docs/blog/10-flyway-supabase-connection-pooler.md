# Flyway + Supabase 커넥션 풀러 충돌 해결기

> **이 글은 초안입니다.** 아래 내용을 저장소의 git 히스토리와 코드에서 직접 찾으려 했으나, **관련 흔적을 전혀 찾지 못했습니다.** `git log -S "prepareThreshold"`, `-S "prepared statement"`, `-S "pgbouncer"`로 전체 히스토리를 검색해도 매치가 없고, datasource(Hikari) 설정에도 이 에러와 직접 관련된 파라미터가 없습니다. 이 글은 실제로 겪었던 사람의 기억(정확한 에러 메시지, 발생 시점, 어떻게 고쳤는지)이 있어야만 완성할 수 있습니다. 아래는 일반적으로 알려진 문제 패턴을 바탕으로 만든 뼈대이며, **실제 이 프로젝트에서 있었던 일과 세부사항이 다를 수 있습니다.**

## 알려진 문제 패턴 (일반론)

Supabase는 두 가지 연결 방식을 제공한다.

- **Direct Connection**: DB에 직접 연결. 동시 커넥션 수 제한이 낮음(요금제별로 다름).
- **Connection Pooler (PgBouncer, Transaction 모드)**: 여러 클라이언트의 커넥션을 풀링해서 재사용. 동시 커넥션 수 제약을 완화해줌.

Flyway나 JPA/MyBatis 같은 JDBC 기반 도구는 기본적으로 **서버 사이드 prepared statement**를 적극 활용한다. 문제는 PgBouncer의 Transaction 모드는 "트랜잭션이 끝나면 커넥션을 다른 클라이언트에게 재할당"하는 구조라서, 한 커넥션에서 준비한 prepared statement가 다음 트랜잭션에서 재사용될 때 **다른 클라이언트가 이미 같은 이름의 statement를 준비해놓은 상태와 충돌**할 수 있다. 이때 흔히 나는 에러가:

```
ERROR: prepared statement "S_1" already exists
```

이런 류다. 해결책으로 흔히 언급되는 것은 JDBC URL에 `prepareThreshold=0`을 추가해서 서버 사이드 prepared statement 캐싱 자체를 끄는 것, 또는 Flyway 같은 마이그레이션 도구만큼은 Pooler가 아니라 Direct Connection을 쓰도록 분리하는 것이다.

## 빈칸 — 이 프로젝트에서 실제로 있었던 일

- [ ] **정확한 에러 메시지 전문** — `already exists` 계열이 맞았는지, 다른 메시지였는지
- [ ] **언제 발생했나** — Flyway 마이그레이션 실행 중이었는지, 애플리케이션 일반 쿼리 중이었는지
- [ ] **재현 조건** — 매번 나던 문제였는지, 특정 상황(배포 직후, 동시 요청 몰릴 때 등)에서만 나던 문제였는지
- [ ] **실제로 어떻게 고쳤나**
  - JDBC URL에 `prepareThreshold=0`을 추가했는지
  - Supabase 대시보드에서 Pooler 대신 Direct Connection 문자열로 바꿨는지
  - Hikari의 `maximum-pool-size`나 다른 설정을 조정했는지
  - 그 외 다른 방법
- [ ] **지금 코드/설정엔 왜 흔적이 안 남아있나** — Render 환경변수(대시보드에서만 관리, git에 없음)로만 해결했을 가능성이 있음. 만약 그렇다면 실제 연결 문자열 형식(Pooler URL vs Direct URL)을 운영 문서에 별도로 남겨둘 필요가 있음

## 이 정도까지만 정리한 이유

내부 운영 문서(`docs/writing-topics.md`)에도 이 주제는 "증상/에러메시지 기억 필요, 미착수"로 표시돼 있었다. 코드 근거가 없는 걸 있는 것처럼 꾸며서 쓰기보다는, 정직하게 뼈대만 세워두고 실제 기억을 채워 넣는 쪽이 나중에 다시 이 문제를 만났을 때(혹은 다른 사람이 비슷한 문제를 검색해서 이 글을 찾아왔을 때) 더 신뢰할 수 있는 글이 될 것이다.

## 교훈 (임시)

- [ ] 실제 원인과 해결 과정이 채워지면, "관리형 Postgres의 Pooler 방식과 JDBC 도구의 prepared statement 캐싱이 부딪힐 수 있다"는 일반 원칙을 이 프로젝트의 구체적 사례로 보여주는 글이 될 수 있다. 지금은 그 구체적 사례가 비어있는 상태다.
