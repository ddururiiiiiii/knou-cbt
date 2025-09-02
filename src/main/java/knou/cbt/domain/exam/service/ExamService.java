package knou.cbt.domain.exam.service;

import knou.cbt.common.api.PageRequest;
import knou.cbt.common.api.PageResponse;
import knou.cbt.domain.exam.dto.ExamRequest;
import knou.cbt.domain.exam.dto.ExamResponse;
import knou.cbt.domain.exam.model.ExamType;
import org.apache.ibatis.annotations.Param;

public interface ExamService {

    PageResponse<ExamResponse> listPage(
            Long departmentId,
            Long subjectId,
            ExamType examType,
            Integer year,
            String useYn,
            PageRequest pageRequest
    );

    int count(@Param("useYn") String useYn,
             @Param("departmentId") Long departmentId,
             @Param("subjectId") Long subjectId,
             @Param("examType") ExamType examType,
             @Param("year") Integer year);

    ExamResponse get(Long id);

    void create(ExamRequest req);

    void update(Long id, ExamRequest req);

    void delete(Long id);
}
