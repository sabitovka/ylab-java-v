package io.sabitovka.controller;

import io.sabitovka.dto.SuccessResponse;
import io.sabitovka.dto.statistic.ReportParamsDto;
import io.sabitovka.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<?> generateReport(@RequestBody ReportParamsDto reportParamsDto) {
        String reportString = statisticService.generateHabitReportString(
                reportParamsDto.getHabitId(),
                reportParamsDto.getStartDate(),
                reportParamsDto.getEndDate()
        );

        return ResponseEntity.ok(new SuccessResponse<>(reportString));
    }
}
