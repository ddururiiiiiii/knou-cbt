package knou.cbt.domain.student;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface StudentMapper {

    List<Student> allStudentList(@Param("offset") int offset, @Param("limit") int limit);

    int countStudents();

    Optional<Student> findByLoginId(@Param("loginId") String loginId);

    void join(@Param("student") Student student);

    Student findById(@Param("studentId") String studentId);

    void update(@Param("studentId") String studentId, @Param("student")Student student);

    String idCheck(@Param("studentId") String studentId);

}
