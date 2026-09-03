package knou.cbt.web.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import knou.cbt.domain.statistics.dto.MemberStatsResponse;
import knou.cbt.domain.statistics.dto.StatisticsDashboardResponse;
import knou.cbt.domain.statistics.dto.SubjectRankingResponse;
import knou.cbt.domain.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 통계 대시보드 (응시 트래픽, 인기 과목/시험, 콘텐츠 현황) 관리자 화면
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/statistics")
public class StatisticsViewController {

    private static final DateTimeFormatter TREND_DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd");

    private final StatisticsService statisticsService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public String dashboard(Model model) {
        StatisticsDashboardResponse dashboard = statisticsService.getDashboard();
        model.addAttribute("dashboard", dashboard);

        MemberStatsResponse memberStats = statisticsService.getMemberStats();
        model.addAttribute("memberStats", memberStats);

        // 차트는 CSP상 인라인 스크립트를 쓸 수 없어 데이터를 data-* 속성에 심어두고
        // 별도 JS 파일(/js/admin-statistics.js)에서 읽어 그린다. 라벨은 과목명처럼 콤마가
        // 포함될 수 있는 텍스트라 CSV로 이어붙이면 값이 깨지므로(예: "성,사랑,사회" 과목이
        // 3개로 쪼개짐) JSON 배열로 직렬화한다. 숫자 값은 콤마가 나올 수 없어 CSV 그대로 둔다.
        model.addAttribute("trendLabelsJson", toJson(dashboard.dailyTrend().stream()
                .map(d -> d.date().format(TREND_DATE_FORMAT))
                .toList()));
        model.addAttribute("trendCountsCsv", dashboard.dailyTrend().stream()
                .map(d -> String.valueOf(d.attemptCount()))
                .collect(Collectors.joining(",")));
        model.addAttribute("subjectLabelsJson", toJson(dashboard.topSubjects().stream()
                .map(SubjectRankingResponse::subjectName)
                .toList()));
        model.addAttribute("subjectCountsCsv", dashboard.topSubjects().stream()
                .map(s -> String.valueOf(s.attemptCount()))
                .collect(Collectors.joining(",")));
        model.addAttribute("signupTrendLabelsJson", toJson(memberStats.signupTrend().stream()
                .map(d -> d.date().format(TREND_DATE_FORMAT))
                .toList()));
        model.addAttribute("signupTrendCountsCsv", memberStats.signupTrend().stream()
                .map(d -> String.valueOf(d.signupCount()))
                .collect(Collectors.joining(",")));

        return "admin/statistics/statisticsDashboard";
    }

    private String toJson(List<String> values) {
        try {
            return objectMapper.writeValueAsString(values);
        } catch (Exception e) {
            log.warn("통계 차트 라벨 직렬화 실패", e);
            return "[]";
        }
    }
}
