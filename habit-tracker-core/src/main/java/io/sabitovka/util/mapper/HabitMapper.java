package io.sabitovka.util.mapper;

import io.sabitovka.dto.habit.HabitInfoDto;
import io.sabitovka.model.Habit;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface HabitMapper {
    Habit habitInfoDtoToHabit(HabitInfoDto habitInfoDto);
    HabitInfoDto habitToHabitInfoDto(Habit habit);
}
