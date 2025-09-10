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

                int colIdx = 0;
                ExamQuestionRequest q = new ExamQuestionRequest();

                // 시험 ID 컬럼이 있을 경우
                if (withExamId) {
                    q.setExamId((long) row.getCell(colIdx++).getNumericCellValue());
                } else {
                    colIdx++; // 시험ID 컬럼 건너뛰기
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
            case NUMERIC -> {
                double val = cell.getNumericCellValue();
                if (val == Math.floor(val)) {
                    yield String.valueOf((long) val); // 정수면 소수점 제거
                } else {
                    yield String.valueOf(val);
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}
