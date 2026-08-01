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
        int rowNum = 0;

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();


            while (rows.hasNext()) {
                Row row = rows.next();
                if (rowNum++ == 0) continue; // 헤더 skip

                int colIdx = 0;
                ExamQuestionRequest q = new ExamQuestionRequest();

                // 시험 ID 컬럼이 있을 경우
                if (withExamId) {
                    q.setExamId((long) getNumericValue(row.getCell(colIdx++)));
                } else {
                    colIdx++; // 시험ID 컬럼 건너뛰기
                }

                q.setQuestionNo((int) getNumericValue(row.getCell(colIdx++)));
                q.setQuestionText(getCellValue(row.getCell(colIdx++)));
                q.setOption1(getCellValue(row.getCell(colIdx++)));
                q.setOption2(getCellValue(row.getCell(colIdx++)));
                q.setOption3(getCellValue(row.getCell(colIdx++)));
                q.setOption4(getCellValue(row.getCell(colIdx++)));
                q.setAnswers(getCellValue(row.getCell(colIdx++)));

                questions.add(q);
            }
        } catch (Exception e) {
            throw new RuntimeException("엑셀 파싱 실패 (행: " + rowNum + ")", e);
        }

        return questions;
    }

    // 파싱 단계에서는 형식을 엄격히 검사하지 않는다 — 숫자가 비었거나 잘못돼도 0으로 채워서
    // 일단 화면(수정 가능한 표)에 올리고, 실제 검증은 저장 시(questionManage.html의 검증 로직)에 맡긴다.
    private static double getNumericValue(Cell cell) {
        if (cell == null) {
            return 0;
        }
        return switch (cell.getCellType()) {
            case NUMERIC -> cell.getNumericCellValue();
            case STRING -> {
                try {
                    yield Double.parseDouble(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    yield 0;
                }
            }
            default -> 0;
        };
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
