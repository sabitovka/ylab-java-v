package io.sabitovka.servlet.filter;

import io.sabitovka.auth.AuthInMemoryContext;
import io.sabitovka.auth.entity.UserDetails;
import io.sabitovka.factory.ServiceFactory;
import io.sabitovka.service.UserDetailsService;
import io.sabitovka.auth.util.Jwt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(filterName = "AuthFilter", value = "/api/*")
public class AuthFilter extends HttpFilter {
    private final UserDetailsService userDetailsService = ServiceFactory.getInstance().getUserDetailsService();

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
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
        return Jwt.verifyAndGetUserId(token);
    }
}
