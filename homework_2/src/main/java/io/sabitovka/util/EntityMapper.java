package io.sabitovka.util;

import io.sabitovka.enums.HabitFrequency;
import io.sabitovka.persistence.annotation.Column;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.sql.ResultSet;
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

                Column columnAnnotation = field.getAnnotation(Column.class);
                if (columnAnnotation != null) {
                    String columnName = columnAnnotation.name();
                    Class<?> fieldType = field.getType();

                    if (fieldType == String.class) {
                        field.set(entity, resultSet.getString(columnName));
                    } else if (fieldType == int.class || fieldType == Integer.class) {
                        field.set(entity, resultSet.getInt(columnName));
                    } else if (fieldType == long.class || fieldType == Long.class) {
                        field.set(entity, resultSet.getLong(columnName));
                    } else if (fieldType == LocalDate.class) {
                        field.set(entity, resultSet.getDate(columnName).toLocalDate());
                    } else if (fieldType == boolean.class || fieldType == Boolean.class){
                        field.set(entity, resultSet.getBoolean(columnName));
                    } else if (fieldType == HabitFrequency.class) {
                        field.set(entity, HabitFrequency.valueOf(resultSet.getString(columnName)));
                    }
                }
            }
            return entity;

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при маппинге ResultSet в сущность " + entityType.getName());
        }
    }
}
