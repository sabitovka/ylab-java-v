package io.sabitovka.controller;

import io.sabitovka.dto.SuccessResponse;
import io.sabitovka.dto.statistic.ReportParamsDto;
import io.sabitovka.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-контроллер для управления статисткой выполнения привычек
 */
@RequestMapping("/api/statistic")
@RestController
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;

    @PostMapping("/report")
    public SuccessResponse<String> generateReport(ReportParamsDto reportParamsDto) {
        String reportString = statisticService.generateHabitReportString(
                reportParamsDto.getHabitId(),
                reportParamsDto.getStartDate(),
                reportParamsDto.getEndDate()
        );

        return new SuccessResponse<>(reportString);
    }
}
