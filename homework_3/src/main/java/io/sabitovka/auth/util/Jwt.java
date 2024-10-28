package io.sabitovka.auth.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.experimental.UtilityClass;

import java.util.Date;
import java.util.UUID;

@UtilityClass
public class Jwt {
    private final static Algorithm algorithm = Algorithm.HMAC256("baeldung"); // TODO: 28.10.2024 Заменит на серкет
    private final static int expirationTime = 5 * 60 * 1000;

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
