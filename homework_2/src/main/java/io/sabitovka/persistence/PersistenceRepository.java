package io.sabitovka.persistence;

import io.sabitovka.persistence.annotation.Column;
import io.sabitovka.persistence.annotation.Table;
import io.sabitovka.persistence.rowmapper.RowMapper;
import io.sabitovka.repository.BaseRepository;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public abstract class PersistenceRepository<I, M> implements BaseRepository<I, M> {
    protected final JdbcTemplate jdbcTemplate;
    protected final RowMapper<M> rowMapper;
    private final Class<M> modelClass;

    protected PersistenceRepository(JdbcTemplate jdbcTemplate, RowMapper<M> rowMapper, Class<M> modelClass) {
        this.jdbcTemplate = jdbcTemplate;
        this.modelClass = modelClass;
        this.rowMapper = rowMapper;
    }

    private String getEntityTableName() {
        Table annotation = modelClass.getAnnotation(Table.class);
        return annotation.name();
    }

    @Override
    public M create(M obj) {
        String tableName = getEntityTableName();
        Map<String, Object> columns = new HashMap<>();

        Field[] fields = modelClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                try {
                    Object value = field.get(obj);
                    if (value != null) {
                        columns.put(column.name(), value);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Ошибка при доступе к полю " + field.getName(), e);
                }
            }
        }

        String sql = "insert into %s (%s) values (%s)".formatted(
                tableName,
                String.join(", ", columns.keySet()),
                columns.values().stream().map(o -> "?").collect(Collectors.joining(", "))
        );

        List<Long> generatedKeys = jdbcTemplate.executeInsert(sql, columns.values().toArray());

        if (!generatedKeys.isEmpty()) {
            Long generatedId = generatedKeys.get(0);

            try {
                Field idField = modelClass.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(obj, generatedId);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Ошибка при установке сгенерированного ID", e);
            }
        }

        return obj;
    }

    @Override
    public Optional<M> findById(I id) {
        String sql = "select * from %s where id = ?".formatted(getEntityTableName());
        return Optional.of(jdbcTemplate.queryForObject(sql, rowMapper, id));
    }

    @Override
    public List<M> findAll() {
        String sql = "select * from %s".formatted(getEntityTableName());
        return jdbcTemplate.queryForList(sql, rowMapper);
    }

    @Override
    public boolean existsById(I id) {
        return findById(id).isPresent();
    }

    @Override
    public boolean update(M obj) {
        String tableName = getEntityTableName();
        Field[] fields = modelClass.getDeclaredFields();

        Map<String, Object> columns = new HashMap<>();
        Object idValue = null;

        for (Field field : fields) {
            field.setAccessible(true);

            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                try {
                    Object value = field.get(obj);
                    if (value != null) {
                        if (column.name().equals("id")) {
                            idValue = value;
                        } else {
                            columns.put(column.name(), value);
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Ошибка при доступе к полю " + field.getName(), e);
                }
            }
        }

        if (idValue == null) {
            throw new IllegalArgumentException("ID объекта не может быть null");
        }

        String sql = "UPDATE %s SET %s WHERE id = ?".formatted(
                tableName,
                columns.values().stream().map(name -> name + " = ?").collect(Collectors.joining(", "))
        );

        columns.values().add(idValue);

        int rowsAffected = jdbcTemplate.executeUpdate(sql, columns.values());
        return rowsAffected > 0;
    }

    @Override
    public boolean deleteById(I id) {
        String sql = "delete from %s where id = ?".formatted(getEntityTableName());
        return jdbcTemplate.executeUpdate(sql, id) > 0;
    }
}
