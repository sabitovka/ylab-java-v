package io.sabitovka.auth.util;

import io.sabitovka.util.YamlPropertySourceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
public final class PasswordHasher {
    @Value("${security.salt}")
    private String salt;
    private MessageDigest getMessageDigest() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes());
            return md;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String hash(String password) {
        MessageDigest md = getMessageDigest();
        byte[] hashedPassword = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hashedPassword);
    }

    public boolean verify(String inputPassword, String storedPassword) {
        MessageDigest md = getMessageDigest();
        byte[] hashedInputPassword = md.digest(inputPassword.getBytes());
        String newHash = Base64.getEncoder().encodeToString(hashedInputPassword);
        return storedPassword.equals(newHash);
    }
}
