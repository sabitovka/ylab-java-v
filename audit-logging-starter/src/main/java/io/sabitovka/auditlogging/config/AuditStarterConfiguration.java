package io.sabitovka.auditlogging.config;

import io.sabitovka.auditlogging.aspect.AuditAspect;
import io.sabitovka.auditlogging.repository.AuditRepository;
import io.sabitovka.auditlogging.repository.impl.AuditRepositoryImpl;
import io.sabitovka.auditlogging.service.AuditService;
import io.sabitovka.auditlogging.service.AuditUserService;
import io.sabitovka.auditlogging.service.impl.AuditServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@RequiredArgsConstructor
public class AuditStarterConfiguration {
    private final AuditUserService auditUserService;

    @Bean
    public AuditAspect auditAspect(AuditService auditService) {
        return new AuditAspect(auditService, auditUserService);
    }

    @Bean
    public AuditService auditService(AuditRepository auditRepository) {
        return new AuditServiceImpl(auditRepository);
    }

    @Bean
    public AuditRepository auditRepository(JdbcTemplate jdbcTemplate) {
        return new AuditRepositoryImpl(jdbcTemplate);
    }
}
