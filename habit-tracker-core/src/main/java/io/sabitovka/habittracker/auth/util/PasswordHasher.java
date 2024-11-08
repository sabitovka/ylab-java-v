package io.sabitovka.habittracker.auth.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Хэшер паролей
 */
@Component
public final class PasswordHasher {
    @Value("${security.salt}")
    private String salt;

    /**
     * Выполняет хеширование переданного пароля (любой строки)
     *
     * @param password Исходный пароль
     * @return Захешированый пароль
     */
    public String hash(String password) {
        MessageDigest md = getMessageDigest();
        byte[] hashedPassword = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hashedPassword);
    }

    /**
     * Выполняет верификацию введенного и хранимого паролей
     *
     * @param inputPassword Введенный пароль
     * @param storedPassword Хранимый пароль (хешированный)
     * @return `true` если пароли совпадают. `false` - иначе
     */
    public boolean verify(String inputPassword, String storedPassword) {
        MessageDigest md = getMessageDigest();
        byte[] hashedInputPassword = md.digest(inputPassword.getBytes());
        String newHash = Base64.getEncoder().encodeToString(hashedInputPassword);
        return storedPassword.equals(newHash);
    }

    private MessageDigest getMessageDigest() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes());
            return md;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
