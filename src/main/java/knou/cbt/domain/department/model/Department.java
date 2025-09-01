package knou.cbt.domain.department.model;

import knou.cbt.domain.common.model.UseYn;
import lombok.*;
import org.apache.catalina.User;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Department {

    private Long id;
    private String departmentName;
    private UseYn useYn;

    // 정적 팩토리 메서드
    public static Department create(Long id, String departmentName, UseYn useYn) {
        return new Department(id, departmentName, useYn);
    }
}
