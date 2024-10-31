package io.sabitovka.persistence;

import io.sabitovka.persistence.annotation.Column;
import io.sabitovka.persistence.annotation.Table;
import io.sabitovka.persistence.rowmapper.RowMapper;
import io.sabitovka.repository.BaseRepository;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Универсальный репозиторий для выполнения базовых CRUD операций над сущностями
 * @param <I> Идентификатор сущности
 * @param <M> Класс сущности
 */
public abstract class PersistenceRepository<I, M> implements BaseRepository<I, M> {
    protected final JdbcTemplate jdbcTemplate;
    protected final RowMapper<M> rowMapper;
    private final Class<M> modelClass;

    protected PersistenceRepository(JdbcTemplate jdbcTemplate, RowMapper<M> rowMapper, Class<M> modelClass) {
        this.jdbcTemplate = jdbcTemplate;
        this.modelClass = modelClass;
        this.rowMapper = rowMapper;
    }

    /**
     * Позволяет получить название таблицы сущности через аннотацию {@link Table}
     * @return Название таблицы
     */
    private String getEntityTableName() {
        Table annotation = modelClass.getAnnotation(Table.class);
        return annotation.name();
    }

    /**
     * Метод для создание записи сущности в БД
     * @param obj Объект сущности, который нужно сохранить.
     * @return Сохраненный объект сущности с обновленным ID
     */
    @Override
    public M create(M obj) {
        if (obj == null) {
            throw new IllegalArgumentException();
        }

        String tableName = getEntityTableName();
        Map<String, Object> columns = new HashMap<>();

        Field[] fields = modelClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

            Column column = field.getAnnotation(Column.class);
            if (column == null) {
                continue;
            }
            try {
                Object value = field.get(obj);
                if (value == null) {
                    continue;
                }
                columns.put(column.name(), value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Ошибка при доступе к полю " + field.getName(), e);
            }
        }

        String sql = "insert into %s (%s) values (%s)".formatted(
                tableName,
                String.join(", ", columns.keySet()),
                columns.keySet().stream().map(o -> "?").collect(Collectors.joining(", "))
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

    /**
     * Позволяет получить сущность по ID
     * @param id Идентификатор сущности, которую нужно найти.
     * @return Полученная из БД сущность
     */
    @Override
    public Optional<M> findById(I id) {
        String sql = "select * from %s where id = ?".formatted(getEntityTableName());
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, id));
    }

    /**
     * Позволяет получить всех сущностей из БД
     * @return Список сущностей
     */
    @Override
    public List<M> findAll() {
        String sql = "select * from %s".formatted(getEntityTableName());
        return jdbcTemplate.queryForList(sql, rowMapper);
    }

    /**
     * Проверяет, существует ли сущность в БД
     * @param id Идентификатор сущности, которую необходимо проверить
     * @return {@code true} если существует {@code false} иначе
     */
    @Override
    public boolean existsById(I id) {
        return findById(id).isPresent();
    }

    /**
     * Выполняет обновление данных сущности
     * @param obj Объект сущности с обновлёнными данными.
     * @return {@code true} если удалось обновить {@code false} иначе
     */
    @Override
    public boolean update(M obj) {
        if (obj == null) {
            throw new IllegalArgumentException();
        }

        String tableName = getEntityTableName();
        Field[] fields = modelClass.getDeclaredFields();

        Map<String, Object> columns = new HashMap<>();
        Object idValue = null;

        for (Field field : fields) {
            field.setAccessible(true);

            Column column = field.getAnnotation(Column.class);
            if (column == null) {
                continue;
            }
            try {
                Object value = field.get(obj);
                if (value == null) {
                    continue;
                }
                if (column.name().equals("id")) {
                    idValue = value;
                } else {
                    columns.put(column.name(), value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Ошибка при доступе к полю " + field.getName(), e);
            }
        }

        if (idValue == null) {
            throw new IllegalArgumentException("ID объекта не может быть null");
        }

        String sql = "UPDATE %s SET %s WHERE id = ?".formatted(
                tableName,
                columns.keySet().stream().map(name -> name + " = ?").collect(Collectors.joining(", "))
        );

        List<Object> columnValues = new ArrayList<>(columns.values());
        columnValues.add(idValue);

        int rowsAffected = jdbcTemplate.executeUpdate(sql, columnValues.toArray());
        return rowsAffected > 0;
    }

    /**
     * Удаляет сущность по ID
     * @param id Идентификатор сущности, которую нужно удалить.
     * @return {@code true} если удалось удалить {@code false} иначе
     */
    @Override
    public boolean deleteById(I id) {
        String sql = "delete from %s where id = ?".formatted(getEntityTableName());
        return jdbcTemplate.executeUpdate(sql, id) > 0;
    }
}
