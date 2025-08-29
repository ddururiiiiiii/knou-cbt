package knou.cbt.domain.department.model;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Department {

    private Long id;
    private String departmentName;
    private String useYn;

    // 정적 팩토리 메서드
    public static Department create(Long id, String departmentName, String useYn) {
        return new Department(id, departmentName, useYn);
    }
}
