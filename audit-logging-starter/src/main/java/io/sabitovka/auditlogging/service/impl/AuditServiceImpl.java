package io.sabitovka.auditlogging.service.impl;

import io.sabitovka.auditlogging.model.AuditRecord;
import io.sabitovka.auditlogging.repository.AuditRepository;
import io.sabitovka.auditlogging.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Сервис аудита. Реализует интерфейс {@link AuditService}
 */
@RequiredArgsConstructor
@Service
public class AuditServiceImpl implements AuditService {
    private final AuditRepository auditRepository;

    @Override
    public void saveAudit(AuditRecord auditRecord) {
        auditRepository.create(auditRecord);
    }
}
