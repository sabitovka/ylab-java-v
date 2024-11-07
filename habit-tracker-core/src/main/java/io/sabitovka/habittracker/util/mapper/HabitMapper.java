package io.sabitovka.habittracker.util.mapper;

import io.sabitovka.habittracker.dto.habit.HabitInfoDto;
import io.sabitovka.habittracker.model.Habit;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface HabitMapper {
    Habit habitInfoDtoToHabit(HabitInfoDto habitInfoDto);
    HabitInfoDto habitToHabitInfoDto(Habit habit);
}
