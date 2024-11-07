package io.sabitovka.habittracker.service;

import io.sabitovka.habittracker.model.AuditRecord;

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
