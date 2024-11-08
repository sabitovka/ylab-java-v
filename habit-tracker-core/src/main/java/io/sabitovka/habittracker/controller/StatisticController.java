package io.sabitovka.habittracker.controller;

import io.sabitovka.auditlogging.annotation.Loggable;
import io.sabitovka.habittracker.dto.SuccessResponse;
import io.sabitovka.habittracker.dto.statistic.ReportParamsDto;
import io.sabitovka.habittracker.service.StatisticService;
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
@Loggable
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
