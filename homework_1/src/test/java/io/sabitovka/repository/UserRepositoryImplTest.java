package io.sabitovka.repository;

import io.sabitovka.exception.EntityAlreadyExistsException;
import io.sabitovka.exception.EntityConstraintException;
import io.sabitovka.exception.EntityNotFoundException;
import io.sabitovka.model.User;
import io.sabitovka.repository.impl.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Тест репозитория UserRepositoryImpl")
class UserRepositoryImplTest {
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryImpl();
    }

    @Test
    @DisplayName("[create] Должен успешно создать пользователя")
    public void createWhenEmailIsUniqueShouldCreateSuccessfully() {
        User user = new User(null, "mock", "mock@example.com", "password123", false, true);
        User createdUser = userRepository.create(user);

        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getId()).isEqualTo(1L);
        assertThat(createdUser.getName()).isEqualTo("mock");
        assertThat(createdUser.getEmail()).isEqualTo("mock@example.com");
        assertThat(createdUser).isNotSameAs(user);
    }

    @Test
    @DisplayName("[create] Когда пользователь содержится по id, должен выбросить исключение")
    public void createWhenUserExistsByIdShouldThrowException() {
        User firstUser = new User(null, "mock", "mock@example.com", "password123", false, true);
        userRepository.create(firstUser);

        User user = new User(1L,"mock", "mock@example.com", "password123", false, true);

        assertThatThrownBy(() -> userRepository.create(user))
                .isInstanceOf(EntityAlreadyExistsException.class);
    }

    @Test
    @DisplayName("[create] Когда пользователь содержится по email, должен выбросить исключение")
    public void createWhenUserExistsByEmailShouldThrowException() {
        User firstUser = new User(null, "mock", "mock@example.com", "password123", false, true);
        userRepository.create(firstUser);

        User user = new User(null, "mock", "mock@example.com", "password123", false, true);

        assertThatThrownBy(() -> userRepository.create(user))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("[create] Когда пользователь null, должен выбросить исключение")
    public void createWhenUserArgumentIsNullShouldThrowException() {
        assertThatThrownBy(() -> userRepository.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User is null");
    }

    @Test
    @DisplayName("[findById] Должен вернуть успешно")
    public void findByIdWhenUserExistsShouldReturnSuccessfully() {
        User firstUser = new User(null, "mock", "mock@example.com", "password123", false, true);
        userRepository.create(firstUser);

        Optional<User> foundUser = userRepository.findById(1L);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser.get()).isNotSameAs(firstUser);
        assertThat(foundUser.get().getId()).isEqualTo(1L);
        assertThat(foundUser.get().getName()).isEqualTo("mock");
        assertThat(foundUser.get().getEmail()).isEqualTo("mock@example.com");
        assertThat(foundUser.get().getPassword()).isEqualTo("password123");
    }

    @Test
    @DisplayName("[findById] Когда пользователь не содержится, должен вернуть Optional.empty()")
    public void findByIdWhenUserIsNotExistShouldReturnNullOfOptional() {
        Optional<User> foundUser = userRepository.findById(1L);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("[findById] Когда аргумент null должен вернуть Optional.empty()")
    public void findByIdWhenArgumentIsNullShouldReturnNullOfOptional() {
        Optional<User> foundUser = userRepository.findById(null);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("[findAll] Должен вернуть успешно")
    public void findAllWhenUsersExistShouldReturnNonEmptyList() {
        User user1 = new User(null, "mock1", "mock1@example.com", "password1231", false, true);
        userRepository.create(user1);
        User user2 = new User(null, "mock2", "mock2@example.com", "password1232", false, true);
        userRepository.create(user2);
        User user3 = new User(null, "mock3", "mock3@example.com", "password1233", false, true);
        userRepository.create(user3);

        List<User> users = userRepository.findAll();

        assertThat(users).isNotNull();
        assertThat(users).isInstanceOf(List.class);
        assertThat(users.size()).isEqualTo(3);
        assertThat(users.get(0)).isNotSameAs(user1);
    }

    @Test
    @DisplayName("[findAll] Должен вернуть пустой список")
    public void findAllWhenUsersNotExistShouldReturnEmptyList() {
        List<User> users = userRepository.findAll();

        assertThat(users).isNotNull();
        assertThat(users).isInstanceOf(List.class);
        assertThat(users.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("[update] Когда аргумент null, должен выбросить исключение")
    public void updateWhenUserArgumentIsNullShouldThrowException() {
        assertThatThrownBy(() -> userRepository.update(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User is null");
    }

    @Test
    @DisplayName("[update] Когда пользователя нет, должен выбросить исключение")
    public void updateWhenUserIsNotExistShouldThrowException() {
        User user1 = new User(1L, "mock1", "mock1@example.com", "password1231", false, true);
        User user2 = new User(null, "mock1", "mock1@example.com", "password1231", false, true);

        assertThatThrownBy(() -> userRepository.update(user1))
                .isInstanceOf(EntityNotFoundException.class);

        assertThatThrownBy(() -> userRepository.update(user2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("[update] Когда есть email у другого пользователя, должен выбросить исключение")
    public void updateWhenUpdatingEmailThatIsExistShouldThrowException() {
        User user1 = new User(null, "mock", "mock@example.com", "password123", false, true);
        userRepository.create(user1);
        User user2 = new User(null, "mock1", "mock1@example.com", "password1231", false, true);
        User createdUser2 = userRepository.create(user2);

        createdUser2.setEmail("mock@example.com");

        assertThatThrownBy(() -> userRepository.update(createdUser2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Нарушение индекса Email");
    }

    @Test
    @DisplayName("[update] Должен успешно обновить")
    public void updateWhenUpdatingIsCorrectShouldReturnTrue() {
        User user1 = new User(null, "mock", "mock@example.com", "password123", false, true);
        userRepository.create(user1);
        User user2 = new User(null, "mock1", "mock1@example.com", "password1231", false, true);
        User createdUser2 = userRepository.create(user2);
        createdUser2.setName("mock");
        createdUser2.setEmail("mock2@example.com");
        createdUser2.setPassword("123password");

        boolean result = userRepository.update(createdUser2);
        Optional<User> user2Updated = userRepository.findById(2L);

        assertThat(result).isTrue();
        assertThat(user2Updated).isNotNull();
        assertThat(user2Updated.isPresent()).isTrue();
        assertThat(user2Updated.get().getName()).isEqualTo("mock");
        assertThat(user2Updated.get().getEmail()).isEqualTo("mock2@example.com");
        assertThat(user2Updated.get().getPassword()).isEqualTo("123password");
    }

    @Test
    @DisplayName("[deleteById] Когда аргумент null, должен вернуть false")
    public void deleteByIdWhenArgumentIsNullShouldReturnFalse() {
        boolean result = userRepository.deleteById(null);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("[deleteById] Когда пользователя нет, должен вернуть false")
    public void deleteByIdWhenUserIsNotExistShouldReturnFalse() {
        boolean result = userRepository.deleteById(1L);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("[deleteById] Должен удалить пользователя и вернуть true")
    public void deleteByIdWhenUserExistsShouldReturnSuccessfully() {
        User user1 = new User(null, "mock", "mock@example.com", "password123", false, true);
        User created = userRepository.create(user1);

        boolean result = userRepository.deleteById(created.getId());
        Optional<User> deletedUser = userRepository.findById(created.getId());

        assertThat(result).isTrue();
        assertThat(deletedUser.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("[findUserByEmail] Должен вернуть пользователя")
    public void findUserByEmailWhenEmailIsExistShouldReturnSuccessfully() {
        User user1 = new User(null, "mock", "mock@example.com", "password123", true, false);
        userRepository.create(user1);
        User user2 = new User(null, "mock1", "mock1@example.com", "password1231", true, false);
        User createdUser2 = userRepository.create(user2);

        Optional<User> foundUser = userRepository.findUserByEmail("mock1@example.com");

        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser.get().getEmail()).isEqualTo("mock1@example.com");
        assertThat(foundUser.get()).isEqualTo(createdUser2);
    }

    @Test
    @DisplayName("[findUserByEmail] Когда пользователь обновил email, должен вернуть по новому email")
    public void findByIdWhenUserWasUpdatedShouldReturnSuccessfully() {
        User user1 = new User(null, "mock", "mock@example.com", "password123", false, true);
        userRepository.create(user1);

        User user2 = new User(null, "mock1", "mock1@example.com", "password1231", false, true);
        User createdUser2 = userRepository.create(user2);

        createdUser2.setName("mock");
        createdUser2.setEmail("mock2@example.com");
        createdUser2.setPassword("123password");
        userRepository.update(createdUser2);

        Optional<User> foundUser = userRepository.findUserByEmail("mock2@example.com");

        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser.get().getEmail()).isEqualTo("mock2@example.com");
        assertThat(foundUser.get().getName()).isEqualTo("mock");
        assertThat(foundUser.get().getPassword()).isEqualTo("123password");
        assertThat(foundUser.get()).isEqualTo(createdUser2);
    }
}
