package knou.cbt.domain.student;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
public class Student {

    private String studentId;
    private String nickname;
    private String password;
    private String joinDate;
    private String lastUpdateDate;
    private String UseYn;

}
