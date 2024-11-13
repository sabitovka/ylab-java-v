package io.sabitovka.habittracker.auth;

import io.sabitovka.habittracker.auth.entity.UserDetails;
import io.sabitovka.habittracker.enums.ErrorCode;
import io.sabitovka.habittracker.exception.ApplicationException;
import lombok.Getter;
import lombok.Setter;

/**
 * Контекст авторизации приложения
 */
public final class AuthInMemoryContext {
    private final static AuthInMemoryContext CONTEXT = new AuthInMemoryContext();

    private final ThreadLocal<UserDetails> authenticatedUser = new ThreadLocal<>();

    @Setter
    @Getter
    private String ip;

    public static synchronized AuthInMemoryContext getContext() {
        return CONTEXT;
    }

    /**
     * Возвращает текущего авторизованного пользователя или выбрасывает исключение.
     * Для безопасной проверки, перед вызовом применять метод {@link #isLoggedIn}
     *
     * @return Авторизованный пользователь
     * @throws ApplicationException если никто не авторизован
     */
    public UserDetails getAuthentication() {
        if (authenticatedUser.get() == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED, "Ошибка авторизации. Выполните вход");
        }
        return authenticatedUser.get();
    }

    /**
     * Выполняет проверку авторизации пользователя
     *
     * @return `true` если пользователь авторизован, `false` - иначе
     */
    public boolean isLoggedIn() {
        return authenticatedUser.get() != null;
    }

    /**
     * Устанавливает авторизованного пользователя в контекст
     *
     * @param user Данные авторизованного пользователя
     */
    public void setAuthentication(UserDetails user) {
        authenticatedUser.set(user);
    }
}
