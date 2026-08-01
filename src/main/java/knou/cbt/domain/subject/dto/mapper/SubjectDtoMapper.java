package knou.cbt.domain.subject.dto.mapper;

import knou.cbt.domain.common.model.UseYn;
import knou.cbt.domain.subject.dto.SubjectRequest;
import knou.cbt.domain.subject.model.GradeCsv;
import knou.cbt.domain.subject.model.Subject;

public class SubjectDtoMapper {

    private SubjectDtoMapper(){
        // 유틸 클래스이므로 인스턴스화 방지
    }

    // DTO -> Entity
    public static Subject fromCreateRequest(SubjectRequest request){
        return Subject.create(null, request.getSubjectName(),
                request.getSubjectCategory(), request.getDepartmentId(),
                GradeCsv.toCsv(request.getGrades()), request.getSemester(), UseYn.Y);
    }

    public static Subject fromUpdateRequest(Long id, SubjectRequest request){
        return Subject.create(id, request.getSubjectName(),
                request.getSubjectCategory(), request.getDepartmentId(),
                GradeCsv.toCsv(request.getGrades()), request.getSemester(), request.getUseYn());
    }

}
