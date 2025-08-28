package knou.cbt.web.exam;

import knou.cbt.domain.department.DepartmentServiceImpl;
import knou.cbt.domain.exam.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/exam")
public class ExamController {

    private final ExamServiceImpl examService;
    private final DepartmentServiceImpl departmentService;

    /**
     * ® 시험 목록 조회
     * @param page
     * @param size
     * @param departmentId
     * @param subjectId
     * @param year
     * @param semester
     * @param category
     * @return
     */
    @GetMapping
    public String allExamList(Model model
        , @RequestParam(defaultValue = "1") int page
        , @RequestParam(defaultValue = "10") int size
        , @RequestParam(required = false) String departmentId
        , @RequestParam(required = false) String subjectId
        , @RequestParam(required = false) String year
        , @RequestParam(required = false) String semester
        , @RequestParam(required = false) String category){

        SearchCriteria searchCriteria = new SearchCriteria(departmentId, subjectId, year, semester, category);
        List<ExamInfo> exams = examService.allExamList(page, size, searchCriteria);

        int totalExams = examService.countExams(page, size, searchCriteria);
        int totalPages = (int) Math.ceil((double) totalExams / size);
        model.addAttribute("exams", exams);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalExams", totalExams);
        return "exam/allExamList";
    }

    /**
     * 시험 목록 조회 (검색조건)
     * @param page
     * @param size
     * @param departmentId
     * @param subjectId
     * @param year
     * @param semester
     * @param category
     * @return
     */
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> allExamListForSearch(
         @RequestParam(defaultValue = "1") int page
       , @RequestParam(defaultValue = "10") int size
       , @RequestParam(required = false) String departmentId
       , @RequestParam(required = false) String subjectId
       , @RequestParam(required = false) String year
       , @RequestParam(required = false) String semester
       , @RequestParam(required = false) String category) {

        SearchCriteria searchCriteria = new SearchCriteria(departmentId, subjectId, year, semester, category);
        List<ExamInfo> exams = examService.allExamList(page, size, searchCriteria);
        int totalExams = examService.countExams(page, size, searchCriteria);
        int totalPages = (int) Math.ceil((double) totalExams / size);

        Map<String, Object> result = new HashMap<>();
        result.put("exams", exams);
        result.put("currentPage", page);
        result.put("totalExams", totalExams);
        result.put("totalPages", totalPages);

        return ResponseEntity.ok(result);
    }

    /**
     * 시험 단건 조회
     * @param examId
     * @param model
     * @return
     */
    @GetMapping("/{examId}")
    public String book(@PathVariable String examId, Model model){
        List<ExamDetail> examQuestions = examService.findByExamId(examId);
        model.addAttribute("examQuestions", examQuestions);
        return "exam/exam";
    }
    @PostMapping("/saveExamResult")
    public String saveExamResult(@RequestBody Map<String, Object> examResult){

        Map<String, String> param1 = (Map<String, String>) examResult.get("param1");
        String examId = (String) param1.get("examId");
        String studentId = (String) param1.get("studentId");
        String takenTime = (String) param1.get("takenTime");

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
        String formattedDate = now.format(formatter);

        Map<Integer, Integer> param2 = (Map<Integer, Integer>) examResult.get("param2");
        for (Map.Entry<Integer, Integer> entry : param2.entrySet()) {
            int key = Integer.parseInt(String.valueOf(entry.getKey()));
            int value = Integer.parseInt(String.valueOf(entry.getValue()));
            InputAnswer inputAnswer = new InputAnswer();
            inputAnswer.setQuestionNo(key);
            inputAnswer.setInputAnswer(value);
            examService.saveExamInput(examId, studentId, takenTime, formattedDate, inputAnswer);
        }
        return "redirect:exam/examResult";
    }

}
