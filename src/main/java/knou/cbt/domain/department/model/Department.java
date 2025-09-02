package knou.cbt.domain.department.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import knou.cbt.domain.common.model.UseYn;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Department {

    private Long id;
    private String departmentName;
    private UseYn useYn;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // 정적 팩토리 메서드
    public static Department create(Long id,
                                    String departmentName,
                                    UseYn useYn,
                                    LocalDateTime createdAt,
                                    LocalDateTime updatedAt
    ) {
        return new Department(id,
                             departmentName,
                             useYn,
                             createdAt,
                             updatedAt);
    }
}
