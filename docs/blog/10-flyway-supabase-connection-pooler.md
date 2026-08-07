# Flyway + Supabase 커넥션 풀러 충돌 해결기

## 문제

Manual Deploy로 밀려있던 마이그레이션 V2~V4를 한 번에 적용하려던 중, 배포가 실패했다. 로그에 남은 에러는 이랬다.

```
FlywaySqlException: prepared statement "S_2" already exists
(SQLState 42P05)
```

이상한 건, V1 마이그레이션 하나만 적용할 땐 이 문제가 전혀 없었다는 점이다. 문제는 **한 번에 적용해야 할 마이그레이션 쿼리 수가 늘어나면서** 나타났다.

## 원인

Supabase는 Direct Connection 외에 **Connection Pooler**(포트 6543, PgBouncer 기반 Transaction 모드)를 제공한다. Transaction 모드의 풀러는 "트랜잭션이 끝나면 그 커넥션을 다른 클라이언트에게 곧바로 재할당"하는 방식으로 동작한다.

문제는 JDBC 드라이버가 기본적으로 **서버 사이드 prepared statement**를 적극적으로 캐싱한다는 점이다. 한 커넥션에서 이름 붙여 준비해둔 prepared statement(`S_2`)가, Transaction 모드에서 그 커넥션이 다른 클라이언트에게 넘어간 뒤 그 클라이언트가 우연히 같은 이름으로 새 statement를 준비하려 하면 충돌이 난다. V1 하나만 적용할 땐 쿼리 수가 적어서 우연히 이 충돌 조건을 안 밟았을 뿐이고, V2~V4를 한 번에 밀어 넣으면서 쿼리 수가 늘어나자 충돌 확률이 올라가 실제로 터진 것이다.

## 해결

Render 환경변수의 `SPRING_DATASOURCE_URL` 뒤에 JDBC 파라미터를 하나 추가했다.

```
...supabase.com:6543/postgres?prepareThreshold=0
```

(이미 다른 쿼리 파라미터가 있으면 `&prepareThreshold=0`으로 이어붙인다.)

`prepareThreshold=0`은 PostgreSQL JDBC 드라이버에게 "서버 사이드 prepared statement 캐싱을 아예 쓰지 마라"고 지시하는 옵션이다. 매 쿼리를 그때그때 파싱하게 되니 아주 미세하게 성능 손해는 있지만, PgBouncer Transaction 모드처럼 커넥션이 클라이언트 사이에서 계속 돌려쓰이는 환경에서는 이 손해보다 "충돌 없이 안정적으로 동작하는 것"이 훨씬 중요하다.

적용 후 재배포에서 성공했고, `flyway_schema_history` 테이블에 V1~V4가 전부 `success=true`로 찍힌 걸 확인했다.

## 확인 포인트

- 이후 새 마이그레이션(V5 이상)을 추가할 때도 이 문제가 재발할 수 있으니, `SPRING_DATASOURCE_URL`에 `prepareThreshold=0`이 계속 남아있는지 배포 전에 확인이 필요하다. Render 환경변수라 git에는 흔적이 남지 않기 때문에, 코드만 보고는 이 설정이 있는지 알 수 없다 — 운영 문서에 별도로 기록해둬야 하는 이유다.
- Direct Connection으로 마이그레이션만 따로 돌리는 방법도 이론적으로는 가능하지만, 운영 환경 자체가 Pooler 연결 하나로 통일돼 있어서 지금은 `prepareThreshold=0` 쪽이 훨씬 간단한 해결책이었다.

## 교훈

- 관리형 Postgres의 Connection Pooler(특히 Transaction 모드)와 JDBC 계열 도구(Flyway, MyBatis, JPA 등)의 prepared statement 캐싱은 구조적으로 부딪힐 수 있다. "쿼리 수가 적을 땐 안 나다가 늘어나면 나는" 것처럼 보이는 간헐적 에러라면 이 조합을 의심해볼 만하다.
- 이런 종류의 설정은 코드가 아니라 배포 플랫폼의 환경변수로만 존재하기 쉽다. 코드 리뷰만으로는 절대 드러나지 않으니, "왜 이 값이 필요한지"를 코드 밖의 운영 문서에 남겨두지 않으면 나중에 새로 마이그레이션을 추가하는 사람(미래의 나 포함)이 똑같은 에러를 다시 겪게 된다.
