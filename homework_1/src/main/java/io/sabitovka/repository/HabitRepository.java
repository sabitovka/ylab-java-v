package io.sabitovka.repository;

import io.sabitovka.model.Habit;
import io.sabitovka.model.User;

import java.time.LocalDate;
import java.util.List;

public interface HabitRepository extends BaseRepository<Long, Habit> {
    List<Habit> findAllByUser(User owner);
    List<Habit> filterByUserAndTimeAndStatus(User owner, LocalDate startDate, LocalDate endDate, Boolean isActive);
}
