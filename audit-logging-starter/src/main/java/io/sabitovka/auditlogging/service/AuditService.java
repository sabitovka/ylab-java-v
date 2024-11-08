package io.sabitovka.auditlogging.service;

import io.sabitovka.auditlogging.model.AuditRecord;

/**
 * Интерфейс сервиса для сохранения действий пользователя
 */
public interface AuditService {
    /**
     * Выполняет сохранение действий пользователя в БД
     * @param auditRecord - Информация о действиях пользователя
     */
    void saveAudit(AuditRecord auditRecord);
}
