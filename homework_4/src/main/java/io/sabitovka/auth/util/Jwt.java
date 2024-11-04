package io.sabitovka.auth.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

/**
 * Утилитарный класс для управления создания и верификации JWT токенов
 */
@Component
@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
public class Jwt {
    @Value("${security.jwt.expirationMinutes}")
    private int expirationTime;
    private final Algorithm algorithm;

    @Autowired
    public Jwt(@Value("${security.jwt.secret}") String jwtSecret, @Value("${security.jwt.expirationMinutes}") int expirationTime) {
        algorithm = Algorithm.HMAC256(jwtSecret);
        this.expirationTime = expirationTime * 60 * 1000;
    }

    /**
     * Генерирует JWT-токен. В Claims записывает переданного ID пользователя
     * @param userId ID пользователя
     * @return Сгенерированный токен
     */
    public String generate(Long userId) {
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
    public Long verifyAndGetUserId(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getClaim("userId").asLong();
        } catch (JWTVerificationException e) {
            return null;
        }
    }
}
