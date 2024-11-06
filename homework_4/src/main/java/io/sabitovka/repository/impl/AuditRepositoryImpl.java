package io.sabitovka.repository.impl;

import io.sabitovka.model.AuditRecord;
import io.sabitovka.persistence.JdbcTemplate;
import io.sabitovka.persistence.PersistenceRepository;
import io.sabitovka.repository.AuditRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий аудита действий пользователя. Реализует интерфейс {@link AuditRepository}
 */
@Repository
public class AuditRepositoryImpl extends PersistenceRepository<Long, AuditRecord> implements AuditRepository {
    public AuditRepositoryImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, null, AuditRecord.class);
    }

    @Override
    public Optional<AuditRecord> findById(Long id) {
        throw new UnsupportedOperationException("Операция не поддерживается");
    }

    @Override
    public List<AuditRecord> findAll() {
        throw new UnsupportedOperationException("Операция не поддерживается");
    }

    @Override
    public boolean existsById(Long id) {
        throw new UnsupportedOperationException("Операция не поддерживается");
    }

    @Override
    public boolean update(AuditRecord obj) {
        throw new UnsupportedOperationException("Операция не поддерживается");
    }

    @Override
    public boolean deleteById(Long id) {
        throw new UnsupportedOperationException("Операция не поддерживается");
    }
}
