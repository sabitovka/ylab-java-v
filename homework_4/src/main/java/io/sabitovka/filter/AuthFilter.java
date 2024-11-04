package io.sabitovka.filter;

import io.sabitovka.auth.AuthInMemoryContext;
import io.sabitovka.auth.entity.UserDetails;
import io.sabitovka.auth.util.Jwt;
import io.sabitovka.service.UserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Фильтр для проверки авторизации пользователя.
 * Если передан Bearer токен, то выполняет его валидацию и авторизацию в системе
 * Если токен не валидный или вообще не был передан, фильтр пропускает дальнейшее выполнение,
 * оставляя право методы принять решение, обрабатывать запрос без авторизации или нет
 */
@RequiredArgsConstructor
@Component("authFilter")
public class AuthFilter extends HttpFilter {
    private final UserDetailsService userDetailsService;
    private final Jwt jwt;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        AuthInMemoryContext.getContext().setIp(req.getRemoteAddr());

        String authHeader = req.getHeader("Authorization");

        if (authHeader == null || authHeader.isBlank()) {
            chain.doFilter(req, res);
            return;
        }

        Long userId = checkAuthorization(authHeader);
        if (userId != null) {
            UserDetails userDetails = userDetailsService.findUserById(userId);
            AuthInMemoryContext.getContext().setAuthentication(userDetails);
        }

        chain.doFilter(req, res);
        AuthInMemoryContext.getContext().setAuthentication(null);
    }

    private Long checkAuthorization(String authHeader) {
        if (!authHeader.startsWith("Bearer "))
            return null;

        String token = authHeader.substring(7);
        return jwt.verifyAndGetUserId(token);
    }
}
