package io.sabitovka.repository;

import io.sabitovka.config.DataSourceConfig;
import io.sabitovka.config.MainWebAppInitializer;
import io.sabitovka.enums.HabitFrequency;
import io.sabitovka.model.Habit;
import io.sabitovka.model.User;
import io.sabitovka.persistence.JdbcTemplate;
import io.sabitovka.persistence.rowmapper.HabitRowMapper;
import io.sabitovka.persistence.rowmapper.UserRowMapper;
import io.sabitovka.repository.impl.HabitRepositoryImpl;
import io.sabitovka.repository.impl.UserRepositoryImpl;
import io.sabitovka.util.MigrationManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Тест репозитория HabitRepositoryImpl")
class HabitRepositoryImplTest {
    private final static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:17.0")
            .withDatabaseName("testdb")
            .withUsername("junit")
            .withPassword("password");

    private JdbcTemplate jdbcTemplate;
    private HabitRepository habitRepository;
    private User user;
    private Habit habit1;
    private Habit habit2;

    @BeforeAll
    static void beforeAll() {
        postgresContainer.start();
    }

    @AfterAll
    static void afterAll() {
        postgresContainer.stop();
    }

    @BeforeEach
    public void setUp() throws SQLException {
        DataSourceConfig.DataSource dataSource = new DataSourceConfig.DataSource(
                postgresContainer.getJdbcUrl() + "&currentSchema=model",
                postgresContainer.getUsername(),
                postgresContainer.getPassword()
        );
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        MigrationManager.migrate(dataSource.getConnection(), "db/changelog/test-changelog.xml", "model", "public");

        habitRepository = new HabitRepositoryImpl(jdbcTemplate, new HabitRowMapper());
        UserRepository userRepository = new UserRepositoryImpl(jdbcTemplate, new UserRowMapper());
        user = new User(1L, "testUser", "test@example.com", "password", false, true);
        userRepository.create(user);
        habit1 = new Habit(1L, "Habit1", "Description1", HabitFrequency.DAILY, LocalDate.now(), true, 1L);
        habit2 = new Habit(2L, "Habit2", "Description2", HabitFrequency.WEEKLY, LocalDate.now().minusDays(5), false, 1L);
    }

    @AfterEach
    public void tearDown() {
        DataSourceConfig.DataSource dataSource = new DataSourceConfig.DataSource(
                postgresContainer.getJdbcUrl() + "&currentSchema=model",
                postgresContainer.getUsername(),
                postgresContainer.getPassword()
        );
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        jdbcTemplate.executeUpdate("TRUNCATE TABLE fulfilled_habits, habits, users RESTART IDENTITY CASCADE");
    }

    @Test
    @DisplayName("[create] Должен успешно создать привычку")
    public void createShouldCreateHabitSuccessfully() {
        Habit createdHabit = habitRepository.create(habit1);

        assertThat(habitRepository.existsById(createdHabit.getId())).isTrue();
        assertThat(createdHabit.getName()).isEqualTo(habit1.getName());
    }

    @Test
    @DisplayName("[create] Когда привычка = null, должен выбросить исключение")
    public void createWhenHabitIsNullShouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> habitRepository.create(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("[create] Когда привычка уже содержится, должен выбросить исключение")
    public void createWhenHabitAlreadyExistsShouldThrowEntityAlreadyExistsException() {
        habitRepository.create(habit1);

        assertThatThrownBy(() -> habitRepository.create(habit1))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("[create] Должен успешно вернуть все привычки")
    public void findByIdWhenHabitExistsShouldReturnHabit() {
        Habit createdHabit = habitRepository.create(habit1);

        Optional<Habit> foundHabit = habitRepository.findById(createdHabit.getId());

        assertThat(foundHabit.isPresent()).isTrue();
        assertThat(foundHabit.get().getName()).isEqualTo(habit1.getName());
    }

    @Test
    @DisplayName("[findById] Когда привычки нет, должен вернуть пустой Optional")
    public void findByIdWhenHabitDoesNotExistShouldReturnEmptyOptional() {
        Optional<Habit> foundHabit = habitRepository.findById(999L);

        assertThat(foundHabit.isPresent()).isFalse();
    }

    @Test
    @DisplayName("[findAll] Должен вернуть все привычки")
    public void findAllShouldReturnAllHabits() {
        habitRepository.create(habit1);
        habitRepository.create(habit2);

        List<Habit> habits = habitRepository.findAll();
        assertThat(habits).hasSize(2);
    }

    @Test
    @DisplayName("[update] Должен успешно обновить привычку")
    public void updateShouldUpdateHabitSuccessfully() {
        Habit createdHabit = habitRepository.create(habit1);
        createdHabit.setName("UpdatedHabit");

        habitRepository.update(createdHabit);

        Optional<Habit> updatedHabit = habitRepository.findById(createdHabit.getId());
        assertThat(updatedHabit.get().getName()).isEqualTo("UpdatedHabit");
    }

    @Test
    @DisplayName("[update] Когда привычка null, должен выбросить исключение")
    public void updateWhenHabitIsNullShouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> habitRepository.update(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("[update] Когда привычка не содержится, должен вернуть false")
    public void updateWhenHabitDoesNotExistShouldReturnFalse() {
        Habit nonExistentHabit = new Habit(999L, "NonExistent", "NonExistent", HabitFrequency.DAILY,  LocalDate.now(), true, 1L);

        boolean result = habitRepository.update(nonExistentHabit);
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("[deleteById] Должен успешно удалить по id")
    public void deleteByIdWhenHabitExistsShouldDeleteSuccessfully() {
        Habit createdHabit = habitRepository.create(habit1);

        boolean deleted = habitRepository.deleteById(createdHabit.getId());

        assertThat(deleted).isTrue();
        assertThat(habitRepository.findById(createdHabit.getId()).isPresent()).isFalse();
    }

    @Test
    @DisplayName("[deleteById] Когда привычка не содержится, должен вернуть false")
    public void deleteByIdWhenHabitDoesNotExistShouldReturnFalse() {
        boolean deleted = habitRepository.deleteById(999L);

        assertThat(deleted).isFalse();
    }

    @Test
    @DisplayName("[findAllByUser] Должен вернуть список привычек пользователя")
    public void findAllByUserShouldReturnHabitsByUser() {
        habitRepository.create(habit1);
        habitRepository.create(habit2);

        List<Habit> userHabits = habitRepository.findAllByUserId(user.getId());

        assertThat(userHabits).hasSize(2);
    }

    @Test
    @DisplayName("[filterByUserAndTimeAndStatus] Должен отфильтровать корректно")
    public void filterByUserAndTimeAndStatusShouldFilterCorrectly() {
        habitRepository.create(habit1);
        habitRepository.create(habit2);

        List<Habit> filteredHabits = habitRepository.filterByUserAndTimeAndStatus(
                user.getId(), LocalDate.now().minusDays(6), LocalDate.now(), true);

        assertThat(filteredHabits).hasSize(1);
        assertThat(filteredHabits.get(0).getName()).isEqualTo(habit1.getName());
    }

    @Test
    @DisplayName("[filterByUserAndTimeAndStatus] Когда ничего не попадает под фильтр, должен вернуть пустой список")
    public void filterByUserAndTimeAndStatusWhenNoMatchesShouldReturnEmptyList() {
        habitRepository.create(habit1);
        habitRepository.create(habit2);

        List<Habit> filteredHabits = habitRepository.filterByUserAndTimeAndStatus(
                user.getId(), LocalDate.now().minusDays(1), LocalDate.now(), true);

        assertThat(filteredHabits).isNotEmpty();
    }
}
