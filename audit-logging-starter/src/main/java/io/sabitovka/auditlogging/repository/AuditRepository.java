package io.sabitovka.auditlogging.repository;

import io.sabitovka.auditlogging.model.AuditRecord;

/**
 * Репозиторий для доступа записей аудита из БД
 */
public interface AuditRepository {
    /**
     * Сохраняет новую запись аудита
     * @param auditRecord Запись аудита для сохранения
     */
    void create(AuditRecord auditRecord);
}
