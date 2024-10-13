package io.sabitovka.repository;

import io.sabitovka.exception.EntityAlreadyExistsException;
import io.sabitovka.exception.EntityConstraintException;
import io.sabitovka.exception.EntityNotFoundException;
import io.sabitovka.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserRepositoryImplTest {

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryImpl();
    }

    @Test
    public void create_whenEmailIsUnique_shouldCreateSuccessfully() {
        User user = new User("mock", "mock@example.com", "password123");
        User createdUser = userRepository.create(user);

        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getId()).isEqualTo(1L);
        assertThat(createdUser.getName()).isEqualTo("mock");
        assertThat(createdUser.getEmail()).isEqualTo("mock@example.com");
        assertThat(createdUser).isNotSameAs(user);
    }

    @Test
    public void create_whenUserExistsById_shouldThrowException() {
        User firstUser = new User("mock", "mock@example.com", "password123");
        userRepository.create(firstUser);

        User user = new User(1L,"mock", "mock@example.com", "password123", false, true);
        assertThatThrownBy(() -> userRepository.create(user))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessageContaining("Пользователь уже существует в системе");
    }

    @Test
    public void create_whenUserExistsByEmail_shouldThrowException() {
        User firstUser = new User("mock", "mock@example.com", "password123");
        userRepository.create(firstUser);

        User user = new User("mock", "mock@example.com", "password123");
        assertThatThrownBy(() -> userRepository.create(user))
                .isInstanceOf(EntityConstraintException.class)
                .hasMessageContaining("Нарушение индекса Email");
    }

    @Test
    public void create_whenUserArgumentIsNull_shouldThrowException() {
        assertThatThrownBy(() -> userRepository.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User is null");
    }

    @Test
    public void findById_whenUserExists_shouldReturnSuccessfully() {
        User firstUser = new User("mock", "mock@example.com", "password123");
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
    public void findById_whenUserIsNotExist_shouldReturnNullOfOptional() {
        Optional<User> foundUser = userRepository.findById(1L);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.isEmpty()).isTrue();
    }

    @Test
    public void findById_whenArgumentIsNull_shouldReturnNullOfOptional() {
        Optional<User> foundUser = userRepository.findById(null);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.isEmpty()).isTrue();
    }

    @Test
    public void findAll_whenUsersExist_shouldReturnNonEmptyList() {
        User user1 = new User("mock1", "mock1@example.com", "password1231");
        userRepository.create(user1);
        User user2 = new User("mock2", "mock2@example.com", "password1232");
        userRepository.create(user2);
        User user3 = new User("mock3", "mock3@example.com", "password1233");
        userRepository.create(user3);

        List<User> users = userRepository.findAll();

        assertThat(users).isNotNull();
        assertThat(users).isInstanceOf(ArrayList.class);
        assertThat(users.size()).isEqualTo(3);
        assertThat(users.get(0)).isNotSameAs(user1);
    }

    @Test
    public void findAll_whenUsersNotExist_shouldReturnEmptyList() {
        List<User> users = userRepository.findAll();

        assertThat(users).isNotNull();
        assertThat(users).isInstanceOf(ArrayList.class);
        assertThat(users.isEmpty()).isTrue();
    }

    @Test
    public void update_whenUserArgumentIsNull_shouldThrowException() {
        assertThatThrownBy(() -> userRepository.update(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User is null");
    }

    @Test
    public void update_whenUserIsNotExist_shouldThrowException() {
        User user1 = new User(1L, "mock1", "mock1@example.com", "password1231", false, true);
        User user2 = new User( "mock1", "mock1@example.com", "password1231");

        assertThatThrownBy(() -> userRepository.update(user1))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Пользователь не найден в системе");

        assertThatThrownBy(() -> userRepository.update(user2))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Пользователь не найден в системе");
    }

    @Test
    public void update_whenUpdatingEmailThatIsExist_shouldThrowException() {
        User user1 = new User("mock", "mock@example.com", "password123");
        userRepository.create(user1);
        User user2 = new User( "mock1", "mock1@example.com", "password1231");
        User createdUser2 = userRepository.create(user2);

        createdUser2.setEmail("mock@example.com");

        assertThatThrownBy(() -> userRepository.update(createdUser2))
                .isInstanceOf(EntityConstraintException.class)
                .hasMessageContaining("Нарушение индекса Email");
    }

    @Test
    public void update_whenUpdatingIsCorrect_shouldReturnTrue() {
        User user1 = new User("mock", "mock@example.com", "password123");
        userRepository.create(user1);
        User user2 = new User( "mock1", "mock1@example.com", "password1231");
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
    public void deleteById_whenArgumentIsNull_shouldReturnFalse() {
        boolean result = userRepository.deleteById(null);

        assertThat(result).isFalse();
    }

    @Test
    public void deleteById_whenUserIsNotExist_shouldReturnFalse() {
        boolean result = userRepository.deleteById(1L);

        assertThat(result).isFalse();
    }

    @Test
    public void deleteById_whenUserExists_shouldReturnSuccessfully() {
        User user1 = new User("mock", "mock@example.com", "password123");
        User created = userRepository.create(user1);

        boolean result = userRepository.deleteById(created.getId());
        Optional<User> deletedUser = userRepository.findById(created.getId());

        assertThat(result).isTrue();
        assertThat(deletedUser.isEmpty()).isTrue();
    }

    @Test
    public void findUserByEmail_whenEmailIsExist_shouldReturnSuccessfully() {
        User user1 = new User("mock", "mock@example.com", "password123");
        userRepository.create(user1);
        User user2 = new User( "mock1", "mock1@example.com", "password1231");
        User createdUser2 = userRepository.create(user2);

        Optional<User> foundUser = userRepository.findUserByEmail("mock1@example.com");

        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser.get().getEmail()).isEqualTo("mock1@example.com");
        assertThat(foundUser.get()).isEqualTo(createdUser2);
    }

    @Test
    public void findById_whenUserWasUpdated_shouldReturnSuccessfully() {
        User user1 = new User("mock", "mock@example.com", "password123");
        userRepository.create(user1);

        User user2 = new User("mock1", "mock1@example.com", "password1231");
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
