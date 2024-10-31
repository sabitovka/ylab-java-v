package io.sabitovka.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Long id) {
        super("Сущность с id %d не найдена в системе");
    }
}
