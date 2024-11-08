package io.sabitovka.auditlogging.service;

import org.springframework.stereotype.Component;

@Component
public interface AuditUserService {
    String getUsername();
    String getIp();
}
