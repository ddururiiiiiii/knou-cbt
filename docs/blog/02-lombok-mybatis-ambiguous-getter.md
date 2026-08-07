# Lombok 게터와 내가 만든 게터가 충돌해서 MyBatis가 헷갈렸던 이야기

## 문제

점검 모드(유지보수 모드) 기능을 만들어서 배포했다. 관리자 화면(`/admin/maintenance`)에서 점검 모드를 켜고 저장 버튼을 눌렀는데 — 로컬에서는 멀쩡히 되던 게 실서비스 배포 후에는 저장할 때마다 500 에러가 났다.

콘솔에는 이런 로그가 찍혔다.

```
Illegal overloaded getter method with ambiguous type for property "maintenanceEnabled"
```

"오버로드된 게터라 타입이 모호하다"니, 처음 보는 문구였다.

## 원인

설정값을 담는 `SiteOperationSetting` 클래스는 Lombok `@Getter`를 붙인 평범한 클래스였다.

```java
@Getter
public class SiteOperationSetting {
    private final Boolean maintenanceEnabled;
    // ...

    public boolean isMaintenanceEnabled() {
        return Boolean.TRUE.equals(maintenanceEnabled);
    }
}
```

Lombok `@Getter`는 `Boolean maintenanceEnabled` 필드에 대해 자동으로 `getMaintenanceEnabled()`를 만들어준다. 그런데 코드 안에는 null-safe하게 boolean만 뽑아 쓰려고 직접 작성한 `isMaintenanceEnabled()`도 같이 있었다.

즉 같은 프로퍼티(`maintenanceEnabled`)에 대해 게터가 **두 개** 존재하는 상태였다.

- Lombok이 만든 `getMaintenanceEnabled()` → 반환 타입 `Boolean`
- 내가 만든 `isMaintenanceEnabled()` → 반환 타입 `boolean`

자바 문법상으로는 전혀 문제가 없다. 컴파일도 잘 되고, 애플리케이션 코드에서 `setting.isMaintenanceEnabled()`를 호출하는 것도 아무 문제 없이 동작한다.

문제는 MyBatis였다. MyBatis는 SQL 결과를 자바 객체에 매핑할 때(또는 파라미터 객체에서 값을 읽을 때) 리플렉션 기반의 `MetaObject`/`Reflector`로 "이 프로퍼티의 게터가 뭐지?"를 찾는다. 이때 같은 프로퍼티 이름에 대해 반환 타입이 다른 게터가 두 개 있으면, MyBatis 입장에서는 **어느 쪽을 프로퍼티의 진짜 타입으로 볼지 결정할 수 없다.** 그래서 `Illegal overloaded getter method with ambiguous type`을 던지며 포기해버린다.

로컬에서는 왜 안 터졌을까 — 정확히는 로컬에서도 잠재적으로 같은 문제였지만, 저장(`update`) 경로에서 이 객체가 MyBatis 파라미터로 넘어가는 특정 케이스를 로컬 테스트 때 밟지 않았을 뿐이었다. 배포 후 실제로 저장 버튼을 눌러야만 재현되는 경로였다.

## 해결

원인을 알고 나면 수정은 간단하다. 헬퍼 메서드(`isMaintenanceEnabled()`, `isBannerEnabled()`)를 지우고, 호출부에서 Lombok이 만든 게터로 직접 null-safe 비교를 하도록 바꿨다.

```java
// Before
if (!setting.isMaintenanceEnabled()) { ... }

// After
if (!Boolean.TRUE.equals(setting.getMaintenanceEnabled())) { ... }
```

그리고 같은 사고가 다시 나지 않도록, 실제 저장→조회 라운드트립을 검증하는 테스트를 추가했다.

```java
@Test
void updateAndFindRoundTrip() {
    SiteOperationSetting setting = SiteOperationSetting.of(
            1L, true, "테스트 메시지", null, false, "", null, null
    );
    mapper.update(setting);

    SiteOperationSetting found = mapper.find();
    assertThat(found.getMaintenanceEnabled()).isTrue();
    assertThat(found.getMaintenanceMessage()).isEqualTo("테스트 메시지");
}
```

## 교훈

- Lombok `@Getter`와 직접 만든 편의 메서드는, 이름만 안 겹치면(`getX()` vs `isXEnabled()` 같은 식으로) 아무 문제가 없어 보이지만, **같은 프로퍼티에 대해 반환 타입이 다른 게터가 두 개**가 되는 순간 리플렉션 기반 라이브러리(MyBatis, Jackson 등)에서 조용히 폭발할 수 있다.
- 컴파일러는 이걸 못 잡아준다. IDE도 경고를 안 준다. 리플렉션으로 프로퍼티를 순회하는 시점에서야, 그것도 그 코드 경로를 실제로 타야만 드러난다.
- `Boolean` 필드에 커스텀 `is-` 게터를 얹고 싶다면, Lombok의 자동 게터를 `@Getter(AccessLevel.NONE)`으로 끄거나, 아예 필드명을 다르게 해서 프로퍼티 자체를 분리하는 게 안전하다. 이 프로젝트에서는 그냥 커스텀 게터를 없애고 Lombok 게터 하나로 통일하는 쪽을 택했다 — 굳이 두 가지 표현을 유지할 이유가 없었기 때문.
