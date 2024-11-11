package io.sabitovka.habittracker.controller;

import io.sabitovka.auditlogging.annotation.Loggable;
import io.sabitovka.habittracker.annotation.RequiresAuthorization;
import io.sabitovka.habittracker.common.Constants;
import io.sabitovka.habittracker.dto.ErrorDto;
import io.sabitovka.habittracker.dto.SuccessResponse;
import io.sabitovka.habittracker.dto.statistic.ReportParamsDto;
import io.sabitovka.habittracker.service.StatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-контроллер для управления статисткой выполнения привычек
 */
@Tag(name = "Статистка", description = "Сервис статистики")
@RequestMapping("/api/statistic")
@RestController
@RequiredArgsConstructor
@Loggable
public class StatisticController {
    private final StatisticService statisticService;

    @Operation(summary = "Получить отчет о выполнении привычки")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Отчет о выполнении"),
            @ApiResponse(description = "Любая ошибка", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @SecurityRequirement(name = Constants.BEARER_AUTHORIZATION)
    @RequiresAuthorization
    @PostMapping("/report")
    public ResponseEntity<SuccessResponse<String>> generateReport(@RequestBody ReportParamsDto reportParamsDto) {
        String reportString = statisticService.generateHabitReportString(
                reportParamsDto.getHabitId(),
                reportParamsDto.getStartDate(),
                reportParamsDto.getEndDate()
        );

        return ResponseEntity.ok(new SuccessResponse<>(reportString));
    }
}
