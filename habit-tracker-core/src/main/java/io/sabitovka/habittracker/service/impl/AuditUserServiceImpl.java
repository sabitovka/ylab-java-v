package io.sabitovka.habittracker.service.impl;

import io.sabitovka.audit.service.AuditUserService;
import io.sabitovka.habittracker.auth.AuthInMemoryContext;
import io.sabitovka.habittracker.auth.entity.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuditUserServiceImpl implements AuditUserService {
    @Override
    public String getUsername() {
        Optional<UserDetails> userDetails = Optional.ofNullable(AuthInMemoryContext.getContext().isLoggedIn()
                ? AuthInMemoryContext.getContext().getAuthentication() : null);

        return userDetails.map(UserDetails::getUsername).orElse("Не авторизован");
    }

    @Override
    public String getIp() {
        return AuthInMemoryContext.getContext().getIp();
    }
}
