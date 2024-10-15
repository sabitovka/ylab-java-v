package io.sabitovka.util;

import io.sabitovka.common.Constants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class PasswordHasher {
    private static final String salt = Constants.SALT;

    private PasswordHasher() {}

    private static MessageDigest getMessageDigest() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes());
            return md;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String hash(String password) {
        MessageDigest md = getMessageDigest();
        byte[] hashedPassword = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hashedPassword);
    }

    public static boolean verify(String inputPassword, String storedPassword) {
        MessageDigest md = getMessageDigest();
        byte[] hashedInputPassword = md.digest(inputPassword.getBytes());
        String newHash = Base64.getEncoder().encodeToString(hashedInputPassword);
        return storedPassword.equals(newHash);
    }
}
