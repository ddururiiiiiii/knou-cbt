package knou.cbt.web.examquestion;

import jakarta.servlet.http.HttpServletResponse;
import knou.cbt.common.excel.ExcelParser;
import knou.cbt.domain.exam.dto.ExamResponse;
import knou.cbt.domain.exam.service.ExamService;
import knou.cbt.domain.examquestion.dto.ExamQuestionRequest;
import knou.cbt.domain.examquestion.dto.ExamQuestionRequestList;
import knou.cbt.domain.examquestion.dto.ExamQuestionResponse;
import knou.cbt.domain.examquestion.service.ExamQuestionService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/exams/{examId}/questions")
public class ExamQuestionViewController {

    private final ExamQuestionService examQuestionService;
    private final ExamService examService;

    /**
     * 특정 시험 문제 조회 및 수정 화면
     * @param examId
     * @param model
     * @return
     */
    @GetMapping
    public String list(@PathVariable("examId") Long examId, Model model) {
        List<ExamQuestionResponse> questions = examQuestionService.getQuestions(examId);
        ExamResponse exam = examService.get(examId);

        model.addAttribute("examId", examId);
        model.addAttribute("questions", questions);
        model.addAttribute("exam", exam);

        return "examquestion/questionManage";
    }

    /**
     * 저장 처리
     * @param examId
     * @param wrapper
     * @return
     */
    @PostMapping("/save")
    public String saveAll(@PathVariable("examId") Long examId,
                          @ModelAttribute ExamQuestionRequestList wrapper,
                          BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "입력값이 올바르지 않습니다.");
            return "redirect:/exams/{examId}/questions";
        }

        examQuestionService.saveAll(examId, wrapper.getQuestions());
        redirectAttributes.addFlashAttribute("saveSuccess", true);
        return "redirect:/exams/{examId}/questions";
    }

    /**
     * 엑셀 양식 다운로드
     * @param examId
     * @param response
     * @throws IOException
     */
    @GetMapping("/excel-template")
    public void downloadTemplate(@PathVariable("examId") Long examId, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=exam_upload_template.xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("문제양식");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("시험ID");
            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue(examId);
            header.createCell(1).setCellValue("문제번호");
            header.createCell(2).setCellValue("문제");
            header.createCell(3).setCellValue("보기1");
            header.createCell(4).setCellValue("보기2");
            header.createCell(5).setCellValue("보기3");
            header.createCell(6).setCellValue("보기4");
            header.createCell(7).setCellValue("정답");
            workbook.write(response.getOutputStream());
        }
    }

    @PostMapping("/excel-upload")
    public String uploadExcel(@PathVariable("examId") Long examId,
                              @RequestParam("file") MultipartFile file,
                              Model model) {
        boolean withExamId = (examId == 0); // examId=0 이면 "전체 업로드" 모드로 간주
        List<ExamQuestionRequest> questions = ExcelParser.parse(file, withExamId);

        if (!withExamId) {
            // 특정 시험 모드일 때는 examId 덮어쓰기
            questions.forEach(q -> q.setExamId(examId));
        }

        model.addAttribute("examId", examId);
        model.addAttribute("exam", examService.get(examId));
        model.addAttribute("questions", questions);

        return "examquestion/questionManage";
    }
}
