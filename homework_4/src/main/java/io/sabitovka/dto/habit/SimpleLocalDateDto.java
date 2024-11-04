package io.sabitovka.dto.habit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO с простой датой
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SimpleLocalDateDto {
    LocalDate date;
}
