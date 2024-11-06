package io.sabitovka.controller;

import io.sabitovka.dto.habit.HabitFilterDto;
import io.sabitovka.dto.habit.HabitInfoDto;
import io.sabitovka.dto.habit.SimpleLocalDateDto;
import io.sabitovka.enums.HabitFrequency;
import io.sabitovka.service.HabitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HabitsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HabitService habitService;

    @InjectMocks
    private HabitsController habitsController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new HabitsController(habitService)).build();
    }

    @Test
    @DisplayName("POST /api/habits - Создать привычку")
    void shouldCreateHabit() throws Exception {
        HabitInfoDto createdHabit = new HabitInfoDto(
                null, "New Habit", "Description", HabitFrequency.DAILY, LocalDate.now(), true, 1L
        );

        when(habitService.createHabit(any(HabitInfoDto.class))).thenReturn(createdHabit);

        mockMvc.perform(post("/api/habits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdHabit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("New Habit"))
                .andExpect(jsonPath("$.data.description").value("Description"));
    }

    @Test
    @DisplayName("PUT /api/habits/{id} - Обновить привычку")
    void shouldUpdateHabit() throws Exception {
        Long habitId = 1L;
        HabitInfoDto createdHabit = new HabitInfoDto(
                1L, "New Habit", "Description", HabitFrequency.DAILY, LocalDate.now(), true, 1L
        );

        mockMvc.perform(put("/api/habits/{id}", habitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdHabit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Привычка обновлена"));
    }

    @Test
    @DisplayName("POST /api/habits/filter - Фильтрация привычек")
    void shouldFilterHabitsByFilters() throws Exception {
        HabitInfoDto createdHabit = new HabitInfoDto(
                null, "Filtered Habit", "Description", HabitFrequency.DAILY, LocalDate.now(), true, 1L
        );
        List<HabitInfoDto> filteredHabits = Collections.singletonList(createdHabit);

        when(habitService.getHabitsByFilters(any(HabitFilterDto.class))).thenReturn(filteredHabits);

        mockMvc.perform(post("/api/habits/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdHabit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Filtered Habit"));
    }

    @Test
    @DisplayName("GET /api/habits/user/{userId} - Получить привычки пользователя")
    void shouldReturnUserHabits() throws Exception {
        Long userId = 1L;
        HabitInfoDto userHabit = new HabitInfoDto(
                null, "User Habit", "Description", HabitFrequency.DAILY, LocalDate.now(), true, 1L
        );
        List<HabitInfoDto> userHabits = Collections.singletonList(userHabit);

        when(habitService.getAllByOwner(userId)).thenReturn(userHabits);

        mockMvc.perform(get("/api/habits/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("User Habit"));
    }

    @Test
    @DisplayName("PUT /api/habits/{id}/disable - Отключить привычку")
    void shouldDisableHabit() throws Exception {
        Long habitId = 1L;

        mockMvc.perform(put("/api/habits/{id}/disable", habitId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Привычка отключена"));
    }

    @Test
    @DisplayName("GET /api/habits/{id} - Получить привычку по ID")
    void shouldReturnHabitById() throws Exception {
        Long habitId = 1L;
        HabitInfoDto habitInfo = new HabitInfoDto(
                null, "Habit by ID", "Description", HabitFrequency.DAILY, LocalDate.now(), true, 1L
        );

        when(habitService.getHabitById(habitId)).thenReturn(habitInfo);

        mockMvc.perform(get("/api/habits/{id}", habitId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Habit by ID"))
                .andExpect(jsonPath("$.data.description").value("Description"));
    }

    @Test
    @DisplayName("DELETE /api/habits/{id} - Удалить привычку")
    void shouldDeleteHabit() throws Exception {
        Long habitId = 1L;

        mockMvc.perform(delete("/api/habits/{id}", habitId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Привычка удалена"));
    }

    @Test
    @DisplayName("POST /api/habits/{id}/fulfill - Отметить привычку как выполненную")
    void shouldFulfillHabit() throws Exception {
        Long habitId = 1L;
        SimpleLocalDateDto localDateDto = new SimpleLocalDateDto(LocalDate.now());

        mockMvc.perform(post("/api/habits/{id}/fulfill", habitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(localDateDto)))
                .andExpect(status().isOk());
    }
}

