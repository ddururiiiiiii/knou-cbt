package knou.cbt.domain.student;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
public class UpdateForm {

    private String nickname;
    private String password;

}
