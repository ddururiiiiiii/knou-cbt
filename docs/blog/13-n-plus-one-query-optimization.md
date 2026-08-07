# N+1 쿼리 최적화기

## 문제

관리자 화면에서 학과 목록을 보여줄 때, 각 학과 옆에 "이 학과에 등록된 과목이 있는지"를 아이콘으로 표시해야 했다. 과목이 하나도 없는 학과는 삭제할 수 있게 해주고, 과목이 있으면 삭제를 막아야 하니 목록 화면에서부터 미리 알려줄 필요가 있었다.

처음 구현은 직관적이었다. 학과 목록을 페이지 단위로 조회한 뒤, 각 학과마다 "과목이 있는지" 쿼리를 하나씩 더 날렸다.

```java
List<DepartmentResponse> content = mapper.findAllPaged(
                offset, size, keyword, useYn
        ).stream()
        .map(dept -> {
            boolean hasSubjects = subjectMapper.existsByDepartmentId(dept.getId());
            return DepartmentResponse.of(dept, hasSubjects);
        })
        .toList();
```

목록 한 번 조회에 쿼리 1번(`findAllPaged`) + 페이지에 보이는 학과 수만큼 쿼리 N번(`existsByDepartmentId`) — 전형적인 N+1 문제였다. 페이지 크기가 20이면 학과 목록 화면 하나 여는 데 쿼리가 21번 나갔다. 데이터가 적을 땐 체감이 안 되지만, 트래픽이 늘거나 학과/과목/시험 수가 많아지면 목록 화면이 눈에 띄게 느려질 수 있는 구조였다.

## 해결 — 배치 조회로 전환

각 학과마다 개별 쿼리를 날리는 대신, 현재 페이지에 보이는 학과 ID들을 한 번에 모아서 "과목이 있는 학과 ID 목록"을 통째로 조회하도록 바꿨다.

```java
List<Department> depts = mapper.findAllPaged(offset, size, keyword, useYn);

List<Long> deptIds = depts.stream().map(Department::getId).toList();
Set<Long> deptIdsWithSubjects = deptIds.isEmpty()
        ? Set.of()
        : subjectMapper.findDepartmentIdsWithSubjects(deptIds);

List<DepartmentResponse> content = depts.stream()
        .map(dept -> DepartmentResponse.of(dept, deptIdsWithSubjects.contains(dept.getId())))
        .toList();
```

이제 목록 조회 쿼리 1번, 배치 조회 쿼리 1번 — 페이지 크기와 무관하게 항상 쿼리 2번으로 끝난다. `findDepartmentIdsWithSubjects(deptIds)`는 `IN` 절로 여러 학과 ID를 한 번에 받아서, 그중 실제로 과목이 하나라도 있는 학과 ID들만 `Set`으로 돌려준다. 이후엔 각 학과마다 이 `Set`에 포함되는지만 `contains()`로 확인하면 되니, DB에 다시 갈 필요가 없다.

빈 페이지(검색 결과가 0건)일 때 `IN ()` 같은 빈 조건으로 쿼리를 날리지 않도록 `deptIds.isEmpty()` 체크도 넣었다 — 빈 컬렉션으로 `IN` 절을 만들면 매퍼에 따라 에러가 나거나 의도치 않은 쿼리가 나갈 수 있어서, 아예 DB를 타지 않고 빈 `Set`으로 처리하는 편이 안전하다.

## 교훈

- N+1은 "목록을 조회한 뒤, 각 항목마다 추가 정보를 채워 넣는" 패턴에서 가장 흔하게 생긴다. 특히 이 프로젝트처럼 "이 항목에 하위 데이터가 있는가?" 같은 존재 여부 체크를 리스트 순회 중에 넣는 코드는 그 자체로 N+1의 강한 신호다.
- 고치는 방법은 대체로 같은 형태다 — 순회하면서 하나씩 물어보는 대신, ID를 모아서 한 번에 물어보고 메모리에서 매칭한다. 쿼리 개수가 "페이지 크기에 비례"에서 "항상 상수"로 바뀌는 게 핵심이다.
- 데이터가 적은 개발 초기에는 N+1이 있어도 체감 성능 차이가 거의 없어서 방치되기 쉽다. 그래서 코드 리뷰 시점에 "리스트를 순회하면서 매퍼/서비스를 호출하는 코드가 있는가"를 의식적으로 찾아보는 습관이 필요하다.
