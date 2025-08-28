package knou.cbt.domain.student;

import java.util.List;

public interface StudentService {

    List<Student> allStudentList(int page, int size);

    int countStudents();

    void join(Student student);

    Student findById(String studentId);

    void update(String studentId, UpdateForm updateForm);

    String idCheck(String studentId);


}
