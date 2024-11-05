package io.sabitovka.util.mapper;

import io.sabitovka.dto.habit.HabitInfoDto;
import io.sabitovka.model.Habit;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HabitMapper {
    HabitMapper INSTANCE = Mappers.getMapper(HabitMapper.class);

    Habit habitInfoDtoToHabit(HabitInfoDto habitInfoDto);
    HabitInfoDto habitToHabitInfoDto(Habit habit);
}
