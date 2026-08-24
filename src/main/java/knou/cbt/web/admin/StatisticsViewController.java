package knou.cbt.web.admin;

import knou.cbt.domain.statistics.dto.StatisticsDashboardResponse;
import knou.cbt.domain.statistics.dto.SubjectRankingResponse;
import knou.cbt.domain.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * 통계 대시보드 (응시 트래픽, 인기 과목/시험, 콘텐츠 현황) 관리자 화면
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/statistics")
public class StatisticsViewController {

    private static final DateTimeFormatter TREND_DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd");

    private final StatisticsService statisticsService;

    @GetMapping
    public String dashboard(Model model) {
        StatisticsDashboardResponse dashboard = statisticsService.getDashboard();
        model.addAttribute("dashboard", dashboard);

        // 차트는 CSP상 인라인 스크립트를 쓸 수 없어 데이터를 CSV 문자열로 미리 만들어
        // data-* 속성에 심어두고, 별도 JS 파일(/js/admin-statistics.js)에서 읽어 그린다.
        model.addAttribute("trendLabelsCsv", dashboard.dailyTrend().stream()
                .map(d -> d.date().format(TREND_DATE_FORMAT))
                .collect(Collectors.joining(",")));
        model.addAttribute("trendCountsCsv", dashboard.dailyTrend().stream()
                .map(d -> String.valueOf(d.attemptCount()))
                .collect(Collectors.joining(",")));
        model.addAttribute("subjectLabelsCsv", dashboard.topSubjects().stream()
                .map(SubjectRankingResponse::subjectName)
                .collect(Collectors.joining(",")));
        model.addAttribute("subjectCountsCsv", dashboard.topSubjects().stream()
                .map(s -> String.valueOf(s.attemptCount()))
                .collect(Collectors.joining(",")));

        return "admin/statistics/statisticsDashboard";
    }
}
