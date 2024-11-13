package io.sabitovka.repository;

import io.sabitovka.config.DataSourceConfig;
import io.sabitovka.config.TestConfig;
import io.sabitovka.enums.HabitFrequency;
import io.sabitovka.model.FulfilledHabit;
import io.sabitovka.model.Habit;
import io.sabitovka.model.User;
import io.sabitovka.persistence.JdbcTemplate;
import io.sabitovka.util.MigrationManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Тест репозитория FulfilledHabitRepositoryImpl")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestConfig.class })
class FulfilledHabitRepositoryImplTest {
    private final static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:17.0")
            .withDatabaseName("testdb")
            .withUsername("junit")
            .withPassword("password");

    @DynamicPropertySource
    private static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("db.url", () -> postgresContainer.getJdbcUrl() + "&currentSchema=model");
        registry.add("db.username", postgresContainer::getUsername);
        registry.add("db.password", postgresContainer::getPassword);
    }

    @Autowired
    private FulfilledHabitRepository fulfilledHabitRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSourceConfig dataSourceConfig;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HabitRepository habitRepository;

    private FulfilledHabit fulfilledHabit1;
    private FulfilledHabit fulfilledHabit2;

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
        MigrationManager.migrate(dataSourceConfig.getDataSource().getConnection(), "db/changelog/test-changelog.xml", "model", "public");

        User user = new User(null, "mock", "mock@example.ru", "pass", false, true);
        userRepository.create(user);

        Habit habit = new Habit(null, "mock", "", HabitFrequency.DAILY, LocalDate.now(), true, user.getId());
        habitRepository.create(habit);
        fulfilledHabit1 = new FulfilledHabit(1L, habit.getId(), LocalDate.now());
        fulfilledHabit2 = new FulfilledHabit(2L, habit.getId(), LocalDate.now().minusDays(1));
    }

    @AfterEach
    public void tearDown() {
        jdbcTemplate.executeUpdate("TRUNCATE TABLE fulfilled_habits, habits, users RESTART IDENTITY CASCADE");
    }

    @Test
    @DisplayName("[create] Должен успешно создать выполненную привычку")
    public void createShouldCreateFulfilledHabitSuccessfully() {
        FulfilledHabit createdHabit = fulfilledHabitRepository.create(fulfilledHabit1);

        assertThat(fulfilledHabitRepository.existsById(createdHabit.getId())).isTrue();
        assertThat(createdHabit.getHabitId()).isEqualTo(fulfilledHabit1.getHabitId());
    }

    @Test
    @DisplayName("[create] Когда выполненная привычка = null, должен выбросить исключение")
    public void createWhenFulfilledHabitIsNullShouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> fulfilledHabitRepository.create(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("[create] Когда выполненная привычка уже есть в системе, должен выбросить исключение")
    public void createWhenFulfilledHabitAlreadyExistsShouldThrowEntityAlreadyExistsException() {
        fulfilledHabitRepository.create(fulfilledHabit1);

        assertThatThrownBy(() -> fulfilledHabitRepository.create(fulfilledHabit1))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("[findById] Когда выполненная привычка уже есть, должен вернуть ее")
    public void findByIdWhenFulfilledHabitExistsShouldReturnFulfilledHabit() {
        FulfilledHabit createdHabit = fulfilledHabitRepository.create(fulfilledHabit1);

        Optional<FulfilledHabit> foundHabit = fulfilledHabitRepository.findById(createdHabit.getId());

        assertThat(foundHabit.isPresent()).isTrue();
        assertThat(foundHabit.get().getHabitId()).isEqualTo(fulfilledHabit1.getHabitId());
    }

    @Test
    @DisplayName("[findById] Когда выполненной привычки нет, должен вернуть пустой Optional")
    public void findByIdWhenFulfilledHabitDoesNotExistShouldReturnEmptyOptional() {
        Optional<FulfilledHabit> foundHabit = fulfilledHabitRepository.findById(999L);

        assertThat(foundHabit.isPresent()).isFalse();
    }

    @Test
    @DisplayName("[findAll] Должен успешно вернуть выполненные привычки")
    public void findAllShouldReturnAllFulfilledHabits() {
        fulfilledHabitRepository.create(fulfilledHabit1);
        fulfilledHabitRepository.create(fulfilledHabit2);

        List<FulfilledHabit> habits = fulfilledHabitRepository.findAll();

        assertThat(habits).hasSize(2);
    }

    @Test
    @DisplayName("[update] Не поддерживается. Ожидается исключение")
    public void updateShouldThrowUnsupportedOperationException() {
        assertThatThrownBy(() -> fulfilledHabitRepository.update(fulfilledHabit1))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Обновление привычки не поддерживается");
    }

    @Test
    @DisplayName("[deleteById] Когда выполненная привычка имеется, должен удалить успешно")
    public void deleteByIdWhenFulfilledHabitExistsShouldDeleteSuccessfully() {
        FulfilledHabit createdHabit = fulfilledHabitRepository.create(fulfilledHabit1);

        boolean deleted = fulfilledHabitRepository.deleteById(createdHabit.getId());

        assertThat(deleted).isTrue();
        assertThat(fulfilledHabitRepository.findById(createdHabit.getId()).isPresent()).isFalse();
    }

    @Test
    @DisplayName("[deleteById] Когда выполненной привычки нет, должен вернуть false")
    public void deleteByIdWhenFulfilledHabitDoesNotExistShouldReturnFalse() {
        boolean deleted = fulfilledHabitRepository.deleteById(999L);

        assertThat(deleted).isFalse();
    }

}