package io.sabitovka.auth.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.sabitovka.common.Constants;
import lombok.experimental.UtilityClass;

import java.util.Date;
import java.util.UUID;

/**
 * Утилитарный класс для управления создания и верификации JWT токенов
 */
@UtilityClass
public class Jwt {
    private final static Algorithm algorithm = Algorithm.HMAC256(Constants.JWT_SECRET);
    private final static int expirationTime = 5 * 60 * 1000;

    /**
     * Генерирует JWT-токен. В Claims записывает переданного ID пользователя
     * @param userId ID пользователя
     * @return Сгенерированный токен
     */
    public static String generate(Long userId) {
        return JWT.create()
                .withIssuer("Habit Tracker API")
                .withSubject("Habit Tracker Client")
                .withClaim("userId", userId)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .withJWTId(UUID.randomUUID()
                        .toString())
                .sign(algorithm);
    }

    /**
     * Верифицирует полученный токен от пользователя. В любом случае неудачной верификации возвращает null
     * Если токен валидный, возвращает пользователя
     * @param token Полученный от пользователя токен
     * @return ID пользователя или null если токен не валидный
     */
    public static Long verifyAndGetUserId(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getClaim("userId").asLong();
        } catch (JWTVerificationException e) {
            return null;
        }
    }
}
