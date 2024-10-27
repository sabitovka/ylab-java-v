package io.sabitovka.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ErrorDto {
    private int internalCode;
    private int status;
    private String error;
    private String message;
//    @JsonFormat()
//    private LocalDateTime timestamp = LocalDateTime.now();
}
