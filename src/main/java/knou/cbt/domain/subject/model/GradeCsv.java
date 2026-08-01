package knou.cbt.domain.subject.model;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 과목의 학년은 하나의 과목이 여러 학년에서 개설될 수 있어(예: 1학년+3학년) DB에는
 * "1,3" 같은 콤마구분 문자열로 저장한다(exam_question.answers와 동일한 패턴).
 * 화면/폼과 주고받을 땐 List&lt;Integer&gt;로 다루기 위한 변환 유틸.
 */
public final class GradeCsv {

    private GradeCsv() {
    }

    public static String toCsv(List<Integer> grades) {
        if (grades == null || grades.isEmpty()) {
            return null;
        }
        return grades.stream()
                .distinct()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    public static List<Integer> fromCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }
        return java.util.Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(Integer::parseInt)
                .sorted()
                .toList();
    }

    public static String toDisplay(String csv) {
        List<Integer> grades = fromCsv(csv);
        if (grades.isEmpty()) {
            return "-";
        }
        return grades.stream()
                .map(g -> g + "학년")
                .collect(Collectors.joining(", "));
    }
}
