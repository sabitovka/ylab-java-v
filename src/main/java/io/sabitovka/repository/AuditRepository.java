package io.sabitovka.repository;

import io.sabitovka.model.AuditRecord;

/**
 * Репозиторий для доступа записей аудита из БД
 */
public interface AuditRepository {
    /**
     * Сохраняет новую запись аудита
     * @param auditRecord Запись аудита
     */
    AuditRecord create(AuditRecord auditRecord);
}