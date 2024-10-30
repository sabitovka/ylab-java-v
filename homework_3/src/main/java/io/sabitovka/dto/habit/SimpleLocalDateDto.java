package io.sabitovka.dto.habit;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
public class SimpleLocalDateDto {
    LocalDate date;
}
