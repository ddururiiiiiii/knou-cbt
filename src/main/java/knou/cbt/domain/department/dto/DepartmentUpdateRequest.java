package knou.cbt.domain.department.dto;

import knou.cbt.domain.common.model.UseYn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DepartmentUpdateRequest extends DepartmentBaseRequest {

        private UseYn useYn;

}