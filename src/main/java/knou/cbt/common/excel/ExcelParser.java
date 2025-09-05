package knou.cbt.common.excel;

import knou.cbt.domain.examquestion.dto.ExamQuestionRequest;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelParser {

    public static List<ExamQuestionRequest> parse(MultipartFile file, boolean withExamId) {
        List<ExamQuestionRequest> questions = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            int rowNum = 0;
            while (rows.hasNext()) {
                Row row = rows.next();
                if (rowNum++ == 0) continue; // 헤더 skip

                Long examId = null;
                int colIdx = 0;

                if (withExamId) {
                    examId = (long) row.getCell(colIdx++).getNumericCellValue();
                }

                ExamQuestionRequest q = new ExamQuestionRequest();
                if (withExamId) {
                    q.setExamId((long) row.getCell(colIdx++).getNumericCellValue());
                }
                q.setQuestionNo((int) row.getCell(colIdx++).getNumericCellValue());
                q.setQuestionText(getCellValue(row.getCell(colIdx++)));
                q.setOption1(getCellValue(row.getCell(colIdx++)));
                q.setOption2(getCellValue(row.getCell(colIdx++)));
                q.setOption3(getCellValue(row.getCell(colIdx++)));
                q.setOption4(getCellValue(row.getCell(colIdx++)));
                q.setAnswers(getCellValue(row.getCell(colIdx++)));

                questions.add(q);
            }
        } catch (Exception e) {
            throw new RuntimeException("엑셀 파싱 실패", e);
        }

        return questions;
    }


    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}
