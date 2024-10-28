package io.sabitovka.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.experimental.UtilityClass;

import java.util.Date;
import java.util.UUID;

@UtilityClass
public class Jwt {
    private final static Algorithm algorithm = Algorithm.HMAC256("baeldung"); // TODO: 28.10.2024 Заменит на серкет
    public static String generate(String userId) {
        return JWT.create()
                .withIssuer("Habit Tracker API")
                .withSubject("Habit Tracker Client")
                .withClaim("userId", userId)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 5000L))
                .withJWTId(UUID.randomUUID()
                        .toString())
                .sign(algorithm);
    }
}
