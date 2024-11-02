package io.sabitovka.habittracker.exception;

public class EntityConstraintException extends RuntimeException {
    public EntityConstraintException(String message) {
        super(message);
    }
}
