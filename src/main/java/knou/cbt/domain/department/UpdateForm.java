package knou.cbt.domain.department;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
public class UpdateForm {

    private String departmentName;
    private String useYn;

}
