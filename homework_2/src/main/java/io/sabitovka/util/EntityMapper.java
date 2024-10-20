package io.sabitovka.util;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Утилитарный класс, который позволяет смапить сущность из {@link ResultSet}
 */
@UtilityClass
public class EntityMapper {
    public static <T> T mapResultSetToEntity(ResultSet resultSet, Class<T> entityType) {
        try {
            T entity = entityType.getDeclaredConstructor().newInstance();

            for (Field field : entityType.getDeclaredFields()) {
                field.setAccessible(true);

                String columnName = field.getName();
                Class<?> fieldType = field.getType();

                if (fieldType == String.class) {
                    field.set(entity, resultSet.getString(columnName));
                } else if (fieldType == int.class || fieldType == Integer.class) {
                    field.set(entity, resultSet.getInt(columnName));
                } else if (fieldType == long.class || fieldType == Long.class) {
                    field.set(entity, resultSet.getLong(columnName));
                } else if (fieldType == LocalDate.class) {
                    field.set(entity, resultSet.getDate(columnName).toLocalDate());
                }
            }
            return entity;

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при маппинге ResultSet в сущность " + entityType.getName());
        }
    }
}
