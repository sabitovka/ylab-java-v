package io.sabitovka.exception;

public class EntityAlreadyExistsException extends RuntimeException {
    public EntityAlreadyExistsException(Long id) {
        super("Сущность уже существует в системе с id %d".formatted(id));
    }
}
