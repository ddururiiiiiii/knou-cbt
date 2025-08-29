package knou.cbt.domain.department.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class DepartmentBaseRequest {

    @NotBlank(message = "학과명은 필수 입력 값입니다.")
    @Size(max = 50, message = "학과명은 최대 50자까지 가능합니다.")
    protected String departmentName;
}
