package knou.cbt.domain.department.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DepartmentUpdateRequest extends DepartmentBaseRequest {

        @Pattern(regexp = "^[YN]$")
        private String useYn;

}