package knou.cbt.domain.student;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService{

    private final StudentMapper studentMapper;
    @Override
    public List<Student> allStudentList(int page, int size) {
        int offset = (page - 1) * size;
        return studentMapper.allStudentList(offset, size);
    }

    @Override
    public int countStudents() {
        return studentMapper.countStudents();
    }

    @Override
    public void join(Student student) {
        studentMapper.join(student);
    }

    @Override
    public Student findById(String studentId) {
        return studentMapper.findById(studentId);
    }

    @Override
    public void update(String studentId, UpdateForm updateForm) {
        Student findStudent = findById(studentId);
        findStudent.setNickname(updateForm.getNickname());
        findStudent.setPassword(updateForm.getPassword());
        studentMapper.update(studentId, findStudent);
    }

    @Override
    public String idCheck(String studentId) {
        return studentMapper.idCheck(studentId);
    }
}
