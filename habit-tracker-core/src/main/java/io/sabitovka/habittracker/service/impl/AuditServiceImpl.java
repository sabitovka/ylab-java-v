package io.sabitovka.habittracker.service.impl;

import io.sabitovka.habittracker.model.AuditRecord;
import io.sabitovka.habittracker.repository.AuditRepository;
import io.sabitovka.habittracker.service.AuditService;
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
