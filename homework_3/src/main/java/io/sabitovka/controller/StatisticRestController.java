package io.sabitovka.controller;

import io.sabitovka.dto.statistic.ReportParamsDto;
import io.sabitovka.factory.ServiceFactory;
import io.sabitovka.service.StatisticService;
import io.sabitovka.servlet.RestController;
import io.sabitovka.servlet.annotation.PostMapping;
import io.sabitovka.servlet.annotation.RequestMapping;
import io.sabitovka.servlet.util.SuccessResponse;

@RequestMapping("/statistic")
public class StatisticRestController implements RestController {
    StatisticService statisticService = ServiceFactory.getInstance().getStatisticService();

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
